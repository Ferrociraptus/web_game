package org.ai_multiuser_game.data;
import org.gamecore.RussianCheckers.Checker;

public class CheckerDTO {
    public String color;
    public String type;
    public String position;

    public CheckerDTO(Checker checker){
        color = checker.getColor().toString();
        type = checker.getType().toString();
        position = checker.getCurrentPosNotation();
    }

    public static CheckerDTO fromRussianChecker(Checker checker){
        return new CheckerDTO(checker);
    }
}
