package com.example.ksis_3.controller;

import com.example.ksis_3.service.ChatWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/message")
public class ChatController {

    private final ChatWebSocketService service;

    @Autowired
    public ChatController(ChatWebSocketService service) {
        this.service = service;
    }

    @GetMapping("/getAllRooms")
    public String getAllRooms() {
        return service.getAllRooms();
    }

    @GetMapping("/getHistory/{id}")
    public String getHistory(@PathVariable String id) {
        return service.getHistoryByRoomId(UUID.fromString(id));
    }
}
