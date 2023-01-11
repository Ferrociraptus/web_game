package org.ai_multiuser_game.controllers;


import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;


@ApplicationScoped
@Path("/frontend")
public class FrontEndController {

    @GET
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    public Response redirectToFrontend(){
        return Response.seeOther(URI.create("https://pashabezk.github.io/CheckersOnlineFrontend")).build();
    }
}

