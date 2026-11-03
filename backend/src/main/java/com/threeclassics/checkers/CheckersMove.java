package com.threeclassics.checkers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * A complete move: every square the piece visits, in order, and every piece it jumps. A simple
 * move has a two-square path and no captures; a multi-jump lists each landing square.
 */
public record CheckersMove(List<Integer> path, List<Integer> captures) {

    public CheckersMove {
        path = List.copyOf(path);
        captures = captures == null ? List.of() : List.copyOf(captures);
    }

    @JsonIgnore
    public int from() {
        return path.getFirst();
    }

    @JsonIgnore
    public int to() {
        return path.getLast();
    }

    @JsonIgnore
    public boolean isCapture() {
        return !captures.isEmpty();
    }
}
