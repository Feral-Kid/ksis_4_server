package com.example.ksis_3.websocket;

import com.example.ksis_3.chatwebsocket.Session;
import lombok.Value;

@Value
public class UsersSession {
    Session<GameUser> firstUser;
    Session<GameUser> secondUser;
}
