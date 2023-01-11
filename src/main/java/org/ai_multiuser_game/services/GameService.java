package org.ai_multiuser_game.services;


import org.ai_multiuser_game.controllers.CheckersSocketsManager;
import org.ai_multiuser_game.data.CheckerDTO;
import org.ai_multiuser_game.data.GameDTO;
import org.ai_multiuser_game.entities.GameEntity;
import org.ai_multiuser_game.entities.GameStepEntity;
import org.gamecore.*;

import javax.inject.Inject;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Singleton
public class GameService {
    static private class GameINFO{
        public RussianCheckers game;
        public GameDTO gameParam;

        GameINFO(){}
        GameINFO(GameDTO game)
                throws OutOfBorderException, IllegalCheckerPosition {
            this.gameParam = game;
            this.game = new RussianCheckers();
        }

        public Long getOpponentUserId(Long userId){
            if (gameParam.firstUserId.equals(userId))
                return gameParam.secondUserId;
            return gameParam.firstUserId;
        }
    }

    @Inject
    private CheckersSocketsManager socketsManager;

    private Map<Long, GameINFO> gameSessions = new ConcurrentHashMap<>();

    public List<CheckerDTO> getGameCheckers(Long gameId){
        return gameSessions.get(gameId).game
                .getAllCheckers().stream()
                .map(CheckerDTO::fromRussianChecker).toList();
    }

    public List<String> getAvailableStepsForCheckerAtPos(Long gameId, String posNotation)
            throws OutOfBorderException, IllegalCheckerPosition,
            GameStateError, CheckerGameStep.CheckerStepApplyException {
        if (posNotation != null)
            return gameSessions.get(gameId).game
                .getCheckerAt(new CheckerBoardPosition(posNotation))
                .getAvailableSteps()
                .stream().filter(Objects::nonNull)
                .map(step -> step.getTo().getPosNotation()).toList();

        return gameSessions.get(gameId).game
                .getAllCheckers().stream()
                .flatMap(c -> {try{return c.getAvailableSteps().stream();}catch (Throwable ignore){return Stream.empty();
                }})
                .filter(Objects::nonNull)
                .map(step -> ((CheckerGameStep)step).getTo().getPosNotation()).toList();
    }

    public void makeStep(Long gameId, Long userId, String from, String to) throws OutOfBorderException, IllegalCheckerPosition {
        gameSessions.get(gameId).game.getCheckerAt(new CheckerBoardPosition(from)).makeStep(to);

        GameStepEntity.saveStep(gameId, from + ":" + to);
        socketsManager.informUserAboutTurn(gameId, gameSessions.get(gameId).getOpponentUserId(userId));
    }

    public void connectToGame(Long gameId, Long userId){
        GameDTO gameDTO = gameSessions.get(gameId).gameParam;
        gameDTO.secondUserId = userId;
        GameEntity.addUserToGame(gameDTO, userId);
    }

    public void finishGame(Long id){
        GameEntity.finishGame(gameSessions.get(id).gameParam);
        socketsManager.finishGameSockets(gameSessions.get(id).gameParam);
    }

    public List<GameDTO> getUserGames(Long userId){
        return GameEntity.getUsersGamesByUserID(userId);
    }

    public GameDTO startGame(Long userId) throws OutOfBorderException, IllegalCheckerPosition {
        GameDTO newGame = GameEntity.registerGame(userId);
        gameSessions.put(newGame.id, new GameINFO(newGame));
        return newGame;
    }

    public Set<Long> getGamesIDs(){
        return gameSessions.keySet();
    }
}
