package com.threeclassics.checkers;

import static com.threeclassics.checkers.CheckersState.SIZE;

import com.threeclassics.core.AiPlayer;
import com.threeclassics.core.Difficulty;
import com.threeclassics.core.Side;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Easy plays a random legal move. Medium searches four plies on material and advancement. Hard
 * runs alpha-beta with iterative deepening inside the budget and a richer evaluation.
 */
public final class CheckersAi implements AiPlayer<CheckersState, CheckersMove> {

    private static final int WIN = 100_000;
    private static final int MAN = 100;
    private static final int KING = 160;
    private static final int MEDIUM_DEPTH = 4;
    private static final int MEDIUM_TOLERANCE = 6;
    /** Extra plies searched past the horizon while captures are pending, so exchanges resolve. */
    private static final int CAPTURE_EXTENSION = 6;

    @Override
    public CheckersMove chooseMove(CheckersState state, Difficulty difficulty, RandomGenerator rng, Duration budget) {
        char[] board = state.board().toCharArray();
        Side side = state.sideToMove();
        List<CheckersMove> legal = Rules.moves(board, side);
        if (legal.isEmpty()) {
            throw new IllegalStateException("No legal moves to choose from");
        }
        if (legal.size() == 1 || difficulty == Difficulty.EASY) {
            return legal.get(rng.nextInt(legal.size()));
        }
        return difficulty == Difficulty.MEDIUM ? medium(board, side, legal, rng) : hard(board, side, legal, budget);
    }

    private static CheckersMove medium(char[] board, Side side, List<CheckersMove> legal, RandomGenerator rng) {
        Search search = new Search(Long.MAX_VALUE, false);
        int[] scores = new int[legal.size()];
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < legal.size(); i++) {
            scores[i] = -search.negamax(Rules.play(board, legal.get(i)), side.opponent(), MEDIUM_DEPTH - 1, -WIN * 2, WIN * 2, 1);
            best = Math.max(best, scores[i]);
        }
        List<CheckersMove> nearBest = new ArrayList<>();
        for (int i = 0; i < legal.size(); i++) {
            if (scores[i] >= best - MEDIUM_TOLERANCE) nearBest.add(legal.get(i));
        }
        return nearBest.get(rng.nextInt(nearBest.size()));
    }

    private static CheckersMove hard(char[] board, Side side, List<CheckersMove> legal, Duration budget) {
        long deadline = System.nanoTime() + budget.toNanos() * 85 / 100;
        Search search = new Search(deadline, true);
        List<CheckersMove> ordered = new ArrayList<>(legal);
        CheckersMove best = ordered.getFirst();
        for (int depth = 1; depth <= 30; depth++) {
            try {
                int alpha = -WIN * 2;
                CheckersMove bestThisDepth = null;
                int[] scores = new int[ordered.size()];
                for (int i = 0; i < ordered.size(); i++) {
                    CheckersMove move = ordered.get(i);
                    int score = -search.negamax(Rules.play(board, move), side.opponent(), depth - 1, -WIN * 2, -alpha, 1);
                    scores[i] = score;
                    if (bestThisDepth == null || score > alpha) {
                        alpha = score;
                        bestThisDepth = move;
                    }
                }
                best = bestThisDepth;
                // Search the best moves first next time.
                List<Integer> order = new ArrayList<>();
                for (int i = 0; i < ordered.size(); i++) order.add(i);
                order.sort(Comparator.comparingInt(i -> -scores[i]));
                List<CheckersMove> next = new ArrayList<>();
                for (int i : order) next.add(ordered.get(i));
                ordered = next;
                if (Math.abs(alpha) >= WIN - 100) break; // forced result
            } catch (Search.Timeout timeout) {
                break;
            }
        }
        return best;
    }

    /** Negamax with alpha-beta. Scores are from the point of view of the side to move. */
    private static final class Search {

        static final class Timeout extends RuntimeException {
            Timeout() {
                super(null, null, false, false);
            }
        }

        private final long deadline;
        private final boolean rich;
        private long nodes;

        Search(long deadline, boolean rich) {
            this.deadline = deadline;
            this.rich = rich;
        }

        int negamax(char[] board, Side side, int depth, int alpha, int beta, int ply) {
            if ((++nodes & 1023) == 0 && System.nanoTime() > deadline) {
                throw new Timeout();
            }
            List<CheckersMove> moves = Rules.moves(board, side);
            if (moves.isEmpty()) {
                return -WIN + ply; // no pieces or no moves: a loss, the later the better
            }
            boolean capturing = moves.getFirst().isCapture();
            if (depth <= 0 && !(capturing && depth > -CAPTURE_EXTENSION)) {
                return evaluate(board, side, rich);
            }
            int best = -WIN * 2;
            for (CheckersMove move : moves) {
                int score = -negamax(Rules.play(board, move), side.opponent(), depth - 1, -beta, -alpha, ply + 1);
                if (score > best) best = score;
                if (score > alpha) alpha = score;
                if (alpha >= beta) break;
            }
            return best;
        }
    }

    /** Static evaluation from {@code side}'s point of view. */
    static int evaluate(char[] board, Side side, boolean rich) {
        int score = 0;
        int mine = 0;
        int theirs = 0;
        int myMaterial = 0;
        int theirMaterial = 0;
        for (int square = 0; square < SIZE * SIZE; square++) {
            char piece = board[square];
            Side owner = Rules.owner(piece);
            if (owner == null) continue;
            int row = square / SIZE;
            int column = square % SIZE;
            boolean king = Rules.isKing(piece);
            int value = king ? KING : MAN;
            if (!king) {
                // Rows advanced toward crowning.
                int advanced = owner == Side.P1 ? SIZE - 1 - row : row;
                value += 3 * advanced;
            }
            if (rich) {
                if (!king && row == SIZE - 1 - Rules.crowningRow(owner)) value += 8; // back-row guard
                if (row >= 2 && row <= 5 && column >= 2 && column <= 5) value += 6; // centre control
            }
            if (owner == side) {
                score += value;
                mine++;
                myMaterial += king ? KING : MAN;
            } else {
                score -= value;
                theirs++;
                theirMaterial += king ? KING : MAN;
            }
        }
        if (!rich) {
            return score;
        }
        score += 2 * (Rules.moves(board, side).size() - Rules.moves(board, side.opponent()).size());
        if (mine + theirs <= 8 && myMaterial != theirMaterial) {
            // Endgame: the side ahead brings its kings toward the enemy; the side behind keeps away.
            int sign = myMaterial > theirMaterial ? 1 : -1;
            Side leader = sign > 0 ? side : side.opponent();
            score -= sign * kingDistance(board, leader);
        }
        return score;
    }

    /** Sum over the leader's kings of the distance to the nearest enemy piece. */
    private static int kingDistance(char[] board, Side leader) {
        int total = 0;
        for (int a = 0; a < SIZE * SIZE; a++) {
            if (Rules.owner(board[a]) != leader || !Rules.isKing(board[a])) continue;
            int nearest = SIZE;
            for (int b = 0; b < SIZE * SIZE; b++) {
                if (Rules.owner(board[b]) != leader.opponent()) continue;
                int distance = Math.max(Math.abs(a / SIZE - b / SIZE), Math.abs(a % SIZE - b % SIZE));
                nearest = Math.min(nearest, distance);
            }
            total += nearest;
        }
        return total;
    }
}
