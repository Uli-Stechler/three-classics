package com.threeclassics.service;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameType;
import com.threeclassics.core.Side;
import java.util.UUID;

/**
 * A request for a new game.
 *
 * @param humanSide null to let the server decide (see {@link RematchPolicy})
 * @param rematchOf the finished game this one replays, used to alternate who starts
 */
public record NewGame(GameType type, GameMode mode, Difficulty difficulty, Side humanSide, UUID rematchOf) {
}
