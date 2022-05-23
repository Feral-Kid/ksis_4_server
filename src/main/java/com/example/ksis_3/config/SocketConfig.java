package com.example.ksis_3.config;

import com.example.ksis_3.chatwebsocket.ChatWebSocket;
import com.example.ksis_3.service.ChatWebSocketService;
import com.example.ksis_3.websocket.ConnectionWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketService service;

    @Autowired
    public SocketConfig(ChatWebSocketService service) {
        this.service = service;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ConnectionWebSocketHandler(), "/websocket").setAllowedOrigins("*");
        registry.addHandler(new ChatWebSocket(service), "/chat").setAllowedOrigins("*");
    }
}
