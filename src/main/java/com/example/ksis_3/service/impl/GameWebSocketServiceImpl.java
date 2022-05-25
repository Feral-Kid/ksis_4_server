package com.example.ksis_3.service.impl;

import com.example.ksis_3.chatwebsocket.Session;
import com.example.ksis_3.service.GameWebSocketService;
import com.example.ksis_3.websocket.GameUser;
import com.example.ksis_3.websocket.SessionMessage;
import com.example.ksis_3.websocket.UsersSession;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class GameWebSocketServiceImpl implements GameWebSocketService {
    private final Gson gson = new Gson();
    private final List<UsersSession> usersSessions = new ArrayList<>();
    private final List<Session<GameUser>> users = new ArrayList<>();

    @Override
    public void handleMessage(WebSocketSession session, SessionMessage sessionMessage) {
        log.info("handleTextMessage() executing");
        if (sessionMessage.getSessionStatus().equals("start")) {
            addInQueue(session, sessionMessage.getUserName());
            findPartner();
        }
        if (sessionMessage.getSessionStatus().equals("game")) {
            startGame(session, sessionMessage.getUserChoice());
        }
    }

    private void addInQueue(WebSocketSession session, String userName) {
        if (userName != null && !userName.isBlank()) {
            users.add(new Session<>(session, new GameUser(userName)));
            log.info("Connected with user: " + userName);
        }
    }

    private void findPartner() {
        List<Session<GameUser>> activeSessions = users.stream().filter(o -> o.getSession().isOpen()).limit(2).collect(Collectors.toList());
        if (activeSessions.size() > 1) {
            addSessionPairAndSendMessage(new UsersSession(activeSessions.get(0), activeSessions.get(1)));
            log.info(String
                    .format("User with name: %s and user with name: %s is connected",
                            activeSessions.get(0).getUser().getName(), activeSessions.get(0).getUser().getName()));
            users.remove(activeSessions.get(0));
            users.remove(activeSessions.get(1));
        }
    }

    public void afterConnectionClosed(WebSocketSession session) {
        Optional<Session<GameUser>> userSessionOptional = users.stream().filter(userSession -> userSession.getSession() == session).findFirst();
        if (userSessionOptional.isPresent()) {
            users.remove(userSessionOptional.get());
            log.info("Connection closed for user with name: " + userSessionOptional.get().getUser().getName());
        } else {
            UsersSession terminatedSession = terminateSession(session);
            if (terminatedSession == null)
                log.info("Connection closed for user with undefined name: ");
            else {
                log.info("Connection closed for user with name: " + terminatedSession.getFirstUser().getUser().getName());
                addInQueue(terminatedSession.getSecondUser().getSession(), terminatedSession.getSecondUser().getUser().getName());
            }
        }
    }

    public void addSessionPairAndSendMessage(UsersSession pair) {

        Session<GameUser> firstUserSession = pair.getFirstUser();
        Session<GameUser> secondUserSession = pair.getSecondUser();
        try {
            firstUserSession.getSession()
                    .sendMessage(new TextMessage(gson
                            .toJson(new SessionMessage(secondUserSession.getUser().getName(), "", "start"))));
            secondUserSession.getSession()
                    .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUser().getName(), "", "start"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        usersSessions.add(pair);
    }

    public UsersSession terminateSession(WebSocketSession session) {
        Optional<UsersSession> userSessionsPairOptional = usersSessions.stream()
                .filter( o -> ((o.getFirstUser().getSession() == session) || (o.getSecondUser().getSession() == session))).findFirst();
        if (userSessionsPairOptional.isEmpty()) {
            return null;
        } else {
            UsersSession userSessionsPair = userSessionsPairOptional.get();
            usersSessions.remove(userSessionsPair);
            Session<GameUser> firstUserSession = userSessionsPair.getFirstUser();
            Session<GameUser> secondUserSession = userSessionsPair.getSecondUser();
            if (firstUserSession.getSession() == session) {
                try {
                    secondUserSession.getSession()
                            .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUser().getName(), "", "terminate"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new UsersSession(firstUserSession, secondUserSession);
            } else {
                try {
                    firstUserSession.getSession()
                            .sendMessage(new TextMessage(gson.toJson(new SessionMessage(secondUserSession.getUser().getName(), "", "terminate"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new UsersSession(secondUserSession, firstUserSession);
            }

        }
    }

    public void startGame(WebSocketSession session, String userChoice) {
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
