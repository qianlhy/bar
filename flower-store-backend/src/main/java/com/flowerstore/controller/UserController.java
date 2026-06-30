package com.flowerstore.controller;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.flowerstore.common.Result;
import com.flowerstore.entity.User;
import com.flowerstore.mapper.UserMapper;
import com.flowerstore.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import javax.imageio.ImageIO;

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

    /**
     * 会员码二维码（PNG 图片），内容为会员标识，供店员扫码识别
     */
    @GetMapping(value = "/membercode/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] memberCode(@PathVariable Long id) {
        try {
            QrConfig config = new QrConfig(300, 300);
            config.setMargin(2);
            String content = "ALLIN-MEMBER:" + id;
            BufferedImage image = QrCodeUtil.generate(content, config);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return out.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }
}

