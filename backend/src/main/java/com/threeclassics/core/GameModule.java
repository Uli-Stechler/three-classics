package com.threeclassics.core;

/**
 * Everything the service needs to host one game: its rules, its computer player and the JSON
 * types of its state and moves.
 */
public record GameModule<S, M>(
        GameType type,
        GameEngine<S, M> engine,
        AiPlayer<S, M> ai,
        Class<S> stateType,
        Class<M> moveType) {
}
