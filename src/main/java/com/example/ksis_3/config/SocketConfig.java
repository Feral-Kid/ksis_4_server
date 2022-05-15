package com.example.ksis_3.config;

import com.example.ksis_3.chatwebsocket.ChatWebSocket;
import com.example.ksis_3.websocket.ConnectionWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ConnectionWebSocketHandler(), "/websocket").setAllowedOrigins("*");
        registry.addHandler(new ChatWebSocket(), "/chat").setAllowedOrigins("*");
    }
}
