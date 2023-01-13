package org.ai_multiuser_game.controllers;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("test/hello")
public class HelloWorldController {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        return  "Hello, world";
    }

    @GET
    @RolesAllowed("user")
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("name") String name){
        return  "Hello, " + name;
    }
}
