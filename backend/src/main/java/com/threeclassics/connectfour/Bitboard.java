package com.threeclassics.connectfour;

import static com.threeclassics.connectfour.ConnectFourState.COLUMNS;
import static com.threeclassics.connectfour.ConnectFourState.ROWS;

import com.threeclassics.core.Side;

/**
 * A mutable bitboard used only inside the AI search. Bit {@code column * 7 + row} is a cell; the
 * spare seventh bit per column keeps lines from wrapping between columns.
 *
 * <p>{@code current} holds the side to move's discs and {@code mask} all discs, so
 * {@code current + mask} identifies a position uniquely and serves as the transposition key.
 */
final class Bitboard {

    static final int STRIDE = ROWS + 1;
    static final int CELLS = COLUMNS * ROWS;

    long current;
    long mask;
    int moves;

    private Bitboard() {
    }

    static Bitboard of(ConnectFourState state) {
        Bitboard board = new Bitboard();
        Side toMove = state.sideToMove();
        for (int column = 0; column < COLUMNS; column++) {
            for (int row = 0; row < state.height(column); row++) {
                long bit = 1L << (column * STRIDE + row);
                board.mask |= bit;
                if (state.at(column, row) == toMove) {
                    board.current |= bit;
                }
                board.moves++;
            }
        }
        return board;
    }

    Bitboard copy() {
        Bitboard copy = new Bitboard();
        copy.current = current;
        copy.mask = mask;
        copy.moves = moves;
        return copy;
    }

    boolean canPlay(int column) {
        return (mask & topCell(column)) == 0;
    }

    void play(int column) {
        current ^= mask;
        mask |= mask + bottomCell(column);
        moves++;
    }

    /** True if the side to move would complete four in a row by playing this column. */
    boolean isWinningMove(int column) {
        long position = current | ((mask + bottomCell(column)) & columnMask(column));
        return hasFour(position);
    }

    long key() {
        return current + mask;
    }

    /** Discs of the side that just moved. */
    long opponent() {
        return current ^ mask;
    }

    static boolean hasFour(long position) {
        long m = position & (position >> STRIDE);
        if ((m & (m >> 2 * STRIDE)) != 0) return true; // horizontal
        m = position & (position >> (STRIDE - 1));
        if ((m & (m >> 2 * (STRIDE - 1))) != 0) return true; // diagonal down
        m = position & (position >> (STRIDE + 1));
        if ((m & (m >> 2 * (STRIDE + 1))) != 0) return true; // diagonal up
        m = position & (position >> 1);
        return (m & (m >> 2)) != 0; // vertical
    }

    static long bottomCell(int column) {
        return 1L << (column * STRIDE);
    }

    static long topCell(int column) {
        return 1L << (column * STRIDE + ROWS - 1);
    }

    static long columnMask(int column) {
        return ((1L << ROWS) - 1) << (column * STRIDE);
    }

    static long cell(int column, int row) {
        return 1L << (column * STRIDE + row);
    }
}
