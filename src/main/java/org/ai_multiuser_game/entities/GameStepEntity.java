package org.ai_multiuser_game.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;

@Entity
public class GameStepEntity extends PanacheEntity {

    @OneToOne
    public GameEntity game;
    public String stepNotation;

    public Integer stepNumber = 1;


    @Transactional
    public static void saveStep(GameEntity game, String stepNotation, Integer stepNumber){
        GameStepEntity step = new GameStepEntity();
        step.game = game;
        step.stepNotation = stepNotation;
        step.stepNumber = stepNumber;
        step.persistAndFlush();
    }

    public static Integer getLastGameStep(GameEntity game){
        GameStepEntity gameStep = GameStepEntity
                .find("from GameStepEntity" +
                        " where game_id = ?1" +
                        " order by stepNotation desc", game.id)
                .firstResult();
        if (gameStep == null)
            return 0;
        return gameStep.stepNumber;
    }
    @Transactional
    public static void saveStep(GameEntity game, String stepNotation){
        GameStepEntity step = new GameStepEntity();
        step.game = game;
        step.stepNotation = stepNotation;
        Integer lastStep = getLastGameStep(game);
        step.stepNumber = lastStep + 1;
        step.persistAndFlush();
    }

    public static void saveStep(Long gameId, String stepNotation){
        GameEntity game = GameEntity.findById(gameId);
        saveStep(game, stepNotation);
    }
}
