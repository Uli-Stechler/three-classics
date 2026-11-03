package com.threeclassics.api;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

/**
 * @param version the game version the client saw; a mismatch is rejected with 409
 * @param move    one of the {@code legalMoves} from that version, echoed back unchanged
 */
record MoveRequest(@NotNull Long version, @NotNull JsonNode move) {
}
