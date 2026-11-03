package com.threeclassics.service;

import com.threeclassics.core.Side;
import com.threeclassics.persistence.Actor;

/**
 * One thing that happened during a request, in order: the human's move, then the computer's
 * roll and moves. The client animates each event and then snaps to {@code state}.
 *
 * @param state the game state right after this event
 */
public record GameEvent(int ply, Side side, Actor actor, Object move, String notation, Object state) {
}
