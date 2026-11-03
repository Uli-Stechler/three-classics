package com.threeclassics.service;

import com.threeclassics.core.Side;
import com.threeclassics.persistence.Actor;

public record HistoryEntry(int ply, Side side, Actor actor, Object move, String notation) {
}
