package org.ai_multiuser_game.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import org.ai_multiuser_game.data.GameDTO;
import org.ai_multiuser_game.data.UserDTO;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class GameEntity extends PanacheEntity {


    @OneToOne
    public UserEntity user1 = null;

    public CheckerColor userColor1 = CheckerColor.WHITE;
    @OneToOne
    public UserEntity user2 = null;

    public CheckerColor userColor2 = CheckerColor.BLACK;
    public Timestamp startTime;
    public Timestamp endTime = null;

    public GameStatus status;

    public GameEntity(){super();};

    @Transactional
    public static GameDTO registerGame(UserEntity user){
        GameEntity gameEntity = new GameEntity();
        gameEntity.startTime = new Timestamp(System.currentTimeMillis());
        gameEntity.user1 = user;
        gameEntity.status = GameStatus.INIT;
        gameEntity.persistAndFlush();
        return gameEntity.toGameDTO();
    }

    @Transactional
    public static GameDTO registerGame(UserEntity user1, UserEntity user2){
        GameEntity gameEntity = new GameEntity();
        gameEntity.startTime = new Timestamp(System.currentTimeMillis());
        gameEntity.user1 = user1;
        gameEntity.user2 = user2;
        gameEntity.status = GameStatus.IN_PROCESS;
        gameEntity.persistAndFlush();
        return gameEntity.toGameDTO();
    }

    @Transactional
    public static void registerGame(GameDTO game){
        GameEntity gameEntity = new GameEntity();
        gameEntity.user1 = UserEntity.findById(game.firstUserId);
        gameEntity.user2 = UserEntity.findById(game.secondUserId);
        gameEntity.status = game.status;
        gameEntity.startTime = new Timestamp(System.currentTimeMillis());
        gameEntity.persistAndFlush();
    }

    @Transactional
    public static GameDTO registerGame(UserDTO user){
        return GameEntity.registerGame(UserEntity.getFromUserDTO(user));
    }

    @Transactional
    public static GameDTO registerGame(Long userId){
        return GameEntity.registerGame(UserEntity.findById(userId));
    }

    public GameDTO toGameDTO(){
        GameDTO game = new GameDTO();
        game.id = id;
        game.firstUserId = user1.id;
        if (game.secondUserId != null) {
            game.secondUserId = user2.id;
            game.secondUserLogin = user2.username;
        }
        game.firstUserColor = userColor1;
        game.secondUserColor = userColor2;
        game.firstUserLogin = user1.username;
        game.status = status;
        return game;
    }
    @Transactional
    public static void addUserToGame(GameDTO game, UserDTO secondUser){
        GameEntity gameEntity = GameEntity.findById(game.id);

        if (gameEntity.user2 != null)
            return;

        gameEntity.user2 = UserEntity.getFromUserDTO(secondUser);
        gameEntity.status = GameStatus.IN_PROCESS;
        gameEntity.persistAndFlush();
    }

    @Transactional
    public static void addUserToGame(GameDTO game, Long secondUserId){
        GameEntity gameEntity = GameEntity.findById(game.id);

        if (gameEntity.user2 != null)
            return;

        gameEntity.user2 = UserEntity.findById(secondUserId);
        gameEntity.status = GameStatus.IN_PROCESS;
        gameEntity.persistAndFlush();
    }
    @Transactional
    public static void finishGame(GameDTO game){
        GameEntity gameEntity = GameEntity.findById(game.id);
        gameEntity.endTime = new Timestamp(System.currentTimeMillis());
        gameEntity.persistAndFlush();
    }

    public Long getId(){
        return id;
    }

    public static List<GameDTO> getUsersGamesByUserID(Long userId){
        UserEntity user = UserEntity.findById(userId);
        return GameEntity
                .find(
                        "from GameEntity as g" +
                                " where g.user1 = ?1 or g.user2 = ?2" +
                                " order by g.status",
                        user, user)
                .stream()
                .map(e -> ((GameEntity)e).toGameDTO())
                .toList();
    }
}
