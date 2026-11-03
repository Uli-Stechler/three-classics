package com.threeclassics.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.threeclassics.service.GameService;
import com.threeclassics.service.MoveResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Starts a game from any position, so end-to-end tests can begin one move from a win. Only exists
 * in the dev and test profiles; the default profile has no such route.
 */
@RestController
@Profile({"dev", "test"})
@RequestMapping("/api/dev")
class DevGameController {

    private final GameService service;

    DevGameController(GameService service) {
        this.service = service;
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    MoveResult create(@Valid @RequestBody DevGameRequest request) {
        return service.createFromState(request.game().toNewGame(), request.state());
    }

    record DevGameRequest(@NotNull @Valid CreateGameRequest game, @NotNull JsonNode state) {
    }
}
