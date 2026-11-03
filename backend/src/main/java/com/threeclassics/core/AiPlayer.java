package com.threeclassics.core;

import java.time.Duration;
import java.util.random.RandomGenerator;

/**
 * A computer opponent. It must only return moves taken from the engine's legal moves, and all of
 * its randomness must come from {@code rng} so that games are reproducible in tests.
 */
public interface AiPlayer<S, M> {

    M chooseMove(S state, Difficulty difficulty, RandomGenerator rng, Duration budget);
}
