package com.flowerstore.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.Order;
import com.flowerstore.entity.RechargeOrder;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.OrderMapper;
import com.flowerstore.mapper.RechargeOrderMapper;
import com.flowerstore.mapper.UserMapper;
import com.flowerstore.util.WeChatPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 支付服务：商品订单支持 wechat / coins / mixed，充值走微信。
 */
@Service
public class PayService {

    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    private static final String RECHARGE_PREFIX = "RC";
    public static final String METHOD_WECHAT = "wechat";
    public static final String METHOD_COINS = "coins";
    public static final String METHOD_MIXED = "mixed";

    @Autowired
    private WeChatPayUtils weChatPayUtils;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private CoinAccountService coinAccountService;

    /**
     * 商品订单支付入口
     * @param payMethod wechat | coins | mixed，默认 wechat
     */
    @Transactional(rollbackFor = Exception.class)
    public JSONObject payOrder(Long userId, Long orderId, String payMethod) {
        String method = normalizePayMethod(payMethod);
        Order order = loadPayableOrder(userId, orderId);
        BigDecimal payable = order.getActualPayment();
        if (payable == null || payable.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("订单金额异常");
        }

        if (METHOD_COINS.equals(method)) {
            return payOrderByCoins(order);
        }
        if (METHOD_MIXED.equals(method)) {
            return payOrderMixed(order);
        }
        return payOrderByWechat(order);
    }

    /** 兼容旧调用：默认微信 */
    public JSONObject payOrder(Long userId, Long orderId) {
        return payOrder(userId, orderId, METHOD_WECHAT);
    }

    private Order loadPayableOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权支付该订单");
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new RuntimeException("该订单当前不可支付");
        }
        return order;
    }

    private String normalizePayMethod(String payMethod) {
        if (payMethod == null || payMethod.trim().isEmpty()) {
            return METHOD_WECHAT;
        }
        String m = payMethod.trim().toLowerCase();
        if (METHOD_WECHAT.equals(m) || METHOD_COINS.equals(m) || METHOD_MIXED.equals(m)) {
            return m;
        }
        throw new RuntimeException("不支持的支付方式");
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject payOrderByCoins(Order order) {
        BigDecimal payable = order.getActualPayment();
        int needCoins = toCoinsCeil(payable);
        int available = coinAccountService.getCoins(order.getUserId());
        if (available < needCoins || BigDecimal.valueOf(available).compareTo(payable) < 0) {
            throw new RuntimeException("All In 币不足，请充值或选择其他支付方式");
        }

        // 条件更新抢占订单，防止并发双付
        Order lock = new Order();
        lock.setId(order.getId());
        lock.setStatus(2);
        lock.setPayTime(LocalDateTime.now());
        lock.setPaymentMethod(METHOD_COINS);
        lock.setCoinsUsed(needCoins);
        lock.setCoinsAmount(payable);
        lock.setWechatAmount(BigDecimal.ZERO);
        LambdaQueryWrapper<Order> cond = new LambdaQueryWrapper<>();
        cond.eq(Order::getId, order.getId()).eq(Order::getStatus, 1);
        int updated = orderMapper.update(lock, cond);
        if (updated == 0) {
            throw new RuntimeException("订单已支付或状态已变更");
        }

        coinAccountService.deduct(order.getUserId(), needCoins, "pay", order.getOrderNo(), "纯币支付订单");

        JSONObject result = new JSONObject();
        result.put("paid", true);
        result.put("payMethod", METHOD_COINS);
        result.put("coinsUsed", needCoins);
        result.put("wechatAmount", 0);
        return result;
    }

    public JSONObject payOrderMixed(Order order) {
        BigDecimal payable = order.getActualPayment();
        int available = coinAccountService.getCoins(order.getUserId());
        int coinsUsed = Math.min(available, toCoinsFloor(payable));
        BigDecimal coinsAmount = BigDecimal.valueOf(coinsUsed);
        BigDecimal wechatAmount = payable.subtract(coinsAmount);
        if (wechatAmount.compareTo(BigDecimal.ZERO) < 0) {
            wechatAmount = BigDecimal.ZERO;
        }

        // 币足够覆盖整单 → 走纯币
        if (wechatAmount.compareTo(BigDecimal.ZERO) <= 0
                || BigDecimal.valueOf(available).compareTo(payable) >= 0) {
            return payOrderByCoins(order);
        }

        // 币为 0 → 等价纯微信
        if (coinsUsed <= 0) {
            return payOrderByWechat(order);
        }

        // 仅记录意图，回调成功后再扣币
        Order intent = new Order();
        intent.setId(order.getId());
        intent.setPaymentMethod(METHOD_MIXED);
        intent.setCoinsUsed(coinsUsed);
        intent.setCoinsAmount(coinsAmount);
        intent.setWechatAmount(wechatAmount);
        LambdaQueryWrapper<Order> cond = new LambdaQueryWrapper<>();
        cond.eq(Order::getId, order.getId()).eq(Order::getStatus, 1);
        int updated = orderMapper.update(intent, cond);
        if (updated == 0) {
            throw new RuntimeException("订单已支付或状态已变更");
        }

        JSONObject payParams = prepay(order.getUserId(), order.getOrderNo(), "梭哈酒馆-商品订单", wechatAmount);
        payParams.put("paid", false);
        payParams.put("payMethod", METHOD_MIXED);
        payParams.put("coinsUsed", coinsUsed);
        payParams.put("wechatAmount", wechatAmount);
        return payParams;
    }

    private JSONObject payOrderByWechat(Order order) {
        BigDecimal payable = order.getActualPayment();
        Order intent = new Order();
        intent.setId(order.getId());
        intent.setPaymentMethod(METHOD_WECHAT);
        intent.setCoinsUsed(0);
        intent.setCoinsAmount(BigDecimal.ZERO);
        intent.setWechatAmount(payable);
        LambdaQueryWrapper<Order> cond = new LambdaQueryWrapper<>();
        cond.eq(Order::getId, order.getId()).eq(Order::getStatus, 1);
        orderMapper.update(intent, cond);

        JSONObject payParams = prepay(order.getUserId(), order.getOrderNo(), "梭哈酒馆-商品订单", payable);
        payParams.put("paid", false);
        payParams.put("payMethod", METHOD_WECHAT);
        payParams.put("coinsUsed", 0);
        payParams.put("wechatAmount", payable);
        return payParams;
    }

    /**
     * 充值订单下单，返回小程序调起支付的参数
     */
    public JSONObject payRecharge(Long userId, Long rechargeOrderId) {
        RechargeOrder order = rechargeOrderMapper.selectById(rechargeOrderId);
        if (order == null) {
            throw new RuntimeException("充值订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权支付该订单");
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            throw new RuntimeException("该充值订单已完成");
        }
        BigDecimal amount = order.getPayAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额异常");
        }
        return prepay(userId, order.getOrderNo(), "梭哈酒馆-会员充值", amount);
    }

    private JSONObject prepay(Long userId, String outTradeNo, String description, BigDecimal amountYuan) {
        if (!weChatPayUtils.isReady()) {
            throw new RuntimeException("微信支付未配置或配置有误，请联系管理员");
        }
        User user = userMapper.selectById(userId);
        if (user == null || user.getOpenid() == null || user.getOpenid().isEmpty()) {
            throw new RuntimeException("未获取到微信身份，请使用微信登录后再支付");
        }
        int fen = WeChatPayUtils.yuanToFen(amountYuan);
        if (fen <= 0) {
            throw new RuntimeException("支付金额异常");
        }
        String prepayId = weChatPayUtils.jsapiPrepay(outTradeNo, description, fen, user.getOpenid());
        return weChatPayUtils.buildMiniProgramPayParams(prepayId);
    }

    /**
     * 处理支付成功：按商户订单号定位业务单据并完成入账。
     * 支持重复调用（回调可能多次投递）。
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePaySuccess(String outTradeNo, String transactionId) {
        if (outTradeNo == null || outTradeNo.isEmpty()) {
            return;
        }
        if (outTradeNo.startsWith(RECHARGE_PREFIX)) {
            completeRecharge(outTradeNo, transactionId);
        } else {
            completeOrder(outTradeNo, transactionId);
        }
    }

    private void completeOrder(String outTradeNo, String transactionId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, outTradeNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            log.warn("[Pay] 支付回调未找到订单 outTradeNo={}", outTradeNo);
            return;
        }
        if (order.getStatus() == null || order.getStatus() != 1) {
            log.info("[Pay] 订单已处理，跳过。orderNo={}, status={}", outTradeNo, order.getStatus());
            return;
        }

        int coinsUsed = order.getCoinsUsed() == null ? 0 : order.getCoinsUsed();
        String method = order.getPaymentMethod();
        if (METHOD_MIXED.equals(method) && coinsUsed > 0) {
            coinAccountService.deduct(order.getUserId(), coinsUsed, "pay", order.getOrderNo(), "混合支付扣币");
        }

        Order paid = new Order();
        paid.setId(order.getId());
        paid.setStatus(2);
        paid.setPayTime(LocalDateTime.now());
        if (method == null || method.isEmpty() || "online".equals(method)) {
            paid.setPaymentMethod(METHOD_WECHAT);
        }
        LambdaQueryWrapper<Order> cond = new LambdaQueryWrapper<>();
        cond.eq(Order::getId, order.getId()).eq(Order::getStatus, 1);
        int updated = orderMapper.update(paid, cond);
        if (updated == 0) {
            // 并发下另一请求已落账：回滚本事务中的扣币，避免重复扣
            throw new RuntimeException("订单已支付，回滚本次扣币");
        }
        log.info("[Pay] 订单支付成功 orderNo={}, transactionId={}, method={}, coinsUsed={}",
                outTradeNo, transactionId, method, coinsUsed);
    }

    private void completeRecharge(String outTradeNo, String transactionId) {
        LambdaQueryWrapper<RechargeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeOrder::getOrderNo, outTradeNo);
        RechargeOrder order = rechargeOrderMapper.selectOne(wrapper);
        if (order == null) {
            log.warn("[Pay] 支付回调未找到充值订单 outTradeNo={}", outTradeNo);
            return;
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            log.info("[Pay] 充值订单已到账，跳过。orderNo={}", outTradeNo);
            return;
        }
        rechargeService.confirmOrder(order.getId());
        log.info("[Pay] 充值支付成功 orderNo={}, transactionId={}", outTradeNo, transactionId);
    }

    /**
     * 主动查询支付结果并同步入账，用于回调未到达时的兜底
     */
    public boolean syncPayResult(String outTradeNo) {
        JSONObject result = weChatPayUtils.queryByOutTradeNo(outTradeNo);
        if (result == null) {
            return false;
        }
        if ("SUCCESS".equals(result.getString("trade_state"))) {
            handlePaySuccess(outTradeNo, result.getString("transaction_id"));
            return true;
        }
        return false;
    }

    /** 1 币 = 1 元：向上取整用于纯币足额校验与扣减 */
    public static int toCoinsCeil(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return amount.setScale(0, RoundingMode.UP).intValue();
    }

    /** 混合支付优先扣币：向下取整，余数走微信 */
    public static int toCoinsFloor(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return amount.setScale(0, RoundingMode.DOWN).intValue();
    }
}
