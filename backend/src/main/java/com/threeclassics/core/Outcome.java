package com.threeclassics.core;

/**
 * How a game ended.
 *
 * @param winner null for a draw
 * @param reason short explanation shown under the result title, e.g. "Four in a row (diagonal)"
 * @param points 1 for most wins; backgammon gammons and backgammons score 2 and 3
 */
public record Outcome(Side winner, String reason, int points) {

    public static Outcome win(Side winner, String reason) {
        return new Outcome(winner, reason, 1);
    }

    public static Outcome win(Side winner, String reason, int points) {
        return new Outcome(winner, reason, points);
    }

    public static Outcome draw(String reason) {
        return new Outcome(null, reason, 0);
    }

    public boolean isDraw() {
        return winner == null;
    }
}
