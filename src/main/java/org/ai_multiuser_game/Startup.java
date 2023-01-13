package org.ai_multiuser_game;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import io.quarkus.runtime.ShutdownEvent;
import org.ai_multiuser_game.entities.GameStatus;
import org.ai_multiuser_game.entities.UserEntity;

import io.quarkus.runtime.StartupEvent;
import org.ai_multiuser_game.services.GameService;


@Singleton
public class Startup {

    @Inject
    public GameService gameService;

    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        // reset and load all test users
//        UserEntity.deleteAll();
//        UserEntity.add("admin", "admin", "admin", "admin", "admin");
//        UserEntity.add("test", "test", "user", "test", "test");
//        UserEntity.add("user", "user", "user", "user", "user");
        gameService.finishAllGames();
    }

    void onStop(@Observes ShutdownEvent ev) {
        gameService.finishAllGames();
    }


}