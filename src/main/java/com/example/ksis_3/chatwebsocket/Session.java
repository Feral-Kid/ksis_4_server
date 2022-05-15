package com.example.ksis_3.chatwebsocket;

import com.example.ksis_3.exception.MessageSendException;
import com.example.ksis_3.websocket.SessionMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class Session<T extends User> {

    private final WebSocketSession session;
    private final T user;

    public Session(WebSocketSession session, T user) {
        this.session = session;
        this.user = user;
    }

    public void sendMessage(String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new MessageSendException(String.format("Failed to send message to user named: %s", user.getName()), e);
        }
    }

    public WebSocketSession getSession() {
        return session;
    }

    public T getUser() {
        return user;
    }
}
