package org.biamn.ds2024.monitor_microservice.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.put(userId, session);
        }
    }

    public void sendNotification(UUID userId, String notificationMessage) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(notificationMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private UUID getUserIdFromSession(WebSocketSession session) {
        String userIdString = session.getUri().getQuery();
        if (userIdString != null && userIdString.startsWith("userId=")) {
            System.out.println(userIdString.substring(7));
            return UUID.fromString(userIdString.substring(7));
        }
        return null;
    }

}
