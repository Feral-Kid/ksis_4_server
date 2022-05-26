package com.example.ksis_3.chatwebsocket;

import com.example.ksis_3.chatwebsocket.util.UUIDUtils;
import com.example.ksis_3.exception.UserIsNotAHostException;
import com.example.ksis_3.exception.UserIsNotPresentException;
import com.example.ksis_3.websocket.GameUser;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class Room {

    private final UUID groupID;
    private final Gson gson;
    private String name;
    private boolean isGameStarted;
    private final List<Session<ChatUser>> users = new ArrayList<>();
    private final List<ChatMessage> history = new ArrayList<>();
    private final List<Session<GameUser>> gameUsers = new ArrayList<>();

    private Session<ChatUser> host;

    public UUID getGroupID() {
        return groupID;
    }

    public boolean isGameStarted() {
        return this.gameUsers.size() == this.users.size();
    }

    public void addGameUser(Session<GameUser> gameUser) {
        this.gameUsers.add(gameUser);
    }

    public List<Session<GameUser>> getGameUsers() {
        return gameUsers;
    }

    public void setGameStarted(boolean gameStarted) {
        isGameStarted = gameStarted;
    }

    public List<ChatUser> getAllUsers() {
        return this.users.stream().map(Session::getUser).collect(Collectors.toList());
    }

    public Room(Gson gson) {
        this.gson = gson;
        groupID = UUID.randomUUID();
    }

    public ChatUser getHost() {
        return host.getUser();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHost(Session<ChatUser> host) {
        this.host = host;
    }

    private void addUser(Session<ChatUser> user) {
        this.users.add(user);
        if (host == null) {
            host = user;
        }
    }

    public ChatUser findUserById(UUID uuid) {
        Session<ChatUser> chatUser = users.stream().filter(o -> o.getUser().getUuid().equals(uuid)).findFirst().orElseThrow(() -> new UserIsNotPresentException(String.format("User with id: %s is not present", uuid.toString())));
        return chatUser.getUser();
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
                .format("User with name: %s is joined in room with name: %s",
                        message.getUserName(), this.name));
        Session<ChatUser> newUser = new Session<>(session, new ChatUser(message.getUserName(), UUIDUtils.getUUIDFromString(session.getId())));
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
                    .format("User with name: %s send message: %s in room: %s", user.getName(), message.getUserMessage(), this.name));
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
                } else {
                    throw new UserIsNotAHostException(String.format("User with name: %s isn't host", optionalChatUserSession.get().getUser().getName()));
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
            log.info(String.format("Connection closed for user with name: %s in room with name: %s", user.getName(), this.name));
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
