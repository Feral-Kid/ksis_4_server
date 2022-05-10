package com.example.ksis_3.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {

    private String userChoice;

    private String userName;

    private WebSocketSession session;

    public UserSession(String userName, WebSocketSession session) {
        this.userName = userName;
        this.session = session;
    }

    public boolean isOpen() {
        return this.session.isOpen();
    }
}
