package com.example.ksis_3.service;

import com.example.ksis_3.chatwebsocket.ChatMessage;
import com.example.ksis_3.chatwebsocket.ChatUser;
import com.example.ksis_3.chatwebsocket.Session;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

public interface ChatWebSocketService {

    void handleMessage(WebSocketSession session, ChatMessage message);

    void terminateConnection(WebSocketSession session);
}
