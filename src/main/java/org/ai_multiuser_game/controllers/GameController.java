package org.ai_multiuser_game.controllers;

import org.ai_multiuser_game.data.CheckerDTO;
import org.ai_multiuser_game.data.GameDTO;
import org.ai_multiuser_game.services.GameService;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.gamecore.CheckerGameStep;
import org.gamecore.GameStateError;
import org.gamecore.IllegalCheckerPosition;
import org.gamecore.OutOfBorderException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonNumber;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@RolesAllowed("user")
@Path("/game/checker")
public class GameController {

    @Inject
    private GameService gameService;
    @Inject
    JsonWebToken jwt;
    @GET
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<GameDTO> returnUserGames(@QueryParam("gameID") Long gameId){
        // in this API path we take the user ID from JWT
        // TODO: break the hack API path move it to user info paths
        return gameService.getUserGames(((JsonNumber)jwt.getClaim("userId")).longValue(), gameId);
    }

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GameDTO startGame() throws OutOfBorderException, IllegalCheckerPosition {
        // in this API path we take the user ID from JWT
        // TODO: break the hack API path move it to user info paths
        return gameService.startGame(((JsonNumber)jwt.getClaim("userId")).longValue());
    }

    @GET
    @RolesAllowed({"user", "admin"})
    @Path("/{id}")
    public List<CheckerDTO> getGameCheckers(@PathParam("id") Long id){
        return gameService.getGameCheckers(id);
    }


    public class FullGameInfo{
        public GameDTO gameInfo;
        public List<CheckerDTO> checkers;
    }
    @GET
    @RolesAllowed({"user", "admin"})
    @Path("/{id}/fullInfo")
    public FullGameInfo getFullGameInfo(@PathParam("id") Long gameId){
        FullGameInfo res = new FullGameInfo();
        res.checkers = gameService.getGameCheckers(gameId);
        List<GameDTO> gameList = gameService.getUserGames(((JsonNumber)jwt.getClaim("userId")).longValue(), gameId);
        if (gameList.size() > 0)
            res.gameInfo = gameList.get(0);
        return res;
    }

    @GET
    @RolesAllowed({"user", "admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/step")
    public List<String> getAvailableSteps(@PathParam("id") Long gameId, @QueryParam("position") String currentCheckerPosNotation)
            throws OutOfBorderException, GameStateError,
            CheckerGameStep.CheckerStepApplyException,
            IllegalCheckerPosition {
        return gameService.getAvailableStepsForCheckerAtPos(gameId, currentCheckerPosNotation);
    }

    public static class MakeStepJSON {
        public String from;
        public String to;
    }

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/step")
    public void makeStep(@PathParam("id") Long gameId, MakeStepJSON makeStepObj)
            throws OutOfBorderException,
            IllegalCheckerPosition, GameStateError {
        gameService.makeStep(gameId,
                ((JsonNumber)jwt.getClaim("userId")).longValue(),
                makeStepObj.from, makeStepObj.to);
    }

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/connect")
    public Response connectToGame(@PathParam("id") Long gameId) {
        // the request should be from user which one connecting to game
        gameService.connectToGame(gameId, ((JsonNumber)jwt.getClaim("userId")).longValue());
        return Response.ok("User connected").build();
    }

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/finish")
    public void finishGame(@PathParam("id") Long gameId) {
        gameService.finishGame(gameId);
    }

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/capitulate")
    public Response capitulateGame(@PathParam("id") Long gameId) {
        gameService.capitulateGameByUser(gameId, ((JsonNumber)jwt.getClaim("userId")).longValue());
        return Response.ok("User were capitulated").build();
    }
}
