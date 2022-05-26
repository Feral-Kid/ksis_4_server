package com.example.ksis_3.chatwebsocket;

import com.example.ksis_3.service.ChatWebSocketService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatWebSocketService service;
    private final Gson gson;

    public ChatWebSocketHandler(ChatWebSocketService service) {
        this.service = service;
        gson = new Gson();;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        log.info("handleTextMessage() executing");
        ChatMessage sessionMessage;
        try {
            sessionMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
            service.handleMessage(session, sessionMessage);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        service.terminateConnection(session);
    }
}
