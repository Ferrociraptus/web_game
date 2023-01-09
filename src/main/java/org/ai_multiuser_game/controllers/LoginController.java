package org.ai_multiuser_game.controllers;

import org.ai_multiuser_game.data.LoginData;
import org.ai_multiuser_game.data.UserDTO;
import org.ai_multiuser_game.services.LoginService;
import org.ai_multiuser_game.services.UsersService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/api/login")
@ApplicationScoped
public class LoginController {

    @Inject
    LoginService loginService;

    @Inject
    UsersService usersService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginData data){
        String token = loginService.authorizeUser(data);
        UserDTO user = usersService.getUserByUsername(data.login);
        return Response.ok(Map.of("token", token, "user", user)).build();
    }
}
