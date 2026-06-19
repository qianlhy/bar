package com.flowerstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信验证码服务
 */
@Service
public class SmsCodeService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRE_MINUTES = 5; // 验证码有效期5分钟

    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 验证码（实际项目中不应该返回，这里为了测试方便）
     */
    public String sendCode(String phone) {
        // 检查是否频繁发送
        String key = SMS_CODE_PREFIX + phone;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        
        if (ttl != null && ttl > 240) { // 如果距离上次发送不到1分钟（5分钟-4分钟=1分钟）
            throw new RuntimeException("验证码发送过于频繁，请稍后再试");
        }

        // 生成验证码
        String code = generateCode();

        // 存储到Redis，设置5分钟过期
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 实际项目中应该调用短信服务商API发送验证码
        // 这里为了测试方便，直接返回验证码
        System.out.println("========================================");
        System.out.println("手机号：" + phone);
        System.out.println("验证码：" + code);
        System.out.println("有效期：" + CODE_EXPIRE_MINUTES + "分钟");
        System.out.println("========================================");

        return code; // 实际项目中不应该返回验证码
    }

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    public boolean verifyCode(String phone, String code) {
        String key = SMS_CODE_PREFIX + phone;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new RuntimeException("验证码已过期或不存在");
        }

        if (!storedCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }

        // 验证成功后删除验证码
        redisTemplate.delete(key);

        return true;
    }

    /**
     * 删除验证码
     * @param phone 手机号
     */
    public void deleteCode(String phone) {
        String key = SMS_CODE_PREFIX + phone;
        redisTemplate.delete(key);
    }
}

