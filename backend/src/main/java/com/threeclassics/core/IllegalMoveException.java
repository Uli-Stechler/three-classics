package com.threeclassics.core;

/** Thrown when a move is not one of the legal moves for the current state. */
public class IllegalMoveException extends RuntimeException {

    public IllegalMoveException(String message) {
        super(message);
    }
}
