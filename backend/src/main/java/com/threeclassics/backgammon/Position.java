package com.threeclassics.backgammon;

import static com.threeclassics.backgammon.BackgammonMove.BAR;
import static com.threeclassics.backgammon.BackgammonMove.OFF;

import com.threeclassics.backgammon.BackgammonMove.Step;
import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The board from one side's point of view, mutable, for move generation and search.
 *
 * <p>{@code mine[p]} and {@code theirs[p]} count checkers on the mover's point {@code p} (1–24).
 * {@code mine[25]} is the mover's bar and {@code mine[0]} their borne-off checkers. The opponent's
 * bar is {@code theirs[0]} (they enter on the mover's low points) and their borne-off checkers are
 * {@code theirs[25]}.
 */
final class Position {

    final int[] mine;
    final int[] theirs;

    private Position(int[] mine, int[] theirs) {
        this.mine = mine;
        this.theirs = theirs;
    }

    static Position of(BackgammonState state, Side side) {
        int[] mine = new int[26];
        int[] theirs = new int[26];
        List<Integer> points = state.points();
        for (int p = 1; p <= 24; p++) {
            // P1's point p is entry p - 1; P2's point p is P1's point 25 - p, entry 24 - p.
            int count = side == Side.P1 ? points.get(p - 1) : -points.get(24 - p);
            if (count > 0) mine[p] = count;
            else theirs[p] = -count;
        }
        mine[BAR] = side == Side.P1 ? state.barP1() : state.barP2();
        mine[OFF] = side == Side.P1 ? state.offP1() : state.offP2();
        theirs[0] = side == Side.P1 ? state.barP2() : state.barP1();
        theirs[25] = side == Side.P1 ? state.offP2() : state.offP1();
        return new Position(mine, theirs);
    }

    Position copy() {
        return new Position(mine.clone(), theirs.clone());
    }

    /** Points from P1's point of view, signed, for storing back into a state. */
    List<Integer> pointsFor(Side side) {
        Integer[] points = new Integer[24];
        for (int p = 1; p <= 24; p++) {
            int signed = mine[p] - theirs[p];
            if (side == Side.P1) points[p - 1] = signed;
            else points[24 - p] = -signed;
        }
        return Arrays.asList(points);
    }

    boolean allHome() {
        for (int p = 7; p <= BAR; p++) {
            if (mine[p] > 0) return false;
        }
        return true;
    }

    /** Every single step a die of {@code die} allows right now. */
    List<Step> steps(int die) {
        List<Step> steps = new ArrayList<>();
        if (mine[BAR] > 0) {
            int target = BAR - die;
            if (theirs[target] <= 1) steps.add(new Step(BAR, target, die, theirs[target] == 1));
            return steps;
        }
        boolean bearingOff = allHome();
        for (int p = 24; p >= 1; p--) {
            if (mine[p] == 0) continue;
            int target = p - die;
            if (target >= 1) {
                if (theirs[target] <= 1) steps.add(new Step(p, target, die, theirs[target] == 1));
            } else if (bearingOff && (target == 0 || isHighest(p))) {
                // Exact roll, or a higher roll from the highest occupied point.
                steps.add(new Step(p, OFF, die, false));
            }
        }
        return steps;
    }

    private boolean isHighest(int point) {
        for (int p = point + 1; p <= 6; p++) {
            if (mine[p] > 0) return false;
        }
        return true;
    }

    void apply(Step step) {
        mine[step.from()]--;
        if (step.to() == OFF) {
            mine[OFF]++;
            return;
        }
        if (theirs[step.to()] == 1) {
            theirs[step.to()] = 0;
            theirs[0]++;
        }
        mine[step.to()]++;
    }

    /** Total pips the mover still has to travel. */
    int pips() {
        int pips = 0;
        for (int p = 1; p <= BAR; p++) pips += p * mine[p];
        return pips;
    }

    /** Pips the opponent still has to travel, in their own numbering. */
    int theirPips() {
        int pips = 0;
        for (int p = 1; p <= 24; p++) pips += (25 - p) * theirs[p];
        return pips + 25 * theirs[0];
    }

    /** True once the two armies have passed each other and no hitting is possible. */
    boolean isRace() {
        int myRearmost = 0;
        for (int p = BAR; p >= 1; p--) {
            if (mine[p] > 0) {
                myRearmost = p;
                break;
            }
        }
        if (theirs[0] > 0) return false;
        for (int p = 1; p <= 24; p++) {
            if (theirs[p] > 0) return myRearmost < p;
        }
        return true;
    }

    String key() {
        return Arrays.toString(mine) + Arrays.toString(theirs);
    }
}
