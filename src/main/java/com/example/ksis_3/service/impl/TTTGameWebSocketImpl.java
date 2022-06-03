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
import java.util.Random;

@Slf4j
public class TTTGameWebSocketImpl extends GameWebSocketServiceImpl {

    public TTTGameWebSocketImpl(ChatWebSocketService chatWebSocketService) {
        super(chatWebSocketService);
    }

    @Override
    public void addSessionPairAndSendMessage(UsersSession pair) {
        Session<GameUser> firstUserSession = pair.getFirstUser();
        Session<GameUser> secondUserSession = pair.getSecondUser();
        Random random = new Random();
        String firstUserChoice;
        String secondUserChoice;
        int value = random.nextInt(2);
        if (value == 0) {
            firstUserChoice = "o";
            secondUserChoice = "x";
        } else {
            firstUserChoice = "x";
            secondUserChoice = "o";
        }

        try {
            firstUserSession.getSession()
                    .sendMessage(new TextMessage(gson
                            .toJson(new SessionMessage(secondUserSession.getUser().getName(), firstUserChoice, "start"))));
            secondUserSession.getSession()
                    .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUser().getName(), secondUserChoice, "start"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        usersSessions.add(pair);
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
                try {
                    secondUserSession.getSession()
                            .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUser().getName(), userChoice, "game"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                secondUserSession.getUser().setUserChoice(null);
                firstUserSession.getUser().setUserChoice(null);
            }
            if (secondUserSession.getSession() == session) {
                try {
                    firstUserSession.getSession()
                            .sendMessage(new TextMessage(gson
                                    .toJson(new SessionMessage(secondUserSession.getUser().getName(), userChoice, "game"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                secondUserSession.getUser().setUserChoice(null);
                firstUserSession.getUser().setUserChoice(null);
            }
        }
    }
}
