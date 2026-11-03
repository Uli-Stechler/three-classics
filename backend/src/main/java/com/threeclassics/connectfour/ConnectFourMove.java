package com.threeclassics.connectfour;

/** Drop a disc into a column (0 is the leftmost). The player picks a column, never a cell. */
public record ConnectFourMove(int column) {
}
