package com.threeclassics.checkers;

import com.threeclassics.core.Side;
import java.util.Arrays;
import java.util.List;

/** Builds checkers positions from algebraic squares as Dark sees the board (a1 bottom-left). */
final class Position {

    private final char[] board = new char[64];

    private Position() {
        Arrays.fill(board, CheckersState.EMPTY);
    }

    static Position empty() {
        return new Position();
    }

    Position put(char piece, String... squares) {
        for (String square : squares) {
            board[sq(square)] = piece;
        }
        return this;
    }

    CheckersState toMove(Side side) {
        return toMove(side, 0);
    }

    CheckersState toMove(Side side, int quietPlies) {
        return new CheckersState(new String(board), side, quietPlies, null, null);
    }

    static int sq(String square) {
        int column = square.charAt(0) - 'a';
        int row = 8 - (square.charAt(1) - '0');
        if ((row + column) % 2 == 0) {
            throw new IllegalArgumentException(square + " is a light square");
        }
        return row * 8 + column;
    }

    static CheckersMove move(String... squares) {
        return new CheckersMove(Arrays.stream(squares).map(Position::sq).toList(), List.of());
    }

    static CheckersMove jump(List<String> path, List<String> captures) {
        return new CheckersMove(path.stream().map(Position::sq).toList(), captures.stream().map(Position::sq).toList());
    }
}
