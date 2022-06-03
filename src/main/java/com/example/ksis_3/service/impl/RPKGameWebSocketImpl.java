package com.example.ksis_3.service.impl;

import com.example.ksis_3.chatwebsocket.Session;
import com.example.ksis_3.service.ChatWebSocketService;
import com.example.ksis_3.websocket.GameUser;
import com.example.ksis_3.websocket.SessionMessage;
import com.example.ksis_3.websocket.UsersSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class RPKGameWebSocketImpl extends GameWebSocketServiceImpl {
    public RPKGameWebSocketImpl(ChatWebSocketService chatWebSocketService) {
        super(chatWebSocketService);
    }

    @Override
    public void startGame(WebSocketSession session, String userChoice) {
        log.info("startGame() method executing");
        Optional<UsersSession> userSessionsPairOptional = usersSessions.stream()
                .filter( o -> ((o.getFirstUser().getSession() == session) || (o.getSecondUser().getSession() == session))).findFirst();
        if (userSessionsPairOptional.isPresent()) {
            UsersSession userSessionsPair = userSessionsPairOptional.get();
            Session<GameUser> firstUserSession = userSessionsPair.getFirstUser();
            Session<GameUser> secondUserSession = userSessionsPair.getSecondUser();
            if (firstUserSession.getSession() == session) {
                if (!(secondUserSession.getUser().getUserChoice() == null)) {
                    try {
                        firstUserSession.getSession()
                                .sendMessage(new TextMessage(gson
                                        .toJson(new SessionMessage(secondUserSession.getUser().getName(), secondUserSession.getUser().getUserChoice(), "game"))));
                        secondUserSession.getSession()
                                .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUser().getName(), userChoice, "game"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    secondUserSession.getUser().setUserChoice(null);
                    firstUserSession.getUser().setUserChoice(null);
                } else {
                    firstUserSession.getUser().setUserChoice(userChoice);
                }
            }
            if (secondUserSession.getSession() == session) {
                if (!(firstUserSession.getUser().getUserChoice() == null)) {
                    try {
                        firstUserSession.getSession()
                                .sendMessage(new TextMessage(gson
                                        .toJson(new SessionMessage(secondUserSession.getUser().getName(), userChoice, "game"))));
                        secondUserSession.getSession()
                                .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUser().getName(), firstUserSession.getUser().getUserChoice(), "game"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    secondUserSession.getUser().setUserChoice(null);
                    firstUserSession.getUser().setUserChoice(null);
                } else {
                    secondUserSession.getUser().setUserChoice(userChoice);
                }
            }
        }
    }
}
