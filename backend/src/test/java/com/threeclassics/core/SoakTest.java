package com.threeclassics.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.checkers.CheckersAi;
import com.threeclassics.checkers.CheckersEngine;
import com.threeclassics.checkers.CheckersState;
import com.threeclassics.connectfour.ConnectFourAi;
import com.threeclassics.connectfour.ConnectFourEngine;
import java.time.Duration;
import java.util.Random;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.Test;

/** Plays many seeded Easy-vs-Easy games per game type and checks invariants after every move. */
class SoakTest {

    private static final int GAMES = 500;
    private static final int MAX_PLIES = 2_000;

    @Test
    void connectFourGamesRunToCompletion() {
        soak(new ConnectFourEngine(), new ConnectFourAi(), (before, after) -> {
        });
    }

    @Test
    void checkersGamesRunToCompletionAndPieceCountsNeverIncrease() {
        soak(new CheckersEngine(), new CheckersAi(), (CheckersState before, CheckersState after) -> {
            assertThat(pieces(after, Side.P1)).isLessThanOrEqualTo(pieces(before, Side.P1));
            assertThat(pieces(after, Side.P2)).isLessThanOrEqualTo(pieces(before, Side.P2));
        });
    }

    private static long pieces(CheckersState state, Side side) {
        String mine = side == Side.P1 ? "dD" : "lL";
        return state.board().chars().filter(c -> mine.indexOf(c) >= 0).count();
    }

    static <S, M> void soak(GameEngine<S, M> engine, AiPlayer<S, M> ai, BiConsumer<S, S> invariant) {
        Random rng = new Random(42);
        for (int game = 0; game < GAMES; game++) {
            S state = engine.initialState();
            int plies = 0;
            while (engine.outcome(state).isEmpty()) {
                M move = ai.chooseMove(state, Difficulty.EASY, rng, Duration.ofMillis(200));
                assertThat(engine.legalMoves(state)).as("game %d ply %d", game, plies).contains(move);
                S next = engine.apply(state, move, rng);
                invariant.accept(state, next);
                state = next;
                assertThat(++plies).as("game %d never ended", game).isLessThan(MAX_PLIES);
            }
            assertThat(engine.legalMoves(state)).isEmpty();
        }
    }
}
