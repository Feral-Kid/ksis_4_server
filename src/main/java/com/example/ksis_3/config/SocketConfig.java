package com.example.ksis_3.config;

import com.example.ksis_3.chatwebsocket.ChatWebSocketHandler;
import com.example.ksis_3.service.ChatWebSocketService;
import com.example.ksis_3.service.GameWebSocketService;
import com.example.ksis_3.service.impl.RPKGameWebSocketImpl;
import com.example.ksis_3.service.impl.TTTGameWebSocketImpl;
import com.example.ksis_3.tictactoewebsocket.ticTacToeWebSocket;
import com.example.ksis_3.websocket.GameWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class SocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketService chatWebSocketService;
    private final GameWebSocketService gameWebSocketService;

    @Autowired
    public SocketConfig(ChatWebSocketService chatWebSocketService, GameWebSocketService gameWebSocketService) {
        this.chatWebSocketService = chatWebSocketService;
        this.gameWebSocketService = gameWebSocketService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameWebSocketHandler(new RPKGameWebSocketImpl(chatWebSocketService)), "/websocket").setAllowedOrigins("*");
        registry.addHandler(new ChatWebSocketHandler(chatWebSocketService), "/chat").setAllowedOrigins("*");
        registry.addHandler(new ticTacToeWebSocket(new TTTGameWebSocketImpl(chatWebSocketService)), "/TTTWebSocket").setAllowedOrigins("*");
    }
}
