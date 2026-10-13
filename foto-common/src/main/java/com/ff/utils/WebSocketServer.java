package com.ff.utils;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {

    // 在线用户
    public static final Map<Long, Session> ONLINE_USERS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        ONLINE_USERS.put(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        ONLINE_USERS.remove(userId);
    }

    // 给指定用户发消息
    public static void sendToUser(Long userId, String message) {
        Session session = ONLINE_USERS.get(userId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }
}