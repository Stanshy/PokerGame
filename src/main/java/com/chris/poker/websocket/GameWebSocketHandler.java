package com.chris.poker.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    
    // 存儲所有連接的WebSocket session
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 玩家連接時
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        System.out.println("WebSocket連接建立: " + sessionId + ", 當前連接數: " + sessions.size());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 玩家斷線時
        String sessionId = session.getId();
        sessions.remove(sessionId);
        System.out.println("WebSocket連接關閉: " + sessionId + ", 當前連接數: " + sessions.size());
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 處理收到的訊息（目前用不到，因為是server主動推送）
        System.out.println("收到訊息: " + message.getPayload());
    }
    
    // 廣播訊息給所有連接的玩家
    public void broadcast(Object message) {
        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            System.err.println("訊息序列化失敗: " + e.getMessage());
            return;
        }
        
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                }
            } catch (IOException e) {
                System.err.println("發送訊息失敗: " + e.getMessage());
            }
        });
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket錯誤: " + exception.getMessage());
        sessions.remove(session.getId());
    }
}