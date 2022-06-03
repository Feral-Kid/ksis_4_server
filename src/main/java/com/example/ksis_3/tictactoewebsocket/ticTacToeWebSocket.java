package com.example.ksis_3.tictactoewebsocket;

import com.example.ksis_3.service.GameWebSocketService;
import com.example.ksis_3.websocket.GameWebSocketHandler;

public class ticTacToeWebSocket extends GameWebSocketHandler {

    public ticTacToeWebSocket(GameWebSocketService socketService) {
        super(socketService);
    }

}
