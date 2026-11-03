package com.threeclassics.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnabledIf("com.threeclassics.api.DatabaseTest#databaseAvailable")
class GameApiTest extends DatabaseTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    private JsonNode body(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsString());
    }

    private ResultActions postJson(String path, String content) throws Exception {
        return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(content));
    }

    private JsonNode newConnectFour(String extra) throws Exception {
        return body(postJson("/api/games", "{\"type\":\"CONNECT_FOUR\",\"difficulty\":\"EASY\"" + extra + "}")
                .andExpect(status().isCreated()));
    }

    @Test
    void createsAGameAndTheComputerReplies() throws Exception {
        JsonNode game = newConnectFour("").get("game");
        assertThat(game.get("status").asText()).isEqualTo("IN_PROGRESS");
        assertThat(game.get("humanSide").asText()).isEqualTo("P1");
        assertThat(game.get("legalMoves")).hasSize(7);

        JsonNode result = body(postJson("/api/games/" + game.get("id").asText() + "/moves",
                "{\"version\":" + game.get("version") + ",\"move\":{\"column\":3}}")
                .andExpect(status().isOk()));

        JsonNode events = result.get("events");
        assertThat(events).hasSize(2);
        assertThat(events.get(0).get("actor").asText()).isEqualTo("HUMAN");
        assertThat(events.get(1).get("actor").asText()).isEqualTo("AI");
        JsonNode after = result.get("game");
        assertThat(after.get("version").asLong()).isGreaterThan(game.get("version").asLong());
        assertThat(after.get("history")).hasSize(2);
        assertThat(after.get("lastMove").get("actor").asText()).isEqualTo("AI");

        mvc.perform(get("/api/games/" + game.get("id").asText()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.history.length()").value(2));
    }

    @Test
    void anIllegalMoveIsA400ProblemDetail() throws Exception {
        JsonNode game = newConnectFour("").get("game");
        postJson("/api/games/" + game.get("id").asText() + "/moves",
                "{\"version\":" + game.get("version") + ",\"move\":{\"column\":9}}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Illegal move"))
                .andExpect(jsonPath("$.detail").value("There is no column 9."));
    }

    @Test
    void anUnreadableMoveIsA400() throws Exception {
        JsonNode game = newConnectFour("").get("game");
        postJson("/api/games/" + game.get("id").asText() + "/moves",
                "{\"version\":" + game.get("version") + ",\"move\":{\"column\":\"middle\"}}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Illegal move"));
    }

    @Test
    void aStaleVersionIsA409() throws Exception {
        JsonNode game = newConnectFour("").get("game");
        String moves = "/api/games/" + game.get("id").asText() + "/moves";
        String move = "{\"version\":" + game.get("version") + ",\"move\":{\"column\":0}}";
        postJson(moves, move).andExpect(status().isOk());
        // The same request again, e.g. a double click, is based on an outdated version.
        postJson(moves, move)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Stale game version"));
    }

    @Test
    void anUnknownGameIsA404() throws Exception {
        mvc.perform(get("/api/games/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Game not found"));
    }

    @Test
    void connectFourPlayAgainSwapsWhoStarts() throws Exception {
        JsonNode first = newConnectFour("").get("game");
        JsonNode rematch = newConnectFour(",\"rematchOf\":\"" + first.get("id").asText() + "\"");
        assertThat(rematch.get("game").get("humanSide").asText()).isEqualTo("P2");
        // The computer now moves first, and its opening arrives as an event.
        assertThat(rematch.get("events")).hasSize(1);
        assertThat(rematch.get("game").get("history")).hasSize(1);
    }

    @Test
    void aWinFromADevPositionFinishesTheGameAndCountsInStats() throws Exception {
        String state = """
                {"columns":[["P1","P2"],["P1","P2"],["P1","P2"],[],[],[],[]],
                 "sideToMove":"P1","winningLines":[],"lastDrop":null}
                """;
        JsonNode created = body(postJson("/api/dev/games",
                "{\"game\":{\"type\":\"CONNECT_FOUR\",\"difficulty\":\"HARD\"},\"state\":" + state + "}")
                .andExpect(status().isCreated()));
        JsonNode game = created.get("game");
        long winsBefore = winsAgainstHard();

        JsonNode result = body(postJson("/api/games/" + game.get("id").asText() + "/moves",
                "{\"version\":" + game.get("version") + ",\"move\":{\"column\":3}}")
                .andExpect(status().isOk()));
        JsonNode finished = result.get("game");
        assertThat(finished.get("status").asText()).isEqualTo("FINISHED");
        assertThat(finished.get("outcome").get("winner").asText()).isEqualTo("P1");
        assertThat(finished.get("outcome").get("reason").asText()).isEqualTo("Four in a row (horizontal)");
        assertThat(finished.get("legalMoves")).isEmpty();
        assertThat(finished.get("state").get("winningLines")).hasSize(1);
        assertThat(winsAgainstHard()).isEqualTo(winsBefore + 1);

        postJson("/api/games/" + game.get("id").asText() + "/moves",
                "{\"version\":" + finished.get("version") + ",\"move\":{\"column\":4}}")
                .andExpect(status().isBadRequest());
    }

    private long winsAgainstHard() throws Exception {
        for (JsonNode row : body(mvc.perform(get("/api/stats")).andExpect(status().isOk()))) {
            if (row.get("type").asText().equals("CONNECT_FOUR") && row.get("difficulty").asText().equals("HARD")) {
                return row.get("wins").asLong();
            }
        }
        throw new AssertionError("No stats row for Connect Four on Hard");
    }
}
