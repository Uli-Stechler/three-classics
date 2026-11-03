package com.threeclassics.service;

import java.util.UUID;

/** The client's move was based on an older version of the game, e.g. after a double click. */
public class StaleVersionException extends RuntimeException {

    public StaleVersionException(UUID id, long requested, long current) {
        super("Game " + id + " is at version " + current + ", but the move was based on version " + requested + ".");
    }
}
