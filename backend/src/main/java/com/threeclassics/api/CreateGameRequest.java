package com.threeclassics.api;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameType;
import com.threeclassics.core.Side;
import com.threeclassics.service.NewGame;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Mode defaults to VS_AI and difficulty to MEDIUM. */
record CreateGameRequest(
        @NotNull GameType type,
        GameMode mode,
        Difficulty difficulty,
        Side humanSide,
        UUID rematchOf) {

    NewGame toNewGame() {
        return new NewGame(
                type,
                mode == null ? GameMode.VS_AI : mode,
                difficulty == null ? Difficulty.MEDIUM : difficulty,
                humanSide,
                rematchOf);
    }
}
