package com.threeclassics.backgammon;

import com.threeclassics.backgammon.BackgammonState.Phase;
import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds backgammon positions with each side counted in its own numbering. Checkers not placed on
 * the board or bar are borne off, so each side always totals 15.
 */
final class Board {

    private final int[] ivory = new int[26];
    private final int[] obsidian = new int[26];

    static Board empty() {
        return new Board();
    }

    /** P1 checkers on P1's point. */
    Board ivory(int point, int count) {
        ivory[point] += count;
        return this;
    }

    /** P2 checkers on P2's own point. */
    Board obsidian(int point, int count) {
        obsidian[point] += count;
        return this;
    }

    Board ivoryBar(int count) {
        return ivory(25, count);
    }

    Board obsidianBar(int count) {
        return obsidian(25, count);
    }

    BackgammonState toMove(Side side, int die1, int die2) {
        return state(side, Phase.MOVE, List.of(die1, die2));
    }

    BackgammonState toRoll(Side side) {
        return state(side, Phase.ROLL, List.of());
    }

    private BackgammonState state(Side side, Phase phase, List<Integer> dice) {
        List<Integer> points = new ArrayList<>();
        for (int p = 1; p <= 24; p++) {
            points.add(ivory[p] - obsidian[25 - p]);
        }
        int ivoryLeft = Arrays.stream(ivory).sum();
        int obsidianLeft = Arrays.stream(obsidian).sum();
        return new BackgammonState(points, ivory[25], obsidian[25], 15 - ivoryLeft, 15 - obsidianLeft,
                side, phase, dice, null, List.of(), null);
    }
}
