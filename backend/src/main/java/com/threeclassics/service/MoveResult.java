package com.threeclassics.service;

import java.util.List;

/** The response to creating a game or making a move. */
public record MoveResult(GameView game, List<GameEvent> events) {
}
