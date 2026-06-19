package com.flowerstore.websocket;

import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单通知WebSocket
 */
@Component
@ServerEndpoint("/ws/order/notification")
public class OrderNotificationWebSocket {

    // 存储所有连接的会话
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket连接建立：" + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session.getId());
        System.out.println("WebSocket连接关闭：" + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息：" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket错误：" + error.getMessage());
    }

    /**
     * 发送新订单通知
     */
    public static void sendNewOrderNotification(Map<String, Object> orderData) {
        String message = JSON.toJSONString(new HashMap<String, Object>() {{
            put("type", "NEW_ORDER");
            put("data", orderData);
            put("voiceText", "您有新的订单，请及时处理");
        }});

        for (Session session : sessions.values()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

