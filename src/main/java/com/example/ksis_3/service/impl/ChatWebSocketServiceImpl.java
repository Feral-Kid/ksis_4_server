package com.example.ksis_3.service.impl;

import com.example.ksis_3.chatwebsocket.ChatMessage;
import com.example.ksis_3.chatwebsocket.ChatUser;
import com.example.ksis_3.chatwebsocket.Session;
import com.example.ksis_3.service.ChatWebSocketService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    private final Gson gson = new Gson();
    private final List<Session<ChatUser>> users = new ArrayList<>();

    private void addUser(Session<ChatUser> user) {
        this.users.add(user);
    }

    private Optional<Session<ChatUser>> findUserBySession(WebSocketSession session) {
        return users.stream().filter(o -> o.getSession() == session).findFirst();
    }


    private void sendMessageToAllUsers(ChatMessage chatMessage) {
        users.forEach(o ->
                o.sendMessage(gson.toJson(chatMessage)));
    }

    private void startConnection(ChatMessage message, WebSocketSession session) {
        log.info(String
                .format("User with name: %s is joined",
                        message.getUserName()));
        addUser(new Session<>(session, new ChatUser(message.getUserName())));
        sendMessageToAllUsers(ChatMessage.builder()
                .message(message.getMessage())
                .userName(message.getUserName())
                .userId(session.getId())
                .type("start")
                .build());
    }

    private void sendMessage(ChatMessage message, WebSocketSession session) {
        Optional<Session<ChatUser>> optionalChatUserSession = findUserBySession(session);
        if (optionalChatUserSession.isPresent()) {
            ChatUser user = optionalChatUserSession.get().getUser();
            log.info(String
                    .format("User with name: %s send message: %s", user.getName(), message.getMessage()));
            sendMessageToAllUsers(ChatMessage.builder()
                    .message(message.getMessage())
                    .userName(user.getName())
                    .userId(session.getId())
                    .type("message")
                    .build());
        } else {
            startConnection(message, session);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, ChatMessage message) {
        if (message.getType().equals("start")) {
            startConnection(message, session);
        }

        if (message.getType().equals("message")) {
           sendMessage(message, session);
        }
    }

    @Override
    public void terminateConnection(WebSocketSession session) {
        Optional<Session<ChatUser>> optionalChatUserSession = findUserBySession(session);
        if (optionalChatUserSession.isPresent()) {
            ChatUser user = optionalChatUserSession.get().getUser();
            log.info(String.format("Connection closed for user with name: %s", user.getName()));
            this.users.remove(optionalChatUserSession.get());
            sendMessageToAllUsers(ChatMessage.builder()
                    .message("")
                    .userName(user.getName())
                    .userId(session.getId())
                    .type("terminate")
                    .build());
        }
    }
}
