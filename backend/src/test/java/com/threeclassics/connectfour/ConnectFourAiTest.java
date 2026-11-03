package com.threeclassics.connectfour;

import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.Side;
import java.time.Duration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class ConnectFourAiTest {

    private static final Duration BUDGET = Duration.ofSeconds(1);
    private final ConnectFourAi ai = new ConnectFourAi();

    private int hardMove(ConnectFourState state) {
        return ai.chooseMove(state, Difficulty.HARD, new Random(7), BUDGET).column();
    }

    @Test
    void hardOpensInTheCentreWhenItMovesFirst() {
        assertThat(hardMove(new ConnectFourEngine().initialState())).isEqualTo(3);
    }

    @Test
    void hardTakesAnImmediateWinEvenWhenThreatened() {
        // Coral wins in column 5; gold also threatens column 1.
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                O......
                O......
                OXXX...
                """, Side.P1);
        assertThat(hardMove(state)).isEqualTo(4);
    }

    @Test
    void hardTakesAnImmediateWinAsTheSecondPlayer() {
        // Gold wins in column 1; coral threatens column 7.
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                O.....X
                O.....X
                O..X..X
                """, Side.P2);
        assertThat(hardMove(state)).isEqualTo(0);
    }

    @Test
    void hardBlocksASingleHorizontalThreat() {
        // Coral's only immediate win is column 1.
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                .......
                ...O...
                .XXXO..
                """, Side.P2);
        assertThat(hardMove(state)).isEqualTo(0);
    }

    @Test
    void hardBlocksASingleVerticalThreat() {
        // Coral's only immediate win is the top of column 6.
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .....X.
                .....X.
                .....X.
                ..O..O.
                """, Side.P2);
        assertThat(hardMove(state)).isEqualTo(5);
    }

    @RepeatedTest(3)
    void hardAnswersWithinTheTwoSecondCap() {
        ConnectFourState midgame = Boards.parse("""
                .......
                .......
                ...X...
                ..OO...
                ..XXO..
                .OXOX..
                """, Side.P1);
        long start = System.nanoTime();
        int column = hardMove(midgame);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        assertThat(elapsedMs).isLessThan(2000);
        assertThat(new ConnectFourEngine().legalMoves(midgame)).contains(new ConnectFourMove(column));
    }

    @Test
    void mediumAndEasyVaryTheirOpenings() {
        ConnectFourState start = new ConnectFourEngine().initialState();
        Random rng = new Random(3);
        Set<Integer> medium = new HashSet<>();
        Set<Integer> easy = new HashSet<>();
        for (int i = 0; i < 40; i++) {
            medium.add(ai.chooseMove(start, Difficulty.MEDIUM, rng, BUDGET).column());
            easy.add(ai.chooseMove(start, Difficulty.EASY, rng, BUDGET).column());
        }
        assertThat(medium).hasSizeGreaterThan(1);
        assertThat(easy).hasSizeGreaterThan(2);
    }
}
