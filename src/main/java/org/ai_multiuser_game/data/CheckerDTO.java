package org.ai_multiuser_game.data;
import org.gamecore.*;

public class CheckerDTO {
    public String color;
    public String type;
    public String position;

    public CheckerDTO(RussianCheckers.Checker checker){
        color = checker.getColor().toString();
        type = checker.getType().toString();
        position = checker.getCurrentPosNotation();
    }

    public static CheckerDTO fromRussianChecker(RussianCheckers.Checker checker){
        return new CheckerDTO(checker);
    }
}
