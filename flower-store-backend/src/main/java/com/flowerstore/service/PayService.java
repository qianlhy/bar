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

import java.math.BigDecimal;

/**
 * 支付服务：负责微信支付下单与支付结果处理
 */
@Service
public class PayService {

    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    /** 充值订单号前缀，用于回调时区分业务类型 */
    private static final String RECHARGE_PREFIX = "RC";

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

    /**
     * 商品订单下单，返回小程序调起支付的参数
     */
    public JSONObject payOrder(Long userId, Long orderId) {
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
        BigDecimal amount = order.getActualPayment();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("订单金额异常");
        }
        return prepay(userId, order.getOrderNo(), "梭哈酒馆-商品订单", amount);
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
        String prepayId = weChatPayUtils.jsapiPrepay(outTradeNo, description, fen, user.getOpenid());
        return weChatPayUtils.buildMiniProgramPayParams(prepayId);
    }

    /**
     * 处理支付成功：按商户订单号定位业务单据并完成入账。
     * 支持重复调用（回调可能多次投递）。
     */
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
        if (order.getStatus() != null && order.getStatus() != 1) {
            log.info("[Pay] 订单已处理，跳过。orderNo={}, status={}", outTradeNo, order.getStatus());
            return;
        }
        orderService.updateStatus(order.getId(), 2);
        log.info("[Pay] 订单支付成功 orderNo={}, transactionId={}", outTradeNo, transactionId);
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
}
