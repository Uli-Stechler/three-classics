package com.threeclassics.backgammon;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.random.RandomGenerator;

/** A "random" source that rolls the given faces in order. */
final class ScriptedDice implements RandomGenerator {

    private final Deque<Integer> faces = new ArrayDeque<>();

    ScriptedDice(int... faces) {
        for (int face : faces) {
            this.faces.add(face);
        }
    }

    @Override
    public int nextInt(int bound) {
        return faces.removeFirst() - 1;
    }

    @Override
    public long nextLong() {
        throw new UnsupportedOperationException("Only dice rolls are scripted");
    }
}
