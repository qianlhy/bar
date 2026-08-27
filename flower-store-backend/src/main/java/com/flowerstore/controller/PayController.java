package com.flowerstore.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.flowerstore.common.Result;
import com.flowerstore.service.PayService;
import com.flowerstore.util.JwtUtils;
import com.flowerstore.util.WeChatPayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付控制器
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    private static final Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayService payService;

    @Autowired
    private WeChatPayUtils weChatPayUtils;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 商品订单支付：payMethod = wechat | coins | mixed
     */
    @PostMapping("/order/{orderId}")
    public Result<JSONObject> payOrder(@RequestHeader("Authorization") String token,
                                       @PathVariable Long orderId,
                                       @RequestBody(required = false) Map<String, Object> body) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "登录已过期，请重新登录");
            }
            String payMethod = "wechat";
            if (body != null && body.get("payMethod") != null) {
                payMethod = body.get("payMethod").toString();
            }
            return Result.success(payService.payOrder(userId, orderId, payMethod));
        } catch (Exception e) {
            log.error("[Pay] 商品订单下单失败 orderId={}", orderId, e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 会员充值发起微信支付，返回小程序调起支付所需参数
     */
    @PostMapping("/recharge/{rechargeOrderId}")
    public Result<JSONObject> payRecharge(@RequestHeader("Authorization") String token,
                                          @PathVariable Long rechargeOrderId) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "登录已过期，请重新登录");
            }
            return Result.success(payService.payRecharge(userId, rechargeOrderId));
        } catch (Exception e) {
            log.error("[Pay] 充值订单下单失败 id={}", rechargeOrderId, e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付结果主动查询，用于前端支付完成后确认状态（回调延迟时的兜底）
     */
    @GetMapping("/query")
    public Result<Boolean> query(@RequestParam String outTradeNo) {
        try {
            return Result.success(payService.syncPayResult(outTradeNo));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 微信支付结果回调通知。需验签后解密报文，成功返回 200 SUCCESS。
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, String>> notify(HttpServletRequest request, @RequestBody String body) {
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String signature = request.getHeader("Wechatpay-Signature");

        try {
            if (!weChatPayUtils.verifyNotify(serial, timestamp, nonce, signature, body)) {
                log.warn("[Pay] 回调验签失败");
                return fail("签名验证失败");
            }

            JSONObject notifyJson = JSON.parseObject(body);
            JSONObject resource = notifyJson.getJSONObject("resource");
            if (resource == null) {
                return fail("报文格式错误");
            }

            String plain = weChatPayUtils.decryptResource(
                    resource.getString("associated_data"),
                    resource.getString("nonce"),
                    resource.getString("ciphertext"));
            JSONObject data = JSON.parseObject(plain);

            String tradeState = data.getString("trade_state");
            String outTradeNo = data.getString("out_trade_no");
            String transactionId = data.getString("transaction_id");

            if ("SUCCESS".equals(tradeState)) {
                payService.handlePaySuccess(outTradeNo, transactionId);
            } else {
                log.info("[Pay] 回调非成功状态 outTradeNo={}, tradeState={}", outTradeNo, tradeState);
            }

            Map<String, String> ok = new HashMap<>();
            ok.put("code", "SUCCESS");
            ok.put("message", "成功");
            return ResponseEntity.ok(ok);
        } catch (Exception e) {
            log.error("[Pay] 处理支付回调异常", e);
            return fail("处理失败");
        }
    }

    private ResponseEntity<Map<String, String>> fail(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("code", "FAIL");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
