package com.threeclassics.core;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * The rules of one game. Implementations are pure: states are immutable and {@link #apply}
 * returns a new state. Every game starts with {@link Side#P1} to act.
 *
 * @param <S> immutable game state, serialised to JSON for storage and for the client
 * @param <M> a complete move (a whole multi-jump or a whole backgammon turn), also JSON
 */
public interface GameEngine<S, M> {

    S initialState();

    Side sideToMove(S state);

    /** Every legal move for the side to move. Empty once the game is over. */
    List<M> legalMoves(S state);

    /**
     * Applies a move after checking that it is legal.
     *
     * @param rng source of chance (dice); never use a global random inside an engine
     * @throws IllegalMoveException if the move is not in {@link #legalMoves}
     */
    S apply(S state, M move, RandomGenerator rng);

    /** Present once the game is over. */
    Optional<Outcome> outcome(S state);

    /**
     * The legal moves as offered to the client. Defaults to all of them; a game can collapse moves
     * that lead to the same position (backgammon doubles can be played in thousands of orders).
     */
    default List<M> clientMoves(S state) {
        return legalMoves(state);
    }

    /**
     * An optional step-by-step structure the client walks to build a multi-step move, or null.
     * Submitted moves are still validated against {@link #legalMoves}.
     */
    default Object moveGraph(S state) {
        return null;
    }

    /** Short human-readable notation for the move list, e.g. "c3×e5" or "24/18 13/9". */
    String notation(M move);

    /**
     * The move as it should be stored in history. Lets chance moves record what happened,
     * e.g. a backgammon roll records its dice. Defaults to the move itself.
     */
    default M recorded(S before, M move, S after) {
        return move;
    }
}
