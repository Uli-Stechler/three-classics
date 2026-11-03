package com.threeclassics.backgammon;

import com.threeclassics.backgammon.BackgammonMove.Step;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates every legal way to play a roll.
 *
 * <p>A depth-first search tries the dice in both orders (or four times for doubles) and records each
 * sequence where no die can be played further. Then the rules filter it: the player must use as many
 * dice as possible, and if only one die can be used and either could be, it must be the larger one.
 */
final class Turns {

    private Turns() {
    }

    /** Every legal step sequence, in order of play. Empty if the roll can't be used at all. */
    static List<List<Step>> legal(Position position, int die1, int die2) {
        List<List<Step>> all = new ArrayList<>();
        int[] dice = die1 == die2 ? new int[] {die1, die1, die1, die1} : new int[] {die1, die2};
        search(position, dice, new boolean[dice.length], new ArrayList<>(), all);

        int most = all.stream().mapToInt(List::size).max().orElse(0);
        if (most == 0) {
            return List.of();
        }
        List<List<Step>> longest = all.stream().filter(sequence -> sequence.size() == most).toList();
        if (most == 1 && die1 != die2) {
            int larger = Math.max(die1, die2);
            boolean largerPlayable = longest.stream().anyMatch(sequence -> sequence.getFirst().die() == larger);
            if (largerPlayable) {
                return longest.stream().filter(sequence -> sequence.getFirst().die() == larger).toList();
            }
        }
        return longest;
    }

    /** The legal sequences with one representative per distinct resulting position. */
    static List<List<Step>> distinct(Position position, List<List<Step>> sequences) {
        Map<String, List<Step>> byResult = new LinkedHashMap<>();
        for (List<Step> sequence : sequences) {
            byResult.putIfAbsent(result(position, sequence).key(), sequence);
        }
        return new ArrayList<>(byResult.values());
    }

    static Position result(Position position, List<Step> sequence) {
        Position next = position.copy();
        sequence.forEach(next::apply);
        return next;
    }

    private static void search(Position position, int[] dice, boolean[] used, List<Step> sequence, List<List<Step>> out) {
        boolean moved = false;
        // For doubles every die is the same, so only the first unused one needs trying.
        boolean doubles = dice.length == 4;
        for (int i = 0; i < dice.length; i++) {
            if (used[i]) continue;
            List<Step> steps = position.steps(dice[i]);
            for (Step step : steps) {
                moved = true;
                Position next = position.copy();
                next.apply(step);
                used[i] = true;
                sequence.add(step);
                search(next, dice, used, sequence, out);
                sequence.removeLast();
                used[i] = false;
            }
            if (doubles) break;
        }
        if (!moved) {
            out.add(List.copyOf(sequence));
        }
    }
}
