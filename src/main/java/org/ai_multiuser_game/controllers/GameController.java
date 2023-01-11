package org.ai_multiuser_game.controllers;

import org.ai_multiuser_game.data.CheckerDTO;
import org.ai_multiuser_game.data.GameDTO;
import org.ai_multiuser_game.services.GameService;
import org.eclipse.microprofile.jwt.Claim;
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
    public List<GameDTO> returnUserGames(){
        // in this API path we take the user ID from JWT
        // TODO: break the hack API path move it to user info paths
        return gameService.getUserGames(((JsonNumber)jwt.getClaim("userId")).longValue());
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
            IllegalCheckerPosition {
        gameService.makeStep(gameId,
                ((JsonNumber)jwt.getClaim("userId")).longValue(),
                makeStepObj.from, makeStepObj.to);
    }

    public static class GameConnectJSON{
        public Long gameId;
        public Long secondUserId;
    }
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/connect")
    public void connectToGame(@PathParam("id") Long gameId, GameConnectJSON makeStepObj) {
        gameService.connectToGame(gameId, makeStepObj.secondUserId);
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
    public void capitulateGame(@PathParam("id") Long gameId) {
        gameService.finishGame(gameId);
    }
}
