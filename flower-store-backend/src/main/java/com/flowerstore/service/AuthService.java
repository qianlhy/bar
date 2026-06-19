package com.flowerstore.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.flowerstore.entity.Admin;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.AdminMapper;
import com.flowerstore.mapper.UserMapper;
import com.flowerstore.util.JwtUtils;
import com.flowerstore.util.MD5Utils;
import com.flowerstore.util.WeChatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private WeChatUtils weChatUtils;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SmsCodeService smsCodeService;

    /**
     * 微信小程序登录
     */
    public Map<String, Object> wxLogin(String code, String nickname, String avatar) {
        // 调用微信接口获取openid
        JSONObject sessionData = weChatUtils.code2Session(code);
        if (sessionData == null || sessionData.getString("openid") == null) {
            throw new RuntimeException("微信登录失败");
        }

        String openid = sessionData.getString("openid");

        // 查询用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(wrapper);

        // 如果用户不存在，则创建新用户
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setUserType(1);
            user.setStatus(1);
            userMapper.insert(user);
        } else {
            // 更新用户信息
            user.setNickname(nickname);
            user.setAvatar(avatar);
            userMapper.updateById(user);
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return result;
    }

    /**
     * 手机号密码登录
     */
    public Map<String, Object> phonePasswordLogin(String phone, String password) {
        // 密码MD5加密
        String encryptPassword = MD5Utils.encrypt(password);

        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        wrapper.eq(User::getPassword, encryptPassword);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("手机号或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());

        return result;
    }

    /**
     * 手机号验证码登录
     */
    public Map<String, Object> phoneCodeLogin(String phone, String code) {
        // 验证验证码
        boolean isValid = smsCodeService.verifyCode(phone, code);
        if (!isValid) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);

        // 如果用户不存在，则创建新用户
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7));
            user.setUserType(1);
            user.setStatus(1);
            userMapper.insert(user);
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());

        return result;
    }

    /**
     * 手机号注册
     */
    public Map<String, Object> register(String phone, String password) {
        // 检查手机号是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User existUser = userMapper.selectOne(wrapper);

        if (existUser != null) {
            throw new RuntimeException("该手机号已注册");
        }

        // 创建新用户
        User user = new User();
        user.setPhone(phone);
        user.setPassword(MD5Utils.encrypt(password));
        user.setNickname("用户" + phone.substring(7));
        user.setUserType(1);
        user.setStatus(1);
        userMapper.insert(user);

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), "user");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("phone", user.getPhone());

        return result;
    }

    /**
     * 管理员登录
     */
    public Map<String, Object> adminLogin(String username, String password) {
        // 密码MD5加密
        String encryptPassword = MD5Utils.encrypt(password);

        // 查询管理员
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        wrapper.eq(Admin::getPassword, encryptPassword);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(admin.getId(), "admin");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminInfo", admin);

        return result;
    }
}

