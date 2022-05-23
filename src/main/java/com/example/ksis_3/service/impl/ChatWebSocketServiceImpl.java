package com.example.ksis_3.service.impl;

import com.example.ksis_3.chatwebsocket.ChatMessage;
import com.example.ksis_3.chatwebsocket.Room;
import com.example.ksis_3.exception.MessageSendException;
import com.example.ksis_3.exception.RoomIsNotPresentException;
import com.example.ksis_3.service.ChatWebSocketService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    private final List<Room> rooms = new ArrayList<>();
    private final Gson gson = new Gson();

    public ChatWebSocketServiceImpl() {
        createRoom();
    }

    @Override
    public String getAllRooms() {
        return gson.toJson(this.rooms.stream().map( o -> o.getGroupID().toString()).collect(Collectors.toList()));
    }

    @Override
    public String getHistoryByRoomId(UUID roomId) {
        Room room = findRoomById(roomId);
        return room.getChatHistoryAsJSON();
    }

    private UUID createRoom() {
        Room room = new Room(this.gson);
        this.rooms.add(room);
        return room.getGroupID();
    }

    private Room findRoomById(UUID uuid) {
        Optional<Room> roomOptional = this.rooms.stream().filter(o -> o.getGroupID() == uuid).findFirst();
        if (roomOptional.isPresent()) {
            return roomOptional.get();
        } else {
            throw new RoomIsNotPresentException(String.format("Room with id: %s is not present", uuid.toString()));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, ChatMessage message) {
        if (message.getType().equals("create room")) {
            UUID uuid = createRoom();
            try {
                session.sendMessage(new TextMessage(
                        gson.toJson
                                (ChatMessage.builder()
                                        .userMessage("")
                                        .userId(message.getUserId())
                                        .type("room created")
                                        .groupId(uuid.toString())
                                        .userName(message.getUserName())
                                        .build())));
            } catch (IOException e) {
                throw new MessageSendException(String.format("Failed to send message to user named: %s", message.getUserName()), e);
            }
        } else {
            Room room;
            if (message.getGroupId().isBlank()) {
                room = this.rooms.get(0);
            } else {
                room = findRoomById(UUID.fromString(message.getGroupId()));
            }
            room.handleMessage(session, message);
        }
    }

    @Override
    public void terminateConnection(WebSocketSession session) {
        Optional<Room> roomOptional = this.rooms.stream().filter(o -> o.isUserPresent(session)).findFirst();
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.terminateConnection(session);
            if (room.isEmpty()) {
                this.rooms.remove(room);
            }
        }
    }
}
