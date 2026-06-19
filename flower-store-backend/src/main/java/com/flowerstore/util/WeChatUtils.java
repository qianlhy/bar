package com.flowerstore.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信工具类
 */
@Component
public class WeChatUtils {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 通过code获取openid和session_key
     */
    public JSONObject code2Session(String code) {
        String url = CODE2SESSION_URL + "?appid=" + appid 
                + "&secret=" + secret 
                + "&js_code=" + code 
                + "&grant_type=authorization_code";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            return JSON.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

