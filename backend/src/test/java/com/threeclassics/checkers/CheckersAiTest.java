package com.threeclassics.checkers;

import static com.threeclassics.checkers.Position.move;
import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.Side;
import java.time.Duration;
import java.util.Random;
import org.junit.jupiter.api.Test;

class CheckersAiTest {

    private final CheckersAi ai = new CheckersAi();
    private final CheckersEngine engine = new CheckersEngine();

    @Test
    void hardAnswersWithinTheTwoSecondCapFromTheOpening() {
        assertAnswersInTime(engine.initialState());
    }

    @Test
    void hardAnswersWithinTheTwoSecondCapInAMiddlegame() {
        CheckersState state = Position.empty()
                .put('d', "a1", "c1", "e1", "b2", "d2", "c3", "e3", "f4")
                .put('D', "d6")
                .put('l', "b8", "d8", "h8", "a7", "c7", "e7", "b6", "f6", "h6")
                .put('L', "g3")
                .toMove(Side.P2);
        assertAnswersInTime(state);
    }

    @Test
    void hardAvoidsHangingAPiece() {
        // Dark's c3 man can step into d4, where e5 would capture it, or to b4, which is safe.
        CheckersState state = Position.empty().put('d', "c3", "a1").put('l', "e5", "h8").toMove(Side.P1);
        CheckersMove chosen = ai.chooseMove(state, Difficulty.HARD, new Random(1), Duration.ofSeconds(1));
        assertThat(chosen).isNotEqualTo(move("c3", "d4"));
    }

    private void assertAnswersInTime(CheckersState state) {
        long start = System.nanoTime();
        CheckersMove chosen = ai.chooseMove(state, Difficulty.HARD, new Random(5), Duration.ofSeconds(1));
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        assertThat(elapsedMs).isLessThan(2000);
        assertThat(engine.legalMoves(state)).contains(chosen);
    }
}
