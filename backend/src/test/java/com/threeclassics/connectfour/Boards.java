package com.threeclassics.connectfour;

import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.List;

/** Builds positions from pictures: top row first, X = P1 (coral), O = P2 (gold), . = empty. */
final class Boards {

    private Boards() {
    }

    static ConnectFourState parse(String picture, Side toMove) {
        String[] lines = picture.strip().lines().map(String::strip).toArray(String[]::new);
        if (lines.length != ConnectFourState.ROWS) {
            throw new IllegalArgumentException("Expected 6 rows, got " + lines.length);
        }
        List<List<Side>> columns = new ArrayList<>();
        for (int column = 0; column < ConnectFourState.COLUMNS; column++) {
            List<Side> discs = new ArrayList<>();
            for (int row = 0; row < ConnectFourState.ROWS; row++) {
                char c = lines[ConnectFourState.ROWS - 1 - row].charAt(column);
                if (c == '.') {
                    continue;
                }
                if (discs.size() != row) {
                    throw new IllegalArgumentException("Floating disc in column " + column);
                }
                discs.add(c == 'X' ? Side.P1 : Side.P2);
            }
            columns.add(discs);
        }
        return new ConnectFourState(columns, toMove, List.of(), null);
    }
}
