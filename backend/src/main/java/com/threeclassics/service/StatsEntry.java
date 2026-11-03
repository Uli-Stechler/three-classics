package com.threeclassics.service;

import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameType;

public record StatsEntry(GameType type, Difficulty difficulty, long wins, long losses, long draws) {
}
