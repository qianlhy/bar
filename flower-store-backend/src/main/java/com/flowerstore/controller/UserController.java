package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.UserMapper;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 隐藏敏感信息
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> params) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新用户信息
            if (params.containsKey("nickname")) {
                user.setNickname(params.get("nickname").toString());
            }
            if (params.containsKey("avatar")) {
                user.setAvatar(params.get("avatar").toString());
            }
            if (params.containsKey("phone")) {
                user.setPhone(params.get("phone").toString());
            }
            if (params.containsKey("gender")) {
                user.setGender(Integer.valueOf(params.get("gender").toString()));
            }

            userMapper.updateById(user);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

