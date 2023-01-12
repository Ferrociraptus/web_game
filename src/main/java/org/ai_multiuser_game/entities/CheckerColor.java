package org.ai_multiuser_game.entities;


import org.gamecore.RussianCheckers.Checker;

public enum CheckerColor {
    BLACK,
    WHITE;

    static public CheckerColor fromGameColor(Checker.CheckerColor color){
        return (color == Checker.CheckerColor.Black) ? BLACK : WHITE;
    }
}
