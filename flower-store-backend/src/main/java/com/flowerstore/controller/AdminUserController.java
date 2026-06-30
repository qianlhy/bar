package com.flowerstore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.flowerstore.common.PageResult;
import com.flowerstore.common.Result;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 管理后台 - 用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer status) {
        
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 条件查询
        if (phone != null && !phone.isEmpty()) {
            wrapper.like(User::getPhone, phone);
        }
        if (nickname != null && !nickname.isEmpty()) {
            wrapper.like(User::getNickname, nickname);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        
        // 隐藏敏感信息
        userPage.getRecords().forEach(user -> {
            user.setPassword(null);
            user.setOpenid(null);
        });
        
        return Result.success(PageResult.of(userPage));
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/status/{id}")
    public Result<String> updateStatus(
            @PathVariable Long id,
            @RequestBody User user) {
        User existUser = userMapper.selectById(id);
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        
        existUser.setStatus(user.getStatus());
        userMapper.updateById(existUser);
        
        return Result.success("更新成功");
    }

    /**
     * 调整用户资产（余额 / All In币 / 大师分 / 优惠券）
     * 传入字段即覆盖为绝对值，未传字段保持不变
     */
    @PutMapping("/assets/{id}")
    public Result<String> updateAssets(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (params.get("balance") != null) {
            user.setBalance(new BigDecimal(params.get("balance").toString()));
        }
        if (params.get("coins") != null) {
            user.setCoins(Integer.valueOf(params.get("coins").toString()));
        }
        if (params.get("masterScore") != null) {
            user.setMasterScore(Integer.valueOf(params.get("masterScore").toString()));
        }
        if (params.get("couponCount") != null) {
            user.setCouponCount(Integer.valueOf(params.get("couponCount").toString()));
        }
        userMapper.updateById(user);
        return Result.success("更新成功");
    }

    /**
     * 大师分排行榜（管理端，分页，按大师分降序）
     */
    @GetMapping("/rank")
    public Result<PageResult<User>> rank(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String nickname) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (nickname != null && !nickname.isEmpty()) {
            wrapper.like(User::getNickname, nickname);
        }
        wrapper.orderByDesc(User::getMasterScore);
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        userPage.getRecords().forEach(u -> {
            u.setPassword(null);
            u.setOpenid(null);
        });
        return Result.success(PageResult.of(userPage));
    }
}

