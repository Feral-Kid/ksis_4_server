package com.example.ksis_3.service;

import com.example.ksis_3.chatwebsocket.ChatMessage;
import com.example.ksis_3.chatwebsocket.Room;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public interface ChatWebSocketService {

    String getAllRooms();

    String getHistoryByRoomId(UUID roomId);

    Room findRoomById(UUID uuid);

    String getRoomInfoById(UUID uuid);

    void handleMessage(WebSocketSession session, ChatMessage message);

    void terminateConnection(WebSocketSession session);
}
