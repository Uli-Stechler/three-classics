package com.threeclassics.connectfour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.threeclassics.core.Side;
import java.util.List;

/**
 * A Connect Four position.
 *
 * @param columns      seven columns, each listing its discs from the bottom up
 * @param sideToMove   whose turn it is (P1, coral, always starts)
 * @param winningLines every line of four or more completed by the winning drop; empty otherwise
 * @param lastDrop     where the most recent disc landed, or null at the start
 */
public record ConnectFourState(
        List<List<Side>> columns,
        Side sideToMove,
        List<WinLine> winningLines,
        Cell lastDrop) {

    public static final int COLUMNS = 7;
    public static final int ROWS = 6;

    public ConnectFourState {
        if (columns.size() != COLUMNS) {
            throw new IllegalArgumentException("Connect Four has " + COLUMNS + " columns");
        }
        for (List<Side> column : columns) {
            if (column.size() > ROWS) {
                throw new IllegalArgumentException("A column holds at most " + ROWS + " discs");
            }
        }
        columns = columns.stream().map(List::copyOf).toList();
        winningLines = winningLines == null ? List.of() : List.copyOf(winningLines);
    }

    /** The disc at a cell, or null if the cell is empty. Row 0 is the bottom. */
    public Side at(int column, int row) {
        List<Side> discs = columns.get(column);
        return row < discs.size() ? discs.get(row) : null;
    }

    public int height(int column) {
        return columns.get(column).size();
    }

    @JsonIgnore
    public boolean isFull() {
        return columns.stream().allMatch(column -> column.size() == ROWS);
    }

    public record Cell(int column, int row) {
    }

    public enum Direction {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL_UP,
        DIAGONAL_DOWN
    }

    public record WinLine(Direction direction, List<Cell> cells) {

        public WinLine {
            cells = List.copyOf(cells);
        }
    }
}
