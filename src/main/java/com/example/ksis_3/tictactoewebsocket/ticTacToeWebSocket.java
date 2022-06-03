package com.example.ksis_3.tictactoewebsocket;

import com.example.ksis_3.chatwebsocket.util.MessageParser;
import com.example.ksis_3.service.GameWebSocketService;
import com.example.ksis_3.websocket.GameWebSocketHandler;
import com.example.ksis_3.websocket.SessionMessage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ticTacToeWebSocket extends TextWebSocketHandler {

    private final Gson gson;
    private final GameWebSocketService socketService;

    public ticTacToeWebSocket(GameWebSocketService socketService) {
        this.gson = new Gson();
        this.socketService = socketService;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        try {
            socketService.afterConnectionClosed(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        log.info("handleTextMessage() executing");
        SessionMessage sessionMessage;
        try {
            sessionMessage = MessageParser.parse(message.getPayload(), SessionMessage.class, gson);
            socketService.handleMessage(session, sessionMessage);
        } catch (Exception e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(gson.toJson(SessionMessage.builder().userChoice(e.getMessage()).sessionStatus("Exception").build())));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }
}
