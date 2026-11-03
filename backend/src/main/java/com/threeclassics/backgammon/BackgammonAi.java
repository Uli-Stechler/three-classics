package com.threeclassics.backgammon;

import static com.threeclassics.backgammon.BackgammonMove.BAR;
import static com.threeclassics.backgammon.BackgammonMove.OFF;

import com.threeclassics.backgammon.BackgammonMove.Kind;
import com.threeclassics.backgammon.BackgammonMove.Step;
import com.threeclassics.backgammon.BackgammonState.Phase;
import com.threeclassics.core.AiPlayer;
import com.threeclassics.core.Difficulty;
import com.threeclassics.core.Side;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Easy plays a random legal turn. Medium picks the best resulting position by a heuristic (one ply).
 * Hard looks two plies ahead: for each candidate it averages the opponent's best reply over all 21
 * rolls, and switches to a pure race evaluation once the armies have passed each other.
 */
public final class BackgammonAi implements AiPlayer<BackgammonState, BackgammonMove> {

    private static final double MEDIUM_TOLERANCE = 1.5;
    private static final int HARD_CANDIDATES = 8;
    private static final int[][] ROLLS = rolls();

    @Override
    public BackgammonMove chooseMove(BackgammonState state, Difficulty difficulty, RandomGenerator rng, Duration budget) {
        if (state.phase() != Phase.MOVE) {
            return BackgammonMove.ROLL;
        }
        Position position = Position.of(state, state.sideToMove());
        List<Integer> dice = state.dice();
        List<List<Step>> legal = Turns.legal(position, dice.get(0), dice.get(1));
        if (legal.isEmpty()) {
            throw new IllegalStateException("No legal turn to choose from");
        }
        List<List<Step>> distinct = Turns.distinct(position, legal);
        List<Step> chosen = switch (difficulty) {
            case EASY -> distinct.get(rng.nextInt(distinct.size()));
            case MEDIUM -> medium(position, distinct, rng);
            case HARD -> hard(position, distinct, System.nanoTime() + budget.toNanos() * 85 / 100);
        };
        return new BackgammonMove(Kind.TURN, chosen, dice);
    }

    /** Greedy: the best position after the move, chosen at random among near-equal options. */
    private static List<Step> medium(Position position, List<List<Step>> turns, RandomGenerator rng) {
        double[] scores = new double[turns.size()];
        double best = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < turns.size(); i++) {
            scores[i] = evaluate(Turns.result(position, turns.get(i)));
            best = Math.max(best, scores[i]);
        }
        List<List<Step>> nearBest = new ArrayList<>();
        for (int i = 0; i < turns.size(); i++) {
            if (scores[i] >= best - MEDIUM_TOLERANCE) nearBest.add(turns.get(i));
        }
        return nearBest.get(rng.nextInt(nearBest.size()));
    }

    /**
     * Two-ply expectimax: keep the most promising candidates by the static evaluation, then score each
     * by the opponent's best reply averaged over every roll (doubles weigh 1/36, others 2/36).
     */
    private static List<Step> hard(Position position, List<List<Step>> turns, long deadline) {
        List<Scored> candidates = new ArrayList<>();
        for (List<Step> turn : turns) {
            Position after = Turns.result(position, turn);
            candidates.add(new Scored(turn, after, evaluate(after)));
        }
        candidates.sort(Comparator.comparingDouble(Scored::score).reversed());
        List<Step> best = candidates.getFirst().turn();
        if (candidates.size() == 1 || position.isRace() && candidates.getFirst().after().isRace()) {
            return best; // a race is played well greedily
        }

        double bestExpected = Double.NEGATIVE_INFINITY;
        for (Scored candidate : candidates.subList(0, Math.min(HARD_CANDIDATES, candidates.size()))) {
            if (candidate.after().mine[OFF] == BackgammonState.CHECKERS) {
                return candidate.turn();
            }
            Position theirView = flip(candidate.after());
            double expected = 0;
            for (int[] roll : ROLLS) {
                if (System.nanoTime() > deadline) {
                    return best;
                }
                List<List<Step>> replies = Turns.distinct(theirView, Turns.legal(theirView, roll[0], roll[1]));
                double theirBest = replies.isEmpty() ? evaluate(theirView) : Double.NEGATIVE_INFINITY;
                for (List<Step> reply : replies) {
                    theirBest = Math.max(theirBest, evaluate(Turns.result(theirView, reply)));
                }
                expected += roll[2] * -theirBest;
            }
            expected /= 36;
            if (expected > bestExpected) {
                bestExpected = expected;
                best = candidate.turn();
            }
        }
        return best;
    }

    private record Scored(List<Step> turn, Position after, double score) {
    }

    /**
     * How good a position is for the side that just moved (higher is better), roughly in units of a
     * point. A race is judged by pips; with contact, the heuristic weighs safety and structure.
     */
    static double evaluate(Position p) {
        if (p.mine[OFF] == BackgammonState.CHECKERS) return 100;
        if (p.theirs[25] == BackgammonState.CHECKERS) return -100;
        double pipLead = p.theirPips() - p.pips();
        if (p.isRace()) {
            return pipLead / 8.0 + 0.3 * (p.mine[OFF] - p.theirs[25]);
        }

        double score = pipLead / 15.0;
        score += 1.2 * p.theirs[0] - 1.2 * p.mine[BAR];
        int made = 0;
        int homeMade = 0;
        int run = 0;
        int longestPrime = 0;
        for (int point = 1; point <= 24; point++) {
            if (p.mine[point] >= 2) {
                made++;
                if (point <= 6) homeMade++;
                run++;
                longestPrime = Math.max(longestPrime, run);
            } else {
                run = 0;
            }
        }
        score += 0.25 * made + 0.35 * homeMade + 0.3 * Math.max(0, longestPrime - 1);
        // Home-board strength matters most when the opponent has checkers to bring in.
        score += homeMade * 0.25 * p.theirs[0];
        score -= blotRisk(p);
        score += 0.15 * p.mine[OFF];
        return score;
    }

    /** Penalty for blots that opposing checkers can hit with a single die (direct shots). */
    private static double blotRisk(Position p) {
        double risk = 0;
        for (int point = 1; point <= 24; point++) {
            if (p.mine[point] != 1) continue;
            int shooters = 0;
            for (int distance = 1; distance <= 6; distance++) {
                int from = point - distance;
                // Opponents move upward in our numbering; their bar sits at 0.
                if (from >= 0 && p.theirs[from] > 0) shooters++;
            }
            if (shooters == 0) continue;
            double exposure = Math.min(1.0, shooters * 11 / 36.0);
            // A blot hit deep in our own territory costs more pips than one near their side.
            risk += exposure * (0.4 + (25 - point) / 30.0);
        }
        return risk;
    }

    /** The same position from the opponent's side. */
    private static Position flip(Position p) {
        BackgammonState state = new BackgammonState(
                p.pointsFor(Side.P1),
                p.mine[BAR], p.theirs[0], p.mine[OFF], p.theirs[25],
                Side.P2, Phase.ROLL, List.of(), null, List.of(), null);
        return Position.of(state, Side.P2);
    }

    /** The 21 distinct rolls with their weight out of 36. */
    private static int[][] rolls() {
        List<int[]> rolls = new ArrayList<>();
        for (int a = 1; a <= 6; a++) {
            for (int b = a; b <= 6; b++) {
                rolls.add(new int[] {a, b, a == b ? 1 : 2});
            }
        }
        return rolls.toArray(int[][]::new);
    }
}
