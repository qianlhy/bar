package com.flowerstore.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置（APIv3 公钥模式）
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.pay")
public class WeChatPayProperties {

    /** 是否启用微信支付 */
    private boolean enabled = false;

    /** 商户号 */
    private String mchId;

    /** APIv3密钥（32位），用于回调报文解密 */
    private String apiV3Key;

    /** 商户API证书序列号 */
    private String merchantSerialNo;

    /** 商户API私钥文件路径，支持 classpath: 前缀 */
    private String privateKeyPath;

    /** 微信支付公钥ID */
    private String publicKeyId;

    /** 微信支付公钥文件路径，支持 classpath: 前缀 */
    private String publicKeyPath;

    /** 支付结果通知地址，必须是公网可访问的 https 地址 */
    private String notifyUrl;
}
