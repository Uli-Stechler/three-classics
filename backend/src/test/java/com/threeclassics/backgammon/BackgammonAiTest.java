package com.threeclassics.backgammon;

import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.Side;
import java.time.Duration;
import java.util.Random;
import org.junit.jupiter.api.Test;

class BackgammonAiTest {

    private final BackgammonAi ai = new BackgammonAi();
    private final BackgammonEngine engine = new BackgammonEngine();

    @Test
    void theComputerRollsWhenItIsTimeToRoll() {
        assertThat(ai.chooseMove(engine.initialState(), Difficulty.HARD, new Random(1), Duration.ofSeconds(1)))
                .isEqualTo(BackgammonMove.ROLL);
    }

    @Test
    void hardAnswersWithinTheTwoSecondCapOnEveryOpeningRoll() {
        for (int a = 1; a <= 6; a++) {
            for (int b = 1; b <= 6; b++) {
                BackgammonState state = Board.empty()
                        .ivory(24, 2).ivory(13, 5).ivory(8, 3).ivory(6, 5)
                        .obsidian(24, 2).obsidian(13, 5).obsidian(8, 3).obsidian(6, 5)
                        .toMove(Side.P1, a, b);
                long start = System.nanoTime();
                BackgammonMove chosen = ai.chooseMove(state, Difficulty.HARD, new Random(1), Duration.ofSeconds(1));
                long elapsedMs = (System.nanoTime() - start) / 1_000_000;
                assertThat(elapsedMs).as("roll %d-%d", a, b).isLessThan(2000);
                assertThat(engine.legalMoves(state)).contains(chosen);
            }
        }
    }

    @Test
    void hardHitsAnExposedBlotInItsHomeBoard() {
        // Obsidian has a lone checker on Ivory's 5-point; 8/5* hits it and makes Ivory safer.
        BackgammonState state = Board.empty()
                .ivory(8, 3).ivory(6, 5).ivory(13, 5).ivory(24, 2)
                .obsidian(20, 1).obsidian(13, 5).obsidian(8, 3).obsidian(6, 5).obsidian(24, 1)
                .toMove(Side.P1, 3, 1);
        BackgammonMove chosen = ai.chooseMove(state, Difficulty.HARD, new Random(1), Duration.ofSeconds(1));
        assertThat(chosen.steps()).anyMatch(BackgammonMove.Step::hit);
    }

    @Test
    void mediumMakesThePointOnAThreeOne() {
        // The classic 3-1 opening: 8/5 6/5 makes the 5-point.
        BackgammonState state = Board.empty()
                .ivory(24, 2).ivory(13, 5).ivory(8, 3).ivory(6, 5)
                .obsidian(24, 2).obsidian(13, 5).obsidian(8, 3).obsidian(6, 5)
                .toMove(Side.P1, 3, 1);
        BackgammonState after = engine.apply(state,
                ai.chooseMove(state, Difficulty.MEDIUM, new Random(1), Duration.ofSeconds(1)), new ScriptedDice());
        assertThat(after.points().get(4)).isEqualTo(2);
    }
}
