package com.flowerstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.entity.Admin;
import com.flowerstore.entity.PointsLog;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.AdminMapper;
import com.flowerstore.mapper.PointsLogMapper;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分服务
 * 规则：100积分=1元；单笔最多抵总金额50%；可由后台开关关闭；每日0点清零
 */
@Service
public class PointsService {

    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_CONSUME = 2;
    public static final int TYPE_REFUND = 3;
    public static final int TYPE_DAILY_RESET = 4;
    /** 每日签到 */
    public static final int TYPE_CHECKIN = 5;

    private static final int DEFAULT_CHECKIN_POINTS = 500;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointsLogMapper pointsLogMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private OperationLockService operationLockService;

    public boolean isPointsEnabled() {
        Map<String, String> config = systemConfigService.getAll();
        String enabled = config.getOrDefault("points_enabled", "1");
        return !"0".equals(enabled) && !"false".equalsIgnoreCase(enabled);
    }

    public int getPointsRate() {
        Map<String, String> config = systemConfigService.getAll();
        try {
            return Integer.parseInt(config.getOrDefault("points_rate", "100"));
        } catch (Exception e) {
            return 100;
        }
    }

    public BigDecimal getMaxRatio() {
        Map<String, String> config = systemConfigService.getAll();
        try {
            return new BigDecimal(config.getOrDefault("points_max_ratio", "0.5"));
        } catch (Exception e) {
            return new BigDecimal("0.5");
        }
    }

    /**
     * 计算订单可用积分抵扣预览
     */
    public Map<String, Object> preview(Long userId, BigDecimal orderAmount, Integer requestPoints) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.selectById(userId);
        int available = user == null || user.getPoints() == null ? 0 : user.getPoints();
        int rate = getPointsRate();
        BigDecimal maxRatio = getMaxRatio();
        boolean enabled = isPointsEnabled();

        result.put("enabled", enabled);
        result.put("availablePoints", available);
        result.put("rate", rate);
        result.put("maxRatio", maxRatio);

        if (!enabled || orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("usablePoints", 0);
            result.put("discountAmount", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            return result;
        }

        // 最多可抵金额 = 订单金额 * 50%
        BigDecimal maxDiscount = orderAmount.multiply(maxRatio).setScale(2, RoundingMode.DOWN);
        int maxByAmount = maxDiscount.multiply(new BigDecimal(rate)).intValue();
        int usable = Math.min(available, maxByAmount);
        // 抵扣金额向下取整到分（按积分整除 rate）
        usable = (usable / rate) * rate;
        if (requestPoints != null && requestPoints >= 0) {
            usable = Math.min(usable, (requestPoints / rate) * rate);
        }
        BigDecimal discount = new BigDecimal(usable)
                .divide(new BigDecimal(rate), 2, RoundingMode.DOWN);

        result.put("usablePoints", usable);
        result.put("discountAmount", discount);
        return result;
    }

    /**
     * 管理员手动录入积分（增加）。录入前需持有该用户的操作锁。
     */
    @Transactional(rollbackFor = Exception.class)
    public User addPoints(Long userId, Integer points, String remark, Long adminId) {
        if (points == null || points <= 0) {
            throw new RuntimeException("录入积分必须大于0");
        }
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        // 员工必须持有锁；超管可直接操作但仍建议加锁
        String lockKey = "user:" + userId;
        Map<String, Object> status = operationLockService.status(lockKey, adminId);
        boolean lockedByMe = Boolean.TRUE.equals(status.get("mine"));
        if (admin.getRole() != null && admin.getRole() == 3 && !lockedByMe) {
            throw new RuntimeException("请先锁定该用户再录入积分");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int before = user.getPoints() == null ? 0 : user.getPoints();
        int after = before + points;
        user.setPoints(after);
        userMapper.updateById(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangePoints(points);
        log.setBeforePoints(before);
        log.setAfterPoints(after);
        log.setType(TYPE_MANUAL);
        log.setRemark(remark == null || remark.trim().isEmpty() ? "管理员手动录入" : remark.trim());
        log.setAdminId(adminId);
        log.setAdminName(admin.getNickname() != null ? admin.getNickname() : admin.getUsername());
        pointsLogMapper.insert(log);

        user.setPassword(null);
        user.setOpenid(null);
        return user;
    }

    /**
     * 下单时扣减积分，返回实际使用的积分数和抵扣金额
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> consumeForOrder(Long userId, BigDecimal orderAmount, Integer requestPoints, Long orderId) {
        Map<String, Object> preview = preview(userId, orderAmount, requestPoints);
        int usable = (Integer) preview.get("usablePoints");
        BigDecimal discount = (BigDecimal) preview.get("discountAmount");
        Map<String, Object> result = new HashMap<>();
        result.put("pointsUsed", 0);
        result.put("pointsAmount", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        if (usable <= 0) {
            return result;
        }

        User user = userMapper.selectById(userId);
        int before = user.getPoints() == null ? 0 : user.getPoints();
        if (before < usable) {
            throw new RuntimeException("积分不足");
        }
        int after = before - usable;
        user.setPoints(after);
        userMapper.updateById(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangePoints(-usable);
        log.setBeforePoints(before);
        log.setAfterPoints(after);
        log.setType(TYPE_CONSUME);
        log.setRemark("订单消费抵扣");
        log.setOrderId(orderId);
        pointsLogMapper.insert(log);

        result.put("pointsUsed", usable);
        result.put("pointsAmount", discount);
        return result;
    }

    /**
     * 取消订单时退回积分
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundForOrder(Long userId, Integer pointsUsed, Long orderId) {
        if (pointsUsed == null || pointsUsed <= 0) {
            return;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            return;
        }
        int before = user.getPoints() == null ? 0 : user.getPoints();
        int after = before + pointsUsed;
        user.setPoints(after);
        userMapper.updateById(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangePoints(pointsUsed);
        log.setBeforePoints(before);
        log.setAfterPoints(after);
        log.setType(TYPE_REFUND);
        log.setRemark("订单取消退回积分");
        log.setOrderId(orderId);
        pointsLogMapper.insert(log);
    }

    /**
     * 每日0点清零所有用户积分
     */
    @Transactional(rollbackFor = Exception.class)
    public int resetAllPoints() {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().gt(User::getPoints, 0));
        int count = 0;
        for (User user : users) {
            int before = user.getPoints() == null ? 0 : user.getPoints();
            if (before <= 0) {
                continue;
            }
            user.setPoints(0);
            userMapper.updateById(user);

            PointsLog log = new PointsLog();
            log.setUserId(user.getId());
            log.setChangePoints(-before);
            log.setBeforePoints(before);
            log.setAfterPoints(0);
            log.setType(TYPE_DAILY_RESET);
            log.setRemark("每日0点积分清零");
            pointsLogMapper.insert(log);
            count++;
        }
        return count;
    }

    public int getCheckinPoints() {
        Map<String, String> config = systemConfigService.getAll();
        try {
            return Integer.parseInt(config.getOrDefault("checkin_points", String.valueOf(DEFAULT_CHECKIN_POINTS)));
        } catch (Exception e) {
            return DEFAULT_CHECKIN_POINTS;
        }
    }

    /**
     * 今日是否已签到
     */
    public boolean hasCheckedInToday(Long userId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        Long count = pointsLogMapper.selectCount(new LambdaQueryWrapper<PointsLog>()
                .eq(PointsLog::getUserId, userId)
                .eq(PointsLog::getType, TYPE_CHECKIN)
                .ge(PointsLog::getCreateTime, start)
                .le(PointsLog::getCreateTime, end));
        return count != null && count > 0;
    }

    public Map<String, Object> checkinStatus(Long userId) {
        Map<String, Object> result = new HashMap<>();
        int reward = getCheckinPoints();
        boolean checked = hasCheckedInToday(userId);
        User user = userMapper.selectById(userId);
        result.put("checkedIn", checked);
        result.put("rewardPoints", reward);
        result.put("points", user == null || user.getPoints() == null ? 0 : user.getPoints());
        return result;
    }

    /**
     * 每日签到：送固定积分，同一自然日仅一次
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> checkin(Long userId) {
        if (!isPointsEnabled()) {
            throw new RuntimeException("积分功能暂未开放");
        }
        if (hasCheckedInToday(userId)) {
            throw new RuntimeException("今日已签到，明天再来");
        }
        int reward = getCheckinPoints();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        int before = user.getPoints() == null ? 0 : user.getPoints();
        int after = before + reward;
        user.setPoints(after);
        userMapper.updateById(user);

        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangePoints(reward);
        log.setBeforePoints(before);
        log.setAfterPoints(after);
        log.setType(TYPE_CHECKIN);
        log.setRemark("每日签到 +" + reward + "积分");
        pointsLogMapper.insert(log);

        // 防并发双签：再查一次当天签到次数
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        Long count = pointsLogMapper.selectCount(new LambdaQueryWrapper<PointsLog>()
                .eq(PointsLog::getUserId, userId)
                .eq(PointsLog::getType, TYPE_CHECKIN)
                .ge(PointsLog::getCreateTime, start)
                .le(PointsLog::getCreateTime, end));
        if (count != null && count > 1) {
            throw new RuntimeException("今日已签到，明天再来");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rewardPoints", reward);
        result.put("points", after);
        result.put("checkedIn", true);
        return result;
    }

    public Page<PointsLog> getLogPage(Integer current, Integer size, Long userId) {
        Page<PointsLog> page = new Page<>(current, size);
        LambdaQueryWrapper<PointsLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(PointsLog::getUserId, userId);
        }
        wrapper.orderByDesc(PointsLog::getCreateTime);
        return pointsLogMapper.selectPage(page, wrapper);
    }
}
