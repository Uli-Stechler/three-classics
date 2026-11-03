package com.threeclassics.checkers;

import com.threeclassics.core.Side;
import java.util.List;

/**
 * A checkers position.
 *
 * <p>The board is 64 characters, square {@code row * 8 + column}, row 0 at the top as Dark sees it.
 * Dark starts on rows 5–7 and moves up; Light starts on rows 0–2 and moves down. Playable squares
 * have {@code row + column} odd, so each player's bottom-left corner is playable.
 * Pieces: {@code d} dark man, {@code D} dark king, {@code l} light man, {@code L} light king.
 *
 * @param quietPlies plies since the last capture or man move; 80 (40 each) is a draw
 * @param positions  position keys since the last capture or man move, current one last, for
 *                   threefold repetition
 * @param lastMove   the most recent move, for highlighting; null at the start
 */
public record CheckersState(
        String board,
        Side sideToMove,
        int quietPlies,
        List<String> positions,
        CheckersMove lastMove) {

    public static final int SIZE = 8;
    public static final char EMPTY = '.';

    public CheckersState {
        if (board.length() != SIZE * SIZE) {
            throw new IllegalArgumentException("A checkers board has 64 squares");
        }
        positions = positions == null ? List.of(key(board, sideToMove)) : List.copyOf(positions);
    }

    static String key(String board, Side sideToMove) {
        return board + (sideToMove == Side.P1 ? 'd' : 'l');
    }
}
