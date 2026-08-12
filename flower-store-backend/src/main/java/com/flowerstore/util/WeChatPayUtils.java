package com.flowerstore.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.flowerstore.config.WeChatPayProperties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 微信支付 APIv3 工具类（微信支付公钥模式）
 *
 * 负责请求签名、应答/回调验签、回调报文 AES-256-GCM 解密，
 * 以及 JSAPI 下单、订单查询、关闭订单等接口调用。
 */
@Component
public class WeChatPayUtils {

    private static final Logger log = LoggerFactory.getLogger(WeChatPayUtils.class);

    private static final String HOST = "https://api.mch.weixin.qq.com";
    private static final String JSAPI_PATH = "/v3/pay/transactions/jsapi";
    private static final String SCHEMA = "WECHATPAY2-SHA256-RSA2048";

    @Autowired
    private WeChatPayProperties payProperties;

    @Value("${wechat.appid}")
    private String appid;

    private PrivateKey merchantPrivateKey;
    private PublicKey wechatPayPublicKey;

    @PostConstruct
    public void init() {
        if (!payProperties.isEnabled()) {
            log.info("[WeChatPay] 微信支付未启用，跳过密钥加载");
            return;
        }
        try {
            merchantPrivateKey = loadPrivateKey(readPem(payProperties.getPrivateKeyPath()));
            wechatPayPublicKey = loadPublicKey(readPem(payProperties.getPublicKeyPath()));
            log.info("[WeChatPay] 支付密钥加载成功，mchId={}", payProperties.getMchId());
        } catch (Exception e) {
            log.error("[WeChatPay] 支付密钥加载失败，微信支付将不可用", e);
        }
    }

    public boolean isReady() {
        return payProperties.isEnabled() && merchantPrivateKey != null && wechatPayPublicKey != null;
    }

    // ==================== 对外接口 ====================

    /**
     * JSAPI 下单，返回 prepay_id
     *
     * @param outTradeNo  商户订单号
     * @param description 商品描述
     * @param totalFen    金额（单位：分）
     * @param openid      支付用户的 openid
     */
    public String jsapiPrepay(String outTradeNo, String description, int totalFen, String openid) {
        JSONObject amount = new JSONObject();
        amount.put("total", totalFen);
        amount.put("currency", "CNY");

        JSONObject payer = new JSONObject();
        payer.put("openid", openid);

        JSONObject body = new JSONObject();
        body.put("appid", appid);
        body.put("mchid", payProperties.getMchId());
        body.put("description", description);
        body.put("out_trade_no", outTradeNo);
        body.put("notify_url", payProperties.getNotifyUrl());
        body.put("amount", amount);
        body.put("payer", payer);

        String resp = post(JSAPI_PATH, body.toJSONString());
        JSONObject json = JSON.parseObject(resp);
        String prepayId = json == null ? null : json.getString("prepay_id");
        if (prepayId == null || prepayId.isEmpty()) {
            throw new RuntimeException("微信下单失败：" + resp);
        }
        return prepayId;
    }

    /**
     * 生成小程序端 uni.requestPayment 所需的支付参数
     */
    public JSONObject buildMiniProgramPayParams(String prepayId) {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = randomString();
        String packageStr = "prepay_id=" + prepayId;

        String message = appid + "\n" + timeStamp + "\n" + nonceStr + "\n" + packageStr + "\n";
        String paySign = sign(message);

        JSONObject params = new JSONObject();
        params.put("appId", appid);
        params.put("timeStamp", timeStamp);
        params.put("nonceStr", nonceStr);
        params.put("package", packageStr);
        params.put("signType", "RSA");
        params.put("paySign", paySign);
        return params;
    }

    /**
     * 按商户订单号查询支付结果
     */
    public JSONObject queryByOutTradeNo(String outTradeNo) {
        String path = "/v3/pay/transactions/out-trade-no/" + outTradeNo + "?mchid=" + payProperties.getMchId();
        return JSON.parseObject(get(path));
    }

    /**
     * 关闭订单
     */
    public void closeOrder(String outTradeNo) {
        String path = "/v3/pay/transactions/out-trade-no/" + outTradeNo + "/close";
        JSONObject body = new JSONObject();
        body.put("mchid", payProperties.getMchId());
        post(path, body.toJSONString());
    }

    /**
     * 校验回调通知签名。公钥模式下应答头 Wechatpay-Serial 为微信支付公钥ID。
     */
    public boolean verifyNotify(String serial, String timestamp, String nonce, String signature, String body) {
        try {
            if (serial != null && payProperties.getPublicKeyId() != null
                    && !serial.equals(payProperties.getPublicKeyId())) {
                log.warn("[WeChatPay] 回调证书序列号不匹配，收到={}, 期望={}", serial, payProperties.getPublicKeyId());
                return false;
            }
            String message = timestamp + "\n" + nonce + "\n" + body + "\n";
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(wechatPayPublicKey);
            verifier.update(message.getBytes(StandardCharsets.UTF_8));
            return verifier.verify(Base64.getDecoder().decode(signature));
        } catch (Exception e) {
            log.error("[WeChatPay] 回调验签异常", e);
            return false;
        }
    }

    /**
     * 解密回调 resource 密文（AEAD_AES_256_GCM）
     */
    public String decryptResource(String associatedData, String nonce, String ciphertext) {
        try {
            SecretKeySpec key = new SecretKeySpec(
                    payProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key,
                    new GCMParameterSpec(128, nonce.getBytes(StandardCharsets.UTF_8)));
            if (associatedData != null && !associatedData.isEmpty()) {
                cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));
            }
            return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("回调报文解密失败", e);
        }
    }

    /**
     * 元转分
     */
    public static int yuanToFen(BigDecimal yuan) {
        if (yuan == null) {
            return 0;
        }
        return yuan.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    // ==================== HTTP ====================

    private String post(String path, String body) {
        HttpPost httpPost = new HttpPost(HOST + path);
        httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        httpPost.setHeader("Content-Type", "application/json");
        return execute(httpPost, "POST", path, body);
    }

    private String get(String path) {
        return execute(new HttpGet(HOST + path), "GET", path, "");
    }

    private String execute(HttpUriRequest request, String method, String path, String body) {
        if (!isReady()) {
            throw new RuntimeException("微信支付未正确配置，无法发起请求");
        }
        request.setHeader("Accept", "application/json");
        request.setHeader("User-Agent", "flower-store-backend");
        request.setHeader("Authorization", buildAuthorization(method, path, body));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request)) {
            int status = response.getStatusLine().getStatusCode();
            String respBody = response.getEntity() == null
                    ? "" : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (status < 200 || status >= 300) {
                log.error("[WeChatPay] 请求失败 path={}, status={}, body={}", path, status, respBody);
                throw new RuntimeException("微信支付接口返回异常：" + respBody);
            }
            return respBody;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("调用微信支付接口失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构造 Authorization 头
     */
    private String buildAuthorization(String method, String path, String body) {
        String nonceStr = randomString();
        long timestamp = System.currentTimeMillis() / 1000;
        String message = method + "\n" + path + "\n" + timestamp + "\n" + nonceStr + "\n" + body + "\n";
        String signature = sign(message);
        return SCHEMA + " "
                + "mchid=\"" + payProperties.getMchId() + "\","
                + "nonce_str=\"" + nonceStr + "\","
                + "signature=\"" + signature + "\","
                + "timestamp=\"" + timestamp + "\","
                + "serial_no=\"" + payProperties.getMerchantSerialNo() + "\"";
    }

    private String sign(String message) {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(merchantPrivateKey);
            signer.update(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signer.sign());
        } catch (Exception e) {
            throw new RuntimeException("微信支付签名失败", e);
        }
    }

    private static String randomString() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // ==================== 密钥加载 ====================

    private String readPem(String location) throws Exception {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("密钥路径未配置");
        }
        location = location.trim();
        InputStream in;
        if (location.startsWith("classpath:")) {
            in = new ClassPathResource(location.substring("classpath:".length())).getInputStream();
        } else {
            in = new FileInputStream(new File(location));
        }
        try (InputStream stream = in; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private PrivateKey loadPrivateKey(String pem) throws Exception {
        String content = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content)));
    }

    private PublicKey loadPublicKey(String pem) throws Exception {
        String content = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(content)));
    }
}
