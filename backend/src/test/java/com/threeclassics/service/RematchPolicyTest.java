package com.threeclassics.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameType;
import com.threeclassics.core.Side;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RematchPolicyTest {

    private static NewGame request(GameType type, Side humanSide) {
        return new NewGame(type, GameMode.VS_AI, Difficulty.MEDIUM, humanSide, UUID.randomUUID());
    }

    @Test
    void connectFourPlayAgainAlternatesTheStartingPlayer() {
        assertThat(RematchPolicy.humanSide(request(GameType.CONNECT_FOUR, null), Side.P1)).isEqualTo(Side.P2);
        assertThat(RematchPolicy.humanSide(request(GameType.CONNECT_FOUR, null), Side.P2)).isEqualTo(Side.P1);
    }

    @Test
    void anExplicitChoiceOverridesTheAlternation() {
        assertThat(RematchPolicy.humanSide(request(GameType.CONNECT_FOUR, Side.P1), Side.P1)).isEqualTo(Side.P1);
    }

    @Test
    void otherGamesKeepTheHumansSide() {
        assertThat(RematchPolicy.humanSide(request(GameType.CHECKERS, null), Side.P2)).isEqualTo(Side.P2);
    }

    @Test
    void aFreshGameDefaultsToTheHumanMovingFirst() {
        NewGame fresh = new NewGame(GameType.CONNECT_FOUR, GameMode.VS_AI, Difficulty.EASY, null, null);
        assertThat(RematchPolicy.humanSide(fresh, null)).isEqualTo(Side.P1);
    }

    @Test
    void localGamesAlwaysSeatPlayerOneFirst() {
        NewGame local = new NewGame(GameType.CONNECT_FOUR, GameMode.LOCAL, Difficulty.EASY, Side.P2, null);
        assertThat(RematchPolicy.humanSide(local, Side.P2)).isEqualTo(Side.P1);
    }
}
