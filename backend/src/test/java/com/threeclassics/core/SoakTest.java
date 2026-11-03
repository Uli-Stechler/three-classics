package com.threeclassics.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.backgammon.BackgammonAi;
import com.threeclassics.backgammon.BackgammonEngine;
import com.threeclassics.backgammon.BackgammonState;
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

    @Test
    void backgammonGamesRunToCompletionWithFifteenCheckersEach() {
        soak(new BackgammonEngine(), new BackgammonAi(), (BackgammonState before, BackgammonState after) -> {
            int ivory = after.barP1() + after.offP1();
            int obsidian = after.barP2() + after.offP2();
            for (int count : after.points()) {
                if (count > 0) ivory += count;
                else obsidian -= count;
            }
            assertThat(ivory).isEqualTo(15);
            assertThat(obsidian).isEqualTo(15);
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
