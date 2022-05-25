package com.example.ksis_3.chatwebsocket;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class Room {

    private final UUID groupID;
    private final Gson gson;
    private String name;
    private final List<Session<ChatUser>> users = new ArrayList<>();
    private final List<ChatMessage> history = new ArrayList<>();
    private Session<ChatUser> host;

    public UUID getGroupID() {
        return groupID;
    }

    public Room(Gson gson) {
        this.gson = gson;
        groupID = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void addUser(Session<ChatUser> user) {
        this.users.add(user);
        if (host == null) {
            host = user;
        }
    }

    private Optional<Session<ChatUser>> findUserBySession(WebSocketSession session) {
        return users.stream().filter(o -> o.getSession() == session).findFirst();
    }

    private void sendMessageToAllUsersExcept(ChatMessage chatMessage, Session<ChatUser> excepted) {
        users.stream().filter(o -> o != excepted).forEach(o ->
                o.sendMessage(gson.toJson(chatMessage)));
    }

    private void sendMessageToAllUsers(ChatMessage chatMessage) {
        users.forEach(o ->
                o.sendMessage(gson.toJson(chatMessage)));
    }

    private void addMessageInHistory(ChatMessage message) {
        this.history.add(message);
    }

    public RoomInfo getInfo() {
        return RoomInfo.builder().name(this.name).UUID(this.groupID.toString()).build();
    }

    public void startConnection(ChatMessage message, WebSocketSession session) {
        log.info(String
                .format("User with name: %s is joined",
                        message.getUserName()));
        Session<ChatUser> newUser = new Session<>(session, new ChatUser(message.getUserName()));
        addUser(newUser);
        ChatMessage newMessage = ChatMessage.builder()
                .userMessage(message.getUserMessage())
                .userName(message.getUserName())
                .userId(session.getId())
                .groupId(this.groupID.toString())
                .type("new user")
                .data(message.getData())
                .build();
        sendMessageToAllUsersExcept(newMessage, newUser);
        newUser.sendMessage(gson.toJson(ChatMessage.builder()
                .userMessage(message.getUserMessage())
                .userName(message.getUserName())
                .userId(session.getId())
                .groupId(this.groupID.toString())
                .type("start")
                .data(message.getData())
                .build()));
    }

    private void sendMessage(ChatMessage message, WebSocketSession session) {
        Optional<Session<ChatUser>> optionalChatUserSession = findUserBySession(session);
        if (optionalChatUserSession.isPresent()) {
            ChatUser user = optionalChatUserSession.get().getUser();
            log.info(String
                    .format("User with name: %s send message: %s", user.getName(), message.getUserMessage()));
            ChatMessage newMessage = ChatMessage.builder()
                    .userMessage(message.getUserMessage())
                    .userName(user.getName())
                    .userId(session.getId())
                    .groupId(this.groupID.toString())
                    .type("message")
                    .data(message.getData())
                    .build();
            addMessageInHistory(newMessage);
            sendMessageToAllUsers(newMessage);
        } else {
            startConnection(message, session);
        }
    }

    public void startGame() {

    }

    public String getChatHistoryAsJSON() {
        return gson.toJson(this.history);
    }

    public void handleMessage(WebSocketSession session, ChatMessage message) {
        if (message.getType().equals("start")) {
            startConnection(message, session);
        }
        if (message.getType().equals("message")) {
            sendMessage(message, session);
        }
        if (message.getType().equals("game")) {
            Optional<Session<ChatUser>> optionalChatUserSession = findUserBySession(session);
            if (optionalChatUserSession.isPresent()) {
                if (optionalChatUserSession.get() == host) {
                    startGame();
                }
            }
        }
    }

    public boolean isUserPresent(WebSocketSession session) {
        return this.users.stream().anyMatch(o -> o.getSession() == session);
    }

    private void removeUser(Session<ChatUser> session) {
        this.users.remove(session);
        if (this.host == session) {
            Optional<Session<ChatUser>> sessionOptional = this.users.stream().findFirst();
            sessionOptional.ifPresent(o -> host = o);
            sendMessageToAllUsers(ChatMessage.builder()
                    .userMessage("")
                    .userName(host.getUser().getName())
                    .userId(host.getSession().getId())
                    .type("new host")
                    .groupId(this.groupID.toString())
                    .data("")
                    .build());
        }
    }

    public void terminateConnection(WebSocketSession session) {
        Optional<Session<ChatUser>> optionalChatUserSession = findUserBySession(session);
        if (optionalChatUserSession.isPresent()) {
            ChatUser user = optionalChatUserSession.get().getUser();
            log.info(String.format("Connection closed for user with name: %s", user.getName()));
            removeUser(optionalChatUserSession.get());
            sendMessageToAllUsers(ChatMessage.builder()
                    .userMessage("")
                    .userName(user.getName())
                    .userId(session.getId())
                    .groupId(this.groupID.toString())
                    .type("terminate")
                    .data("")
                    .build());
        }
    }

    public boolean isEmpty() {
        return this.users.isEmpty();
    }
}
