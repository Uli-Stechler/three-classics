package com.threeclassics.backgammon;

import com.threeclassics.core.Side;
import java.util.List;

/**
 * A backgammon position.
 *
 * <p>{@code points} has 24 entries from P1's point of view: entry {@code i} is P1's point
 * {@code i + 1}. Positive counts are P1 (ivory) checkers, negative counts P2 (obsidian). P1 moves
 * from 24 toward 1; P2's point {@code q} is P1's point {@code 25 - q}.
 *
 * @param phase   OPENING (one roll decides who starts), ROLL (the side to move must roll) or MOVE
 *                (the side to move must play {@code dice})
 * @param dice    the current or most recent roll, two values; doubles are played four times
 * @param opening the opening roll as [P1's die, P2's die] until the next roll; null otherwise
 * @param notices turns passed automatically since the last action (no legal moves, closed out)
 * @param lastTurn the most recent turn played, for highlighting; null if none yet
 */
public record BackgammonState(
        List<Integer> points,
        int barP1,
        int barP2,
        int offP1,
        int offP2,
        Side sideToMove,
        Phase phase,
        List<Integer> dice,
        List<Integer> opening,
        List<Notice> notices,
        BackgammonMove lastTurn) {

    public static final int CHECKERS = 15;

    public BackgammonState {
        if (points.size() != 24) {
            throw new IllegalArgumentException("A backgammon board has 24 points");
        }
        points = List.copyOf(points);
        dice = dice == null ? List.of() : List.copyOf(dice);
        notices = notices == null ? List.of() : List.copyOf(notices);
    }

    public enum Phase {
        OPENING,
        ROLL,
        MOVE
    }

    public enum NoticeKind {
        NO_MOVES,
        CLOSED_OUT
    }

    /** A turn that passed without a move. */
    public record Notice(NoticeKind kind, Side side) {
    }
}
