package org.gamecore;

public class OutOfBorderException extends Exception {
    OutOfBorderException() {
        super();
    }

    OutOfBorderException(String msg) {
        super(msg);
    }
}
