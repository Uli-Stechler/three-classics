package com.threeclassics.connectfour;

import static com.threeclassics.connectfour.ConnectFourState.COLUMNS;
import static com.threeclassics.connectfour.ConnectFourState.ROWS;

import com.threeclassics.connectfour.ConnectFourState.Cell;
import com.threeclassics.connectfour.ConnectFourState.Direction;
import com.threeclassics.connectfour.ConnectFourState.WinLine;
import com.threeclassics.core.GameEngine;
import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Connect Four on a 7×6 grid. Four or more in a row (any direction) wins immediately; a full board
 * without one is a draw.
 */
public final class ConnectFourEngine implements GameEngine<ConnectFourState, ConnectFourMove> {

    private static final String[] COUNT_WORDS = {"", "", "", "", "Four", "Five", "Six", "Seven"};

    @Override
    public ConnectFourState initialState() {
        List<List<Side>> columns = Collections.nCopies(COLUMNS, List.of());
        return new ConnectFourState(columns, Side.P1, List.of(), null);
    }

    @Override
    public Side sideToMove(ConnectFourState state) {
        return state.sideToMove();
    }

    @Override
    public List<ConnectFourMove> legalMoves(ConnectFourState state) {
        if (isOver(state)) {
            return List.of();
        }
        return IntStream.range(0, COLUMNS)
                .filter(column -> state.height(column) < ROWS)
                .mapToObj(ConnectFourMove::new)
                .toList();
    }

    @Override
    public ConnectFourState apply(ConnectFourState state, ConnectFourMove move, RandomGenerator rng) {
        if (isOver(state)) {
            throw new IllegalMoveException("The game is already over.");
        }
        int column = move.column();
        if (column < 0 || column >= COLUMNS) {
            throw new IllegalMoveException("There is no column " + column + ".");
        }
        if (state.height(column) == ROWS) {
            throw new IllegalMoveException("Column " + (column + 1) + " is full.");
        }

        Side mover = state.sideToMove();
        List<List<Side>> columns = new ArrayList<>(state.columns());
        List<Side> discs = new ArrayList<>(columns.get(column));
        discs.add(mover);
        columns.set(column, discs);

        Cell landed = new Cell(column, discs.size() - 1);
        ConnectFourState placed = new ConnectFourState(columns, mover.opponent(), List.of(), landed);
        return new ConnectFourState(columns, mover.opponent(), linesThrough(placed, landed, mover), landed);
    }

    @Override
    public Optional<Outcome> outcome(ConnectFourState state) {
        if (!state.winningLines().isEmpty()) {
            Cell first = state.winningLines().getFirst().cells().getFirst();
            Side winner = state.at(first.column(), first.row());
            return Optional.of(Outcome.win(winner, describe(state.winningLines())));
        }
        if (state.isFull()) {
            return Optional.of(Outcome.draw("The board is full"));
        }
        return Optional.empty();
    }

    @Override
    public String notation(ConnectFourMove move) {
        return "Column " + (move.column() + 1);
    }

    private static boolean isOver(ConnectFourState state) {
        return !state.winningLines().isEmpty() || state.isFull();
    }

    /** Every line of four or more through the new disc. One drop can complete several lines. */
    private static List<WinLine> linesThrough(ConnectFourState state, Cell from, Side side) {
        List<WinLine> lines = new ArrayList<>();
        int[][] steps = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        Direction[] directions = {Direction.HORIZONTAL, Direction.VERTICAL, Direction.DIAGONAL_UP, Direction.DIAGONAL_DOWN};
        for (int d = 0; d < steps.length; d++) {
            int dc = steps[d][0];
            int dr = steps[d][1];
            List<Cell> cells = new ArrayList<>();
            // Walk back to the start of the run, then forward to its end.
            int c = from.column();
            int r = from.row();
            while (owns(state, c - dc, r - dr, side)) {
                c -= dc;
                r -= dr;
            }
            while (owns(state, c, r, side)) {
                cells.add(new Cell(c, r));
                c += dc;
                r += dr;
            }
            if (cells.size() >= 4) {
                lines.add(new WinLine(directions[d], cells));
            }
        }
        return lines;
    }

    private static boolean owns(ConnectFourState state, int column, int row, Side side) {
        return column >= 0 && column < COLUMNS && row >= 0 && row < ROWS && state.at(column, row) == side;
    }

    /** e.g. "Four in a row (diagonal)" or "Five in a row (horizontal and vertical)". */
    private static String describe(List<WinLine> lines) {
        int longest = lines.stream().mapToInt(line -> line.cells().size()).max().orElse(4);
        String directions = lines.stream()
                .map(line -> switch (line.direction()) {
                    case HORIZONTAL -> "horizontal";
                    case VERTICAL -> "vertical";
                    case DIAGONAL_UP, DIAGONAL_DOWN -> "diagonal";
                })
                .distinct()
                .collect(Collectors.joining(" and "));
        return COUNT_WORDS[Math.min(longest, COUNT_WORDS.length - 1)] + " in a row (" + directions + ")";
    }
}
