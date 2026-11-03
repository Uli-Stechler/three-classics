package com.threeclassics.service;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(UUID id) {
        super("No game with id " + id + ".");
    }
}
