package com.threeclassics.service;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameType;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import com.threeclassics.persistence.GameStatus;
import java.util.List;
import java.util.UUID;

/**
 * Everything the client needs to render a game.
 *
 * @param legalMoves the moves the human may make now; empty while the computer is to move or
 *                   once the game is over
 */
public record GameView(
        UUID id,
        GameType type,
        GameMode mode,
        Difficulty difficulty,
        Side humanSide,
        long version,
        GameStatus status,
        Side sideToMove,
        Object state,
        List<?> legalMoves,
        Outcome outcome,
        HistoryEntry lastMove,
        List<HistoryEntry> history) {
}
