package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.RechargeOrder;
import com.flowerstore.entity.RechargePackage;
import com.flowerstore.mapper.RechargeOrderMapper;
import com.flowerstore.mapper.RechargePackageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 会员充值服务
 */
@Service
public class RechargeService {

    @Autowired
    private RechargePackageMapper packageMapper;

    @Autowired
    private RechargeOrderMapper orderMapper;

    @Autowired
    private CoinAccountService coinAccountService;

    // ==================== 套餐 ====================

    /**
     * 小程序端：上架套餐列表
     */
    public List<RechargePackage> listEnabledPackages() {
        LambdaQueryWrapper<RechargePackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargePackage::getStatus, 1);
        wrapper.orderByAsc(RechargePackage::getSort);
        return packageMapper.selectList(wrapper);
    }

    public Page<RechargePackage> pagePackages(Integer current, Integer size) {
        Page<RechargePackage> page = new Page<>(current, size);
        LambdaQueryWrapper<RechargePackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(RechargePackage::getSort).orderByDesc(RechargePackage::getId);
        return packageMapper.selectPage(page, wrapper);
    }

    public void savePackage(RechargePackage pkg) {
        packageMapper.insert(pkg);
    }

    public void updatePackage(RechargePackage pkg) {
        packageMapper.updateById(pkg);
    }

    public void deletePackage(Long id) {
        packageMapper.deleteById(id);
    }

    // ==================== 充值订单 ====================

    /**
     * 创建充值订单（待支付）
     */
    public RechargeOrder createOrder(Long userId, Long packageId) {
        RechargePackage pkg = packageMapper.selectById(packageId);
        if (pkg == null || pkg.getStatus() == null || pkg.getStatus() != 1) {
            throw new RuntimeException("充值套餐不存在或已下架");
        }
        RechargeOrder order = new RechargeOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setPackageId(packageId);
        order.setPayAmount(pkg.getPayAmount());
        // 点单支付不再使用余额；套餐 balance 置 0 兼容旧字段
        order.setBalance(BigDecimal.ZERO);
        order.setGiftCoins(pkg.getGiftCoins() == null ? 0 : pkg.getGiftCoins());
        order.setPayMethod("wechat");
        order.setStatus(0);
        orderMapper.insert(order);
        return order;
    }

    public List<RechargeOrder> listMyOrders(Long userId) {
        LambdaQueryWrapper<RechargeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeOrder::getUserId, userId);
        wrapper.orderByDesc(RechargeOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    public Page<RechargeOrder> pageOrders(Integer current, Integer size, Integer status) {
        Page<RechargeOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<RechargeOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(RechargeOrder::getStatus, status);
        }
        wrapper.orderByDesc(RechargeOrder::getCreateTime);
        return orderMapper.selectPage(page, wrapper);
    }

    /**
     * 确认到账：实付金额(元取整) + 赠送币 → 全部进入 All In 币。
     * 幂等：已到账则直接返回，不重复加币。
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        RechargeOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("充值订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            return;
        }
        // 先抢占订单状态，保证并发/重复回调只加币一次
        RechargeOrder claimed = new RechargeOrder();
        claimed.setId(order.getId());
        claimed.setStatus(1);
        LambdaQueryWrapper<RechargeOrder> cond = new LambdaQueryWrapper<>();
        cond.eq(RechargeOrder::getId, order.getId()).eq(RechargeOrder::getStatus, 0);
        int updated = orderMapper.update(claimed, cond);
        if (updated == 0) {
            return;
        }
        int payCoins = 0;
        if (order.getPayAmount() != null) {
            payCoins = order.getPayAmount().setScale(0, RoundingMode.DOWN).intValue();
        }
        int gift = order.getGiftCoins() == null ? 0 : order.getGiftCoins();
        int credit = payCoins + gift;
        if (credit > 0) {
            coinAccountService.credit(order.getUserId(), credit, "recharge", order.getOrderNo(),
                    "充值到账：实付" + payCoins + "+赠送" + gift);
        }
    }

    private String generateOrderNo() {
        return "RC" + System.currentTimeMillis() + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
    }
}
