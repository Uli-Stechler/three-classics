package com.threeclassics.core;

/**
 * A seat at the table. P1 always acts first: coral in Connect Four, Dark in Checkers, and ivory
 * (home board bottom-right) in Backgammon, where P1 also makes the opening roll.
 */
public enum Side {
    P1,
    P2;

    public Side opponent() {
        return this == P1 ? P2 : P1;
    }
}
