package com.example.ksis_3.websocket;

import com.example.ksis_3.chatwebsocket.User;
import lombok.Data;

public class GameUser extends User {

    private String userChoice;

    public GameUser(String name) {
        super(name);
    }

    public String getUserChoice() {
        return userChoice;
    }

    public void setUserChoice(String userChoice) {
        this.userChoice = userChoice;
    }
}
