package com.example.ksis_3.websocket;

import com.example.ksis_3.service.GameWebSocketService;
import com.example.ksis_3.service.impl.GameWebSocketServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ConnectionWebSocketHandler extends TextWebSocketHandler {

    private final Gson gson;
    private final GameWebSocketService socketService;

    public ConnectionWebSocketHandler() {
        this.gson = new Gson();
        socketService = new GameWebSocketServiceImpl();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        socketService.afterConnectionClosed(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        log.info("handleTextMessage() executing");
        SessionMessage sessionMessage;
        try {
            sessionMessage = gson.fromJson(message.getPayload(), SessionMessage.class);
            socketService.handleMessage(session, sessionMessage);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }
}
