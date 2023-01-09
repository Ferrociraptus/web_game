package org.ai_multiuser_game.controllers;

import io.quarkus.security.Authenticated;
import org.ai_multiuser_game.data.FullUserDTO;
import org.ai_multiuser_game.data.StartupUserDTO;
import org.ai_multiuser_game.data.UserDataDTO;
import org.ai_multiuser_game.services.UsersService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Path("api/user")
@Authenticated
public class UsersController {

    @Inject
    UsersService usersService;

    @GET
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FullUserDTO> getUsers(){
        return usersService.getUsers();
    }

    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Long> createUser(StartupUserDTO newUserData){
        return Map.of("id", usersService.createUser(newUserData));
    }

    @GET
    @RolesAllowed({"admin", "user"})
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDataDTO getUserInformation(@PathParam("id") Long id){
        return usersService.getUserById(id);
    }

    @POST
    @RolesAllowed({"admin", "user"})
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeParams(FullUserDTO newUserData){
        usersService.updateUsersData(newUserData);
    }

}
