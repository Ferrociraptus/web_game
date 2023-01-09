package org.ai_multiuser_game.services;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import org.ai_multiuser_game.data.LoginData;
import org.ai_multiuser_game.entities.UserEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import java.util.Set;

@Singleton
public class LoginService {

    @ConfigProperty(name = "login.jwt.expiration_time", defaultValue = "3600")
    Integer expirationTime;

    public String authorizeUser(LoginData loginData){

        UserEntity user = UserEntity.getByUsername(loginData.login);
        if (user == null)
            throw new WebApplicationException(400);
        if (! BcryptUtil.matches(loginData.password, user.password)){
            throw new WebApplicationException(400);
        }

        return Jwt.issuer("ai-multiuser-game")
                .subject(user.username)
                .groups(Set.of(user.role))
                .claim("userId", user.id)
                .expiresAt(System.currentTimeMillis() + expirationTime)
                .sign();
    }
}
