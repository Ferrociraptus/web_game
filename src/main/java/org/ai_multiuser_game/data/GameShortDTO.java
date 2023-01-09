package org.ai_multiuser_game.data;

import org.ai_multiuser_game.entities.GameStatus;

public class GameShortDTO {
    public Integer id;
    public Integer opponentId;
    public String opponentLogin;
    public String playerColor;
    public GameStatus status;
}
