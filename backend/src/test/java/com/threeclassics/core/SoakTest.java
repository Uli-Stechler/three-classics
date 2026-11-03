package com.threeclassics.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.connectfour.ConnectFourAi;
import com.threeclassics.connectfour.ConnectFourEngine;
import java.time.Duration;
import java.util.Random;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/** Plays many seeded Easy-vs-Easy games per game type and checks invariants after every move. */
class SoakTest {

    private static final int GAMES = 500;
    private static final int MAX_PLIES = 2_000;

    @Test
    void connectFourGamesRunToCompletion() {
        soak(new ConnectFourEngine(), new ConnectFourAi(), state -> {
        });
    }

    static <S, M> void soak(GameEngine<S, M> engine, AiPlayer<S, M> ai, Consumer<S> invariant) {
        Random rng = new Random(42);
        for (int game = 0; game < GAMES; game++) {
            S state = engine.initialState();
            int plies = 0;
            while (engine.outcome(state).isEmpty()) {
                M move = ai.chooseMove(state, Difficulty.EASY, rng, Duration.ofMillis(200));
                assertThat(engine.legalMoves(state)).as("game %d ply %d", game, plies).contains(move);
                state = engine.apply(state, move, rng);
                invariant.accept(state);
                assertThat(++plies).as("game %d never ended", game).isLessThan(MAX_PLIES);
            }
            assertThat(engine.legalMoves(state)).isEmpty();
        }
    }
}
