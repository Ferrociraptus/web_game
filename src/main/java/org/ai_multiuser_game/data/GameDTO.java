package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.CheckerColor;
import org.ai_multiuser_game.entities.GameStatus;

public class GameDTO {
    public Long id;
    public Long firstUserId;
    public Long secondUserId;
    public CheckerColor firstUserColor;
    public CheckerColor secondUserColor;
    public String firstUserLogin;
    public String secondUserLogin;
    public GameStatus status;
    public Long winnerId;

    public CheckerColor turnOf = null;

    public CheckerColor getWinnerSideColor(){
        if (winnerId != null){
            if (winnerId.equals(firstUserId))
                return firstUserColor;
            else if (winnerId.equals(secondUserId))
                return secondUserColor;
        }
        return null;
    }
}
