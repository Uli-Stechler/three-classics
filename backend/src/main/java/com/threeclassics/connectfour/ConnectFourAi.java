package com.threeclassics.connectfour;

import static com.threeclassics.connectfour.ConnectFourState.COLUMNS;
import static com.threeclassics.connectfour.ConnectFourState.ROWS;

import com.threeclassics.core.AiPlayer;
import com.threeclassics.core.Difficulty;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Easy is mostly random. Medium searches five plies with a window heuristic. Hard runs negamax
 * with alpha-beta, a transposition table and iterative deepening inside the time budget.
 */
public final class ConnectFourAi implements AiPlayer<ConnectFourState, ConnectFourMove> {

    private static final int CENTER = COLUMNS / 2;
    private static final int[] ORDER = {3, 2, 4, 1, 5, 0, 6};
    private static final int WIN = 1_000_000;
    private static final int MEDIUM_DEPTH = 5;
    private static final int MEDIUM_TOLERANCE = 3;
    private static final long[] WINDOWS = windows();
    /** Value of an open window holding 0..4 of one side's discs (four is scored as a win). */
    private static final int[] WEIGHTS = {0, 1, 4, 20, 0};

    private final ConnectFourEngine engine = new ConnectFourEngine();

    @Override
    public ConnectFourMove chooseMove(ConnectFourState state, Difficulty difficulty, RandomGenerator rng, Duration budget) {
        List<ConnectFourMove> legal = engine.legalMoves(state);
        if (legal.isEmpty()) {
            throw new IllegalStateException("No legal moves to choose from");
        }
        Bitboard board = Bitboard.of(state);
        int column = switch (difficulty) {
            case EASY -> easy(board, legal, rng);
            case MEDIUM -> medium(board, rng);
            case HARD -> hard(board, budget);
        };
        return new ConnectFourMove(column);
    }

    /** Random, except it takes a win or blocks one about half the time each. */
    private static int easy(Bitboard board, List<ConnectFourMove> legal, RandomGenerator rng) {
        if (rng.nextBoolean()) {
            int win = winningColumn(board);
            if (win >= 0) return win;
        }
        if (rng.nextBoolean()) {
            List<Integer> threats = opponentWins(board);
            if (!threats.isEmpty()) return threats.getFirst();
        }
        return legal.get(rng.nextInt(legal.size())).column();
    }

    /** Fixed-depth search, then a random pick among moves that score close to the best. */
    private static int medium(Bitboard board, RandomGenerator rng) {
        if (board.moves == 0) {
            return CENTER - 1 + rng.nextInt(3); // vary the opening
        }
        int win = winningColumn(board);
        if (win >= 0) return win;

        Search search = new Search(Long.MAX_VALUE, false);
        int[] scores = new int[COLUMNS];
        int best = Integer.MIN_VALUE;
        for (int column : ORDER) {
            if (!board.canPlay(column)) continue;
            Bitboard child = board.copy();
            child.play(column);
            scores[column] = -search.negamax(child, MEDIUM_DEPTH - 1, -WIN * 2, WIN * 2, 1);
            best = Math.max(best, scores[column]);
        }
        List<Integer> nearBest = new ArrayList<>();
        for (int column : ORDER) {
            if (board.canPlay(column) && scores[column] >= best - MEDIUM_TOLERANCE) nearBest.add(column);
        }
        return nearBest.get(rng.nextInt(nearBest.size()));
    }

    private static int hard(Bitboard board, Duration budget) {
        if (board.moves == 0) {
            return CENTER; // the first player wins by opening in the centre
        }
        int win = winningColumn(board);
        if (win >= 0) return win;
        List<Integer> threats = opponentWins(board);
        if (threats.size() == 1) return threats.getFirst();

        long deadline = System.nanoTime() + budget.toNanos() * 85 / 100;
        Search search = new Search(deadline, true);
        int bestColumn = firstPlayable(board);
        int remaining = Bitboard.CELLS - board.moves;
        for (int depth = 1; depth <= remaining; depth++) {
            try {
                int[] result = search.root(board, depth);
                bestColumn = result[0];
                if (Math.abs(result[1]) >= WIN - Bitboard.CELLS) break; // forced result found
            } catch (Search.Timeout timeout) {
                break;
            }
        }
        return bestColumn;
    }

    private static int winningColumn(Bitboard board) {
        for (int column : ORDER) {
            if (board.canPlay(column) && board.isWinningMove(column)) return column;
        }
        return -1;
    }

    /** Columns where the opponent would win if it were their turn. */
    private static List<Integer> opponentWins(Bitboard board) {
        Bitboard flipped = board.copy();
        flipped.current = board.opponent();
        List<Integer> columns = new ArrayList<>();
        for (int column : ORDER) {
            if (flipped.canPlay(column) && flipped.isWinningMove(column)) columns.add(column);
        }
        return columns;
    }

    private static int firstPlayable(Bitboard board) {
        for (int column : ORDER) {
            if (board.canPlay(column)) return column;
        }
        throw new IllegalStateException("Board is full");
    }

    /** Scores every window of four: open lines with more of your discs are worth more. */
    static int evaluate(Bitboard board) {
        long mine = board.current;
        long theirs = board.opponent();
        int score = 0;
        for (long window : WINDOWS) {
            int a = Long.bitCount(window & mine);
            int b = Long.bitCount(window & theirs);
            if (b == 0) {
                score += WEIGHTS[a];
            } else if (a == 0) {
                score -= WEIGHTS[b];
            }
        }
        long center = Bitboard.columnMask(CENTER);
        score += 3 * (Long.bitCount(mine & center) - Long.bitCount(theirs & center));
        return score;
    }

    private static long[] windows() {
        List<Long> windows = new ArrayList<>();
        int[][] steps = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        for (int column = 0; column < COLUMNS; column++) {
            for (int row = 0; row < ROWS; row++) {
                for (int[] step : steps) {
                    int endColumn = column + 3 * step[0];
                    int endRow = row + 3 * step[1];
                    if (endColumn < 0 || endColumn >= COLUMNS || endRow < 0 || endRow >= ROWS) continue;
                    long window = 0;
                    for (int i = 0; i < 4; i++) {
                        window |= Bitboard.cell(column + i * step[0], row + i * step[1]);
                    }
                    windows.add(window);
                }
            }
        }
        return windows.stream().mapToLong(Long::longValue).toArray();
    }

    /** Negamax with alpha-beta and an optional transposition table and deadline. */
    private static final class Search {

        static final class Timeout extends RuntimeException {
            Timeout() {
                super(null, null, false, false);
            }
        }

        private static final int TABLE_SIZE = 1 << 20;
        private static final byte EXACT = 0;
        private static final byte LOWER = 1;
        private static final byte UPPER = 2;

        private final long deadline;
        private final long[] keys;
        private final int[] values;
        private final byte[] depths;
        private final byte[] flags;
        private final byte[] bestMoves;
        private long nodes;

        Search(long deadline, boolean useTable) {
            this.deadline = deadline;
            int size = useTable ? TABLE_SIZE : 1;
            keys = new long[size];
            values = new int[size];
            depths = new byte[size];
            flags = new byte[size];
            bestMoves = new byte[size];
        }

        /** Returns {bestColumn, score}. */
        int[] root(Bitboard board, int depth) {
            int alpha = -WIN * 2;
            int bestColumn = -1;
            for (int column : ordered(board, tableMove(board))) {
                Bitboard child = board.copy();
                child.play(column);
                int score = -negamax(child, depth - 1, -WIN * 2, -alpha, 1);
                if (bestColumn < 0 || score > alpha) {
                    alpha = score;
                    bestColumn = column;
                }
            }
            store(board, depth, alpha, EXACT, bestColumn);
            return new int[] {bestColumn, alpha};
        }

        int negamax(Bitboard board, int depth, int alpha, int beta, int ply) {
            if ((++nodes & 4095) == 0 && System.nanoTime() > deadline) {
                throw new Timeout();
            }
            if (board.moves == Bitboard.CELLS) {
                return 0;
            }
            for (int column : ORDER) {
                if (board.canPlay(column) && board.isWinningMove(column)) {
                    return WIN - ply;
                }
            }
            if (depth <= 0) {
                return evaluate(board);
            }

            int originalAlpha = alpha;
            int slot = slot(board);
            if (keys.length > 1 && keys[slot] == board.key() && depths[slot] >= depth) {
                int value = values[slot];
                if (flags[slot] == EXACT) return value;
                if (flags[slot] == LOWER) alpha = Math.max(alpha, value);
                else beta = Math.min(beta, value);
                if (alpha >= beta) return value;
            }

            int best = -WIN * 2;
            int bestColumn = -1;
            for (int column : ordered(board, tableMove(board))) {
                Bitboard child = board.copy();
                child.play(column);
                int score = -negamax(child, depth - 1, -beta, -alpha, ply + 1);
                if (score > best) {
                    best = score;
                    bestColumn = column;
                }
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }

            byte flag = best <= originalAlpha ? UPPER : best >= beta ? LOWER : EXACT;
            store(board, depth, best, flag, bestColumn);
            return best;
        }

        private int tableMove(Bitboard board) {
            int slot = slot(board);
            return keys.length > 1 && keys[slot] == board.key() ? bestMoves[slot] : -1;
        }

        private void store(Bitboard board, int depth, int value, byte flag, int bestColumn) {
            if (keys.length == 1 || bestColumn < 0) return;
            int slot = slot(board);
            keys[slot] = board.key();
            values[slot] = value;
            depths[slot] = (byte) depth;
            flags[slot] = flag;
            bestMoves[slot] = (byte) bestColumn;
        }

        private int slot(Bitboard board) {
            long key = board.key();
            return (int) ((key ^ (key >>> 29)) & (keys.length - 1));
        }

        /** Playable columns, the table's best move first, then centre outwards. */
        private static int[] ordered(Bitboard board, int first) {
            int[] columns = new int[COLUMNS];
            int count = 0;
            if (first >= 0 && board.canPlay(first)) columns[count++] = first;
            for (int column : ORDER) {
                if (column != first && board.canPlay(column)) columns[count++] = column;
            }
            return Arrays.copyOf(columns, count);
        }
    }
}
