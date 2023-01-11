package org.ai_multiuser_game.controllers;


import org.ai_multiuser_game.data.GameDTO;

import java.util.Map;
import java.util.Objects;
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


@ServerEndpoint("/game/checker/{gameID}/user/{userID}")
@ApplicationScoped
public class CheckersSocketsManager {
    static private class IDPair{
        public Long gameId;
        public Long userId;

        public IDPair(Long gameId, Long userId){
            this.gameId = gameId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IDPair idPair)) return false;

            if (!Objects.equals(gameId, idPair.gameId)) return false;
            return Objects.equals(userId, idPair.userId);
        }

        @Override
        public int hashCode() {
            int result = (gameId != null ? gameId.hashCode() : 0) * 17;
            result = (result + (userId != null ? userId.hashCode() : 0)) * 31;
            return result;
        }
    }
    private static final Logger logger = Logger.getLogger(CheckersSocketsManager.class.getName());
    Map<IDPair, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("gameID") Long gameId, @PathParam("userID") Long userId) {
        sessions.put(new IDPair(gameId, userId), session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("gameID") Long gameId, @PathParam("userID") Long userId) {
        sessions.remove(new IDPair(gameId, userId));
        logger.info("Socket for user %dl closed".formatted(userId));
    }

    @OnError
    public void onError(Session session, @PathParam("gameID") Long gameId, @PathParam("userID") Long userId, Throwable throwable) {
        sessions.remove(new IDPair(gameId, userId));
        logger.warning("Sockets trouble: " + throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("gameID") Long gameId, @PathParam("userID") Long userId) {

    }

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
        sessions.remove(new IDPair(gameData.id, gameData.firstUserId));
        sessions.remove(new IDPair(gameData.id, gameData.secondUserId));
    }

    public void informUserAboutTurn(GameDTO gameData, Long userId) {
        sessions.get(new IDPair(gameData.id, userId)).getAsyncRemote()
                .sendObject("{turnAvailable: true}");
    }

    public void informUserAboutTurn(Long gameId, Long userId){
        Session session = sessions.get(new IDPair(gameId, userId));
        if (session != null)
            session.getAsyncRemote().sendObject("{turnAvailable: true}");
    }
}

