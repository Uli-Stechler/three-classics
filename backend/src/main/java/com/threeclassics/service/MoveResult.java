package com.threeclassics.service;

import java.util.List;

/**
 * The response to creating a game or making a move.
 *
 * @param before the state before the first event, so the client can animate from it
 * @param events what happened, in order; empty when nothing moved
 */
public record MoveResult(GameView game, Object before, List<GameEvent> events) {
}
