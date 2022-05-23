package com.example.ksis_3.service;

import com.example.ksis_3.chatwebsocket.ChatMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

public interface ChatWebSocketService {

    String getAllRooms();

    String getHistoryByRoomId(UUID roomId);

    void handleMessage(WebSocketSession session, ChatMessage message);

    void terminateConnection(WebSocketSession session);
}
