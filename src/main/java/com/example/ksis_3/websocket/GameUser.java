package com.example.ksis_3.websocket;

import com.example.ksis_3.chatwebsocket.User;

import java.util.UUID;

public class GameUser extends User {

    private String userChoice;

    public GameUser(String name, UUID uuid) {
        super(name, uuid);
    }

    public String getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }
}
