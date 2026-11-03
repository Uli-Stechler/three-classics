package com.threeclassics.checkers;

import static com.threeclassics.checkers.CheckersState.SIZE;

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

/** American checkers (English draughts) on the 32 dark squares of an 8×8 board. Dark moves first. */
public final class CheckersEngine implements GameEngine<CheckersState, CheckersMove> {

    /** 40 moves by each side with no capture and no man moved. */
    static final int QUIET_PLY_LIMIT = 80;

    @Override
    public CheckersState initialState() {
        StringBuilder board = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                boolean playable = Rules.isPlayable(row, column);
                board.append(!playable ? CheckersState.EMPTY : row < 3 ? 'l' : row > 4 ? 'd' : CheckersState.EMPTY);
            }
        }
        return new CheckersState(board.toString(), Side.P1, 0, null, null);
    }

    @Override
    public Side sideToMove(CheckersState state) {
        return state.sideToMove();
    }

    @Override
    public List<CheckersMove> legalMoves(CheckersState state) {
        return outcome(state).isPresent() ? List.of() : Rules.moves(state.board().toCharArray(), state.sideToMove());
    }

    @Override
    public CheckersState apply(CheckersState state, CheckersMove move, RandomGenerator rng) {
        if (outcome(state).isPresent()) {
            throw new IllegalMoveException("The game is already over.");
        }
        char[] board = state.board().toCharArray();
        List<CheckersMove> legal = Rules.moves(board, state.sideToMove());
        if (!legal.contains(move)) {
            boolean captureRequired = legal.stream().anyMatch(CheckersMove::isCapture);
            throw new IllegalMoveException(captureRequired && !move.isCapture()
                    ? "A capture is available, so you must capture."
                    : "That move isn't legal here.");
        }

        boolean irreversible = Rules.isIrreversible(board, move);
        String next = new String(Rules.play(board, move));
        Side toMove = state.sideToMove().opponent();
        String key = CheckersState.key(next, toMove);
        List<String> positions = new ArrayList<>(irreversible ? List.of() : state.positions());
        positions.add(key);
        int quiet = irreversible ? 0 : state.quietPlies() + 1;
        return new CheckersState(next, toMove, quiet, positions, move);
    }

    @Override
    public Optional<Outcome> outcome(CheckersState state) {
        char[] board = state.board().toCharArray();
        Side toMove = state.sideToMove();
        if (Rules.count(board, toMove) == 0) {
            return Optional.of(Outcome.win(toMove.opponent(), "No pieces left"));
        }
        if (Rules.moves(board, toMove).isEmpty()) {
            return Optional.of(Outcome.win(toMove.opponent(), "No legal moves left"));
        }
        if (state.quietPlies() >= QUIET_PLY_LIMIT) {
            return Optional.of(Outcome.draw("Draw by 40-move rule"));
        }
        String current = state.positions().getLast();
        if (Collections.frequency(state.positions(), current) >= 3) {
            return Optional.of(Outcome.draw("Draw by threefold repetition"));
        }
        return Optional.empty();
    }

    /** Algebraic squares as Dark sees the board, e.g. "c3–d4" or "c3×e5×g7". */
    @Override
    public String notation(CheckersMove move) {
        String separator = move.isCapture() ? "×" : "–";
        return move.path().stream().map(CheckersEngine::squareName).collect(Collectors.joining(separator));
    }

    static String squareName(int square) {
        return "" + (char) ('a' + square % SIZE) + (SIZE - square / SIZE);
    }
}
