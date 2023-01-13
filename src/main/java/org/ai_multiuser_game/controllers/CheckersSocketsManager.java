package org.ai_multiuser_game.controllers;


import org.ai_multiuser_game.data.GameDTO;
import org.ai_multiuser_game.entities.CheckerColor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;


@ServerEndpoint("/game/socket/{userID}")
@ApplicationScoped
public class CheckersSocketsManager {


    private static final Logger logger = Logger.getLogger(CheckersSocketsManager.class.getName());
    Map<Long, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userID") Long userId) {
        sessions.put(userId, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("userID") Long userId) {
        sessions.remove(userId);
        logger.info("Socket for user %dl closed".formatted(userId));
    }

    @OnError
    public void onError(Session session, @PathParam("userID") Long userId, Throwable throwable) {
        sessions.remove(userId);
        logger.warning("Sockets trouble: " + throwable.getMessage());
    }
//
//    @OnMessage
//    public void onMessage(String message, @PathParam("userID") Long userId) {
//
//    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    public void finishGameSockets(GameDTO gameData){
        sessions.remove(gameData.firstUserId);
        sessions.remove(gameData.secondUserId);
    }

    public void informUserAboutTurn(Long gameId, Long userId){
        Session session = sessions.get(userId);
        if (session != null)
            session.getAsyncRemote().sendObject("{\"turnAvailable\": true,\"timestamp\":%s, \"gameID\":%d}"
                    .formatted(Long.toString(System.currentTimeMillis()), gameId));
    }

    public void informUserAboutGameEnd(Long gameId, Long userId, CheckerColor winSide){
        Session session = sessions.get(userId);
        if (session != null)
            session.getAsyncRemote().sendObject("{\"gameFinished\": true," +
                                                    "\"winSide\": \"%s\", \"id\":%s, \"gameID\":%d}".formatted(winSide.name(),
                                                            Long.toString(System.currentTimeMillis()), gameId));
    }

    public void informUserThatOpponentConnectedToGame(Long gameId, Long userId){
        Session session = sessions.get(userId);
        if (session != null)
            session.getAsyncRemote().sendObject("{\"isOpponentConnected\": true, \"id\":%s, \"gameID\":%d}".formatted(
                            Long.toString(System.currentTimeMillis()), gameId));
    }

    public void informUserThatOpponentCapitulatedGame(Long gameId, Long winnerId){
        Session session = sessions.get(winnerId);
        if (session != null)
            session.getAsyncRemote().sendObject("{\"isOpponentCapitulated\": true, \"id\":%s, \"gameID\":%d}".formatted(
                    Long.toString(System.currentTimeMillis()), gameId));
    }
}

