package com.threeclassics.backgammon;

import java.util.List;

/**
 * Either a roll or a whole turn.
 *
 * <p>Clients send {@code {"kind":"ROLL"}} to roll; the server records the dice it rolled. A turn
 * lists every checker step in order. Steps use the mover's own point numbers: 25 is the bar and 0
 * is borne off.
 *
 * @param dice the dice this roll produced, or the dice this turn plays
 */
public record BackgammonMove(Kind kind, List<Step> steps, List<Integer> dice) {

    public BackgammonMove {
        steps = steps == null ? List.of() : List.copyOf(steps);
        dice = dice == null ? null : List.copyOf(dice);
    }

    public static final BackgammonMove ROLL = new BackgammonMove(Kind.ROLL, List.of(), null);

    public enum Kind {
        /** Roll the dice. */
        ROLL,
        /** As recorded: the opening roll, one die each as [P1, P2]. */
        OPENING,
        /** Play the rolled dice. */
        TURN
    }

    public static final int BAR = 25;
    public static final int OFF = 0;

    /** One checker moving one die's worth. {@code hit} is true when it lands on a lone opponent. */
    public record Step(int from, int to, int die, boolean hit) {
    }
}
