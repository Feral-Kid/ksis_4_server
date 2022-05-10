package com.example.ksis_3.service.impl;

import com.example.ksis_3.service.SocketService;
import com.example.ksis_3.websocket.SessionMessage;
import com.example.ksis_3.websocket.UserSession;
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
public class SocketServiceImpl implements SocketService {
    private final Gson gson = new Gson();
    private final List<UsersSession> usersSessions = new ArrayList<>();
    private final List<UserSession> users = new ArrayList<>();

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
            users.add(new UserSession(userName, session));
            log.info("Connected with user: " + userName);
        }
    }

    private void findPartner() {
        List<UserSession> activeSessions = users.stream().filter(UserSession::isOpen).limit(2).collect(Collectors.toList());
        if (activeSessions.size() > 1) {
            addSessionPairAndSendMessage(new UsersSession(activeSessions.get(0), activeSessions.get(1)));
            log.info(String
                    .format("User with name: %s and user with name: %s is connected",
                            activeSessions.get(0).getUserName(), activeSessions.get(0).getUserName()));
            users.remove(activeSessions.get(0));
            users.remove(activeSessions.get(1));
        }
    }

    public void afterConnectionClosed(WebSocketSession session) {
        Optional<UserSession> userSessionOptional = users.stream().filter(userSession -> userSession.getSession() == session).findFirst();
        if (userSessionOptional.isPresent()) {
            users.remove(userSessionOptional.get());
            log.info("Connection closed for user with name: " + userSessionOptional.get().getUserName());
        } else {
            UsersSession terminatedSession = terminateSession(session);
            if (terminatedSession == null)
                log.info("Connection closed for user with undefined name: ");
            else {
                log.info("Connection closed for user with name: " + terminatedSession.getFirstUser().getUserName());
                addInQueue(terminatedSession.getSecondUser().getSession(), terminatedSession.getSecondUser().getUserName());
            }
        }
    }

    public void addSessionPairAndSendMessage(UsersSession pair) {

        UserSession firstUserSession = pair.getFirstUser();
        UserSession secondUserSession = pair.getSecondUser();
        try {
            firstUserSession.getSession()
                    .sendMessage(new TextMessage(gson
                            .toJson(new SessionMessage(secondUserSession.getUserName(), "", "start"))));
            secondUserSession.getSession()
                    .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUserName(), "", "start"))));
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
            UserSession firstUserSession = userSessionsPair.getFirstUser();
            UserSession secondUserSession = userSessionsPair.getSecondUser();
            if (firstUserSession.getSession() == session) {
                try {
                    secondUserSession.getSession()
                            .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUserName(), "", "terminate"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new UsersSession(firstUserSession, secondUserSession);
            } else {
                try {
                    firstUserSession.getSession()
                            .sendMessage(new TextMessage(gson.toJson(new SessionMessage(secondUserSession.getUserName(), "", "terminate"))));
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
            UserSession firstUserSession = userSessionsPair.getFirstUser();
            UserSession secondUserSession = userSessionsPair.getSecondUser();
            if (firstUserSession.getSession() == session) {
                if (!(secondUserSession.getUserChoice() == null)) {
                    try {
                        firstUserSession.getSession()
                                .sendMessage(new TextMessage(gson
                                        .toJson(new SessionMessage(secondUserSession.getUserName(), secondUserSession.getUserChoice(), "game"))));
                        secondUserSession.getSession()
                                .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUserName(), userChoice, "game"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    secondUserSession.setUserChoice(null);
                    firstUserSession.setUserChoice(null);
                } else {
                    firstUserSession.setUserChoice(userChoice);
                }
            }
            if (secondUserSession.getSession() == session) {
                if (!(firstUserSession.getUserChoice() == null)) {
                    try {
                        firstUserSession.getSession()
                                .sendMessage(new TextMessage(gson
                                        .toJson(new SessionMessage(secondUserSession.getUserName(), userChoice, "game"))));
                        secondUserSession.getSession()
                                .sendMessage(new TextMessage(gson.toJson(new SessionMessage(firstUserSession.getUserName(), firstUserSession.getUserChoice(), "game"))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    secondUserSession.setUserChoice(null);
                    firstUserSession.setUserChoice(null);
                } else {
                    secondUserSession.setUserChoice(userChoice);
                }
            }
        }
    }
}
