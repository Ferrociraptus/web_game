package org.gamecore;

public class GameStateError extends Exception {
    GameStateError() {
        super();
    }

    GameStateError(String msg) {
        super(msg);
    }
}
