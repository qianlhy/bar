package com.flowerstore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.common.PageResult;
import com.flowerstore.common.Result;
import com.flowerstore.entity.Admin;
import com.flowerstore.entity.PointsLog;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.AdminMapper;
import com.flowerstore.service.OperationLockService;
import com.flowerstore.service.PointsService;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 积分相关接口（管理端录入 + 小程序预览）
 */
@RestController
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @Autowired
    private OperationLockService operationLockService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 小程序：积分抵扣预览
     */
    @GetMapping("/preview")
    public Result<Map<String, Object>> preview(
            @RequestHeader("Authorization") String token,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) Integer points) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            return Result.success(pointsService.preview(userId, amount, points));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理端：手动录入积分
     */
    @PostMapping("/admin/add")
    public Result<User> addPoints(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(token);
            Long userId = Long.valueOf(params.get("userId").toString());
            Integer points = Integer.valueOf(params.get("points").toString());
            String remark = params.get("remark") != null ? params.get("remark").toString() : null;
            User user = pointsService.addPoints(userId, points, remark, adminId);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理端：积分流水
     */
    @GetMapping("/admin/logs")
    public Result<PageResult<PointsLog>> logs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId) {
        Page<PointsLog> page = pointsService.getLogPage(current, size, userId);
        return Result.success(PageResult.of(page));
    }

    /**
     * 管理端：锁定用户（录入积分前）
     */
    @PostMapping("/admin/lock/user/{userId}")
    public Result<Map<String, Object>> lockUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(token);
            Admin admin = adminMapper.selectById(adminId);
            String name = admin != null && admin.getNickname() != null
                    ? admin.getNickname() : (admin != null ? admin.getUsername() : "管理员");
            return Result.success(operationLockService.tryLock("user:" + userId, adminId, name));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理端：释放用户锁
     */
    @PostMapping("/admin/unlock/user/{userId}")
    public Result<String> unlockUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(token);
            operationLockService.unlock("user:" + userId, adminId);
            return Result.success("已释放");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理端：锁定订单（查看/处理时防冲突）
     */
    @PostMapping("/admin/lock/order/{orderId}")
    public Result<Map<String, Object>> lockOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(token);
            Admin admin = adminMapper.selectById(adminId);
            String name = admin != null && admin.getNickname() != null
                    ? admin.getNickname() : (admin != null ? admin.getUsername() : "管理员");
            return Result.success(operationLockService.tryLock("order:" + orderId, adminId, name));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理端：释放订单锁
     */
    @PostMapping("/admin/unlock/order/{orderId}")
    public Result<String> unlockOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(token);
            operationLockService.unlock("order:" + orderId, adminId);
            return Result.success("已释放");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询锁状态
     */
    @GetMapping("/admin/lock/status")
    public Result<Map<String, Object>> lockStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam String lockKey) {
        try {
            Long adminId = jwtUtils.getUserIdFromToken(token);
            return Result.success(operationLockService.status(lockKey, adminId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
