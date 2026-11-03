package com.threeclassics.service;

import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameType;
import com.threeclassics.core.Side;

/** Decides which seat the human takes in a new game. */
final class RematchPolicy {

    private RematchPolicy() {
    }

    /**
     * An explicit choice always wins. Otherwise a Connect Four rematch swaps who goes first, as the
     * rules suggest for fairness; other games keep the human's previous seat.
     */
    static Side humanSide(NewGame request, Side previousHumanSide) {
        if (request.mode() == GameMode.LOCAL) {
            return Side.P1;
        }
        if (request.humanSide() != null) {
            return request.humanSide();
        }
        if (previousHumanSide != null) {
            return request.type() == GameType.CONNECT_FOUR ? previousHumanSide.opponent() : previousHumanSide;
        }
        return Side.P1;
    }
}
