package com.flowerstore.controller;

import com.flowerstore.common.Result;
import com.flowerstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.flowerstore.service.SmsCodeService smsCodeService;

    /**
     * 微信小程序登录
     */
    @PostMapping("/wx/login")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, String> params) {
        try {
            String code = params.get("code");
            String nickname = params.get("nickname");
            String avatar = params.get("avatar");
            String phone = params.get("phone");
            String password = params.get("password");
            
            // 如果有手机号和密码，使用手机号密码登录
            if (phone != null && !phone.isEmpty() && password != null && !password.isEmpty()) {
                Map<String, Object> result = authService.phonePasswordLogin(phone, password);
                return Result.success(result);
            }
            
            // 否则使用微信登录
            Map<String, Object> result = authService.wxLogin(code, nickname, avatar);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 手机号密码登录
     */
    @PostMapping("/phone/login")
    public Result<Map<String, Object>> phoneLogin(@RequestBody Map<String, String> params) {
        try {
            String phone = params.get("phone");
            String password = params.get("password");
            Map<String, Object> result = authService.phonePasswordLogin(phone, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 手机号验证码登录
     */
    @PostMapping("/phone/code-login")
    public Result<Map<String, Object>> phoneCodeLogin(@RequestBody Map<String, String> params) {
        try {
            String phone = params.get("phone");
            String code = params.get("code");
            Map<String, Object> result = authService.phoneCodeLogin(phone, code);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 手机号注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        try {
            String phone = params.get("phone");
            String password = params.get("password");
            Map<String, Object> result = authService.register(phone, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public Result<Map<String, String>> sendCode(@RequestBody Map<String, String> params) {
        try {
            String phone = params.get("phone");
            if (phone == null || phone.isEmpty()) {
                return Result.error("手机号不能为空");
            }

            // 验证手机号格式
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return Result.error("手机号格式不正确");
            }

            // 发送验证码
            String code = smsCodeService.sendCode(phone);

            // 测试环境返回验证码，生产环境不应该返回
            Map<String, String> result = new HashMap<>();
            result.put("message", "验证码已发送");
            result.put("code", code); // 仅测试环境返回，生产环境删除此行
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员登录
     */
    @PostMapping("/admin/login")
    public Result<Map<String, Object>> adminLogin(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            Map<String, Object> result = authService.adminLogin(username, password);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

