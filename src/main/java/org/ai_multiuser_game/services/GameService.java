package org.ai_multiuser_game.services;


import org.ai_multiuser_game.controllers.CheckersSocketsManager;
import org.ai_multiuser_game.data.CheckerDTO;
import org.ai_multiuser_game.data.GameDTO;
import org.ai_multiuser_game.entities.*;
import org.gamecore.*;

import javax.inject.Inject;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Singleton
public class GameService {
    static private class GameINFO{
        public RussianCheckers game;
        public GameDTO gameParam;

        GameINFO(){}
        GameINFO(GameDTO gameDTO)
                throws OutOfBorderException, IllegalCheckerPosition {
            this.gameParam = gameDTO;
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
        return safeGetGameById(gameId).game
                .getAllCheckers().stream()
                .map(CheckerDTO::fromRussianChecker).toList();
    }

    public List<String> getAvailableStepsForCheckerAtPos(Long gameId, String posNotation)
            throws OutOfBorderException, IllegalCheckerPosition,
            GameStateError, CheckerGameStep.CheckerStepApplyException {
        if (posNotation != null) {
            RussianCheckers.Checker checker = safeGetGameById(gameId).game
                    .getCheckerAt(new CheckerBoardPosition(posNotation));

            if (checker == null)
                throw new WebApplicationException("Checker wasn't found.",
                        Response.status(404,
                                "Checker at position %s in the game{%dl} was not found"
                                        .formatted(posNotation, gameId)).build());
            return checker.getAvailableSteps()
                    .stream().filter(Objects::nonNull)
                    .map(step -> step.getTo().getPosNotation()).toList();
        }
        return gameSessions.get(gameId).game
                .getAllCheckers().stream()
                .flatMap(c -> {try{return c.getAvailableSteps().stream();}catch (Throwable ignore){return Stream.empty();
                }})
                .filter(Objects::nonNull)
                .map(step -> ((CheckerGameStep)step).getTo().getPosNotation()).toList();
    }

    public void makeStep(Long gameId, Long userId, String from, String to)
            throws OutOfBorderException, IllegalCheckerPosition, GameStateError {

        GameDTO gameInfo = gameSessions.get(gameId).gameParam;
        RussianCheckers game = gameSessions.get(gameId).game;
        game.getCheckerAt(from).makeStep(to);

        GameStepEntity.saveStep(gameId, from + ":" + to);

        if (game.isGameEnd()){
            CheckerColor winColor = CheckerColor.fromGameColor(game.getWinnerSide());
            if (gameInfo.firstUserColor.equals(winColor))
                gameInfo.winnerId = gameInfo.firstUserId;
            else
                gameInfo.winnerId = gameInfo.secondUserId;
            finishGame(gameId);
        }
        else
            socketsManager.informUserAboutTurn(gameId, gameSessions.get(gameId).getOpponentUserId(userId));
    }

    public void connectToGame(Long gameId, Long userId){
        GameINFO info = safeGetGameById(gameId);
        GameDTO gameDTO = info.gameParam;
        if (gameDTO.secondUserId != null)
            throw new WebApplicationException("User exist in the game",
                    Response.status(409, "User exist in the game").build());

        info.gameParam = GameEntity.addUserToGame(gameDTO, userId);
        socketsManager.informUserThatOpponentConnectedToGame(gameId, userId);
        socketsManager.informUserAboutTurn(gameId, getOpponentId(gameDTO, userId));
    }

    public void finishGame(Long id){
        finishGame(safeGetGameById(id).gameParam);
    }

    public void finishAllGames(){
        gameSessions.clear();
        GameEntity.finishAllGames();
    }
    private void finishGame(GameDTO game){
        GameEntity.finishGame(game);
        socketsManager.informUserAboutGameEnd(game.id, game.firstUserId, game.getWinnerSideColor());
        socketsManager.informUserAboutGameEnd(game.id, game.secondUserId, game.getWinnerSideColor());
        socketsManager.finishGameSockets(safeGetGameById(game.id).gameParam);
    }

    public void capitulateGameByUser(Long gameId, Long looseId){
        GameEntity.capitulateGameByUser(safeGetGameById(gameId).gameParam, looseId);
        GameDTO dto = gameSessions.get(gameId).gameParam;
        Long winId = getOpponentId(dto, looseId);
        socketsManager.informUserThatOpponentCapitulatedGame(gameId, winId);
        finishGame(gameId);
    }

    public List<GameDTO> getUserGames(Long userId, Long gameId){
        if (gameId == null)
            return GameEntity.getUsersGamesByUserID(userId).stream()
                    .map(el -> {
                        if (el.status != GameStatus.FINISHED) {
                            GameINFO status = gameSessions.getOrDefault(el.id, null);
                            if (status != null)
                                el.turnOf = CheckerColor.fromGameColor(status.game.getTurnSide());
                        }
                        return el;
                    }).toList();

        GameEntity game = GameEntity.findById(gameId);
        if (game == null || (!game.user1.id.equals(userId) &&
                !(game.user2 != null && game.user2.id.equals(userId))))
            return Collections.emptyList();
        GameDTO gameDTO = game.toGameDTO();
        GameINFO gameINFO = gameSessions.getOrDefault(game.id, null);
        if (gameINFO != null)
            gameDTO.turnOf = CheckerColor.fromGameColor(gameINFO.game.getTurnSide());
        return List.of(gameDTO);
    }

    public GameDTO startGame(Long userId) throws OutOfBorderException, IllegalCheckerPosition {
        GameDTO newGame = GameEntity.registerGame(userId);
        gameSessions.put(newGame.id, new GameINFO(newGame));
        return newGame;
    }

    public Set<Long> getGamesIDs(){
        return gameSessions.keySet();
    }

    private GameINFO safeGetGameById(Long gameId){
        GameINFO info = gameSessions.getOrDefault(gameId, null);
        if (info == null)
            throw new WebApplicationException("Game does not exist",
                    Response.status(404, "Game with %dl does not run or exist".formatted(gameId)).build());
        return info;
    }

    private static Long getOpponentId(GameDTO game, Long userId){
        if (game.firstUserId.equals(userId))
            return game.secondUserId;
        return game.firstUserId;
    }
}
