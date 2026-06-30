package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.RechargeOrder;
import com.flowerstore.entity.RechargePackage;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.RechargeOrderMapper;
import com.flowerstore.mapper.RechargePackageMapper;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private UserMapper userMapper;

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
     * 创建充值订单（待支付）。
     * 微信支付能力预留：当前仅创建待支付订单，真实支付接入后在此返回支付参数，
     * 支付成功回调中调用 confirmOrder 完成到账。
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
        order.setBalance(pkg.getBalance());
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
     * 确认到账：将余额、赠送币计入用户账户（管理端手动确认，或真实支付回调调用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        RechargeOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("充值订单不存在");
        }
        if (order.getStatus() != null && order.getStatus() == 1) {
            throw new RuntimeException("该订单已到账，请勿重复操作");
        }
        User user = userMapper.selectById(order.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        BigDecimal balance = user.getBalance() == null ? BigDecimal.ZERO : user.getBalance();
        user.setBalance(balance.add(order.getBalance() == null ? BigDecimal.ZERO : order.getBalance()));
        int coins = user.getCoins() == null ? 0 : user.getCoins();
        user.setCoins(coins + (order.getGiftCoins() == null ? 0 : order.getGiftCoins()));
        userMapper.updateById(user);

        order.setStatus(1);
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        return "RC" + System.currentTimeMillis() + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
    }
}
