package com.threeclassics.persistence;

import com.threeclassics.core.GameMode;
import com.threeclassics.core.Side;

/** Who made a move: the human or the computer in VS_AI games, or a seat in LOCAL games. */
public enum Actor {
    HUMAN,
    AI,
    P1,
    P2;

    public static Actor of(GameMode mode, Side mover, Side humanSide) {
        if (mode == GameMode.LOCAL) {
            return mover == Side.P1 ? P1 : P2;
        }
        return mover == humanSide ? HUMAN : AI;
    }
}
