package com.threeclassics.checkers;

import static com.threeclassics.checkers.CheckersState.EMPTY;
import static com.threeclassics.checkers.CheckersState.SIZE;

import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.List;

/**
 * Move generation on a raw board, shared by the engine and the AI search. American checkers:
 * men move and capture diagonally forward, kings one square in any diagonal direction (no flying
 * kings), captures are mandatory but any capture may be chosen, and a jump must continue with the
 * same piece unless the piece has just been crowned.
 */
final class Rules {

    private static final int[][] FORWARD_DARK = {{-1, -1}, {-1, 1}};
    private static final int[][] FORWARD_LIGHT = {{1, -1}, {1, 1}};
    private static final int[][] ALL = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

    private Rules() {
    }

    static Side owner(char piece) {
        return switch (piece) {
            case 'd', 'D' -> Side.P1;
            case 'l', 'L' -> Side.P2;
            default -> null;
        };
    }

    static boolean isKing(char piece) {
        return piece == 'D' || piece == 'L';
    }

    static boolean isPlayable(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE && (row + column) % 2 == 1;
    }

    static int crowningRow(Side side) {
        return side == Side.P1 ? 0 : SIZE - 1;
    }

    private static int[][] directions(char piece) {
        if (isKing(piece)) return ALL;
        return owner(piece) == Side.P1 ? FORWARD_DARK : FORWARD_LIGHT;
    }

    /** All legal moves for {@code side}: only captures if any capture exists. */
    static List<CheckersMove> moves(char[] board, Side side) {
        List<CheckersMove> captures = new ArrayList<>();
        for (int square = 0; square < SIZE * SIZE; square++) {
            if (owner(board[square]) == side) {
                collectJumps(board, square, captures);
            }
        }
        if (!captures.isEmpty()) {
            return captures;
        }
        List<CheckersMove> simple = new ArrayList<>();
        for (int square = 0; square < SIZE * SIZE; square++) {
            char piece = board[square];
            if (owner(piece) != side) continue;
            int row = square / SIZE;
            int column = square % SIZE;
            for (int[] d : directions(piece)) {
                int r = row + d[0];
                int c = column + d[1];
                if (isPlayable(r, c) && board[r * SIZE + c] == EMPTY) {
                    simple.add(new CheckersMove(List.of(square, r * SIZE + c), List.of()));
                }
            }
        }
        return simple;
    }

    private static void collectJumps(char[] board, int from, List<CheckersMove> out) {
        char piece = board[from];
        // The jumping piece has left its square, so a king may pass back over it.
        board[from] = EMPTY;
        try {
            List<Integer> path = new ArrayList<>(List.of(from));
            extend(board, piece, path, new ArrayList<>(), out);
        } finally {
            board[from] = piece;
        }
    }

    private static void extend(char[] board, char piece, List<Integer> path, List<Integer> captured, List<CheckersMove> out) {
        int at = path.getLast();
        int row = at / SIZE;
        int column = at % SIZE;
        boolean jumped = false;
        for (int[] d : directions(piece)) {
            int midRow = row + d[0];
            int midColumn = column + d[1];
            int landRow = row + 2 * d[0];
            int landColumn = column + 2 * d[1];
            if (!isPlayable(landRow, landColumn)) continue;
            int mid = midRow * SIZE + midColumn;
            int land = landRow * SIZE + landColumn;
            Side enemy = owner(board[mid]);
            if (enemy == null || enemy == owner(piece) || captured.contains(mid) || board[land] != EMPTY) continue;

            jumped = true;
            path.add(land);
            captured.add(mid);
            if (!isKing(piece) && landRow == crowningRow(owner(piece))) {
                // Crowned mid-jump: the move ends here.
                out.add(new CheckersMove(path, captured));
            } else {
                extend(board, piece, path, captured, out);
            }
            path.removeLast();
            captured.removeLast();
        }
        if (!jumped && path.size() > 1) {
            out.add(new CheckersMove(path, captured));
        }
    }

    /** Plays a move that is known to be legal and returns the new board. */
    static char[] play(char[] board, CheckersMove move) {
        char[] next = board.clone();
        char piece = next[move.from()];
        next[move.from()] = EMPTY;
        for (int captured : move.captures()) {
            next[captured] = EMPTY;
        }
        if (!isKing(piece) && move.to() / SIZE == crowningRow(owner(piece))) {
            piece = Character.toUpperCase(piece);
        }
        next[move.to()] = piece;
        return next;
    }

    /** A capture or a man moving can never be undone, so it resets the draw counters. */
    static boolean isIrreversible(char[] board, CheckersMove move) {
        return move.isCapture() || !isKing(board[move.from()]);
    }

    static int count(char[] board, Side side) {
        int n = 0;
        for (char piece : board) {
            if (owner(piece) == side) n++;
        }
        return n;
    }
}
