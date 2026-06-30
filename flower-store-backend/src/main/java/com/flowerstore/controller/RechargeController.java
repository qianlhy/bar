package com.flowerstore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.common.PageResult;
import com.flowerstore.common.Result;
import com.flowerstore.entity.RechargeOrder;
import com.flowerstore.entity.RechargePackage;
import com.flowerstore.service.RechargeService;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员充值控制器
 */
@RestController
@RequestMapping("/recharge")
public class RechargeController {

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private JwtUtils jwtUtils;

    // ==================== 小程序端 ====================

    /**
     * 上架的充值套餐列表
     */
    @GetMapping("/packages")
    public Result<List<RechargePackage>> packages() {
        return Result.success(rechargeService.listEnabledPackages());
    }

    /**
     * 创建充值订单（微信支付能力预留，当前生成待支付订单）
     */
    @PostMapping("/create")
    public Result<RechargeOrder> create(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            Long packageId = Long.valueOf(params.get("packageId").toString());
            return Result.success(rechargeService.createOrder(userId, packageId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 我的充值记录
     */
    @GetMapping("/my")
    public Result<List<RechargeOrder>> my(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            return Result.success(rechargeService.listMyOrders(userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== 管理端 ====================

    @GetMapping("/admin/packages/page")
    public Result<PageResult<RechargePackage>> adminPackagePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<RechargePackage> page = rechargeService.pagePackages(current, size);
        return Result.success(PageResult.of(page));
    }

    @PostMapping("/admin/packages")
    public Result<String> adminAddPackage(@RequestBody RechargePackage pkg) {
        try {
            rechargeService.savePackage(pkg);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/admin/packages")
    public Result<String> adminUpdatePackage(@RequestBody RechargePackage pkg) {
        try {
            rechargeService.updatePackage(pkg);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/admin/packages/{id}")
    public Result<String> adminDeletePackage(@PathVariable Long id) {
        try {
            rechargeService.deletePackage(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/admin/orders/page")
    public Result<PageResult<RechargeOrder>> adminOrderPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<RechargeOrder> page = rechargeService.pageOrders(current, size, status);
        return Result.success(PageResult.of(page));
    }

    /**
     * 确认到账（手动确认 / 真实支付回调）
     */
    @PutMapping("/admin/orders/{id}/confirm")
    public Result<String> adminConfirm(@PathVariable Long id) {
        try {
            rechargeService.confirmOrder(id);
            return Result.success("已确认到账");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
