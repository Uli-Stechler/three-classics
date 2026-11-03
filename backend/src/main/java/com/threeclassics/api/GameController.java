package com.threeclassics.api;

import com.threeclassics.service.GameService;
import com.threeclassics.service.GameView;
import com.threeclassics.service.MoveResult;
import com.threeclassics.service.StatsEntry;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class GameController {

    private final GameService service;

    GameController(GameService service) {
        this.service = service;
    }

    /** Creates a game. If the computer moves first, its move is already in {@code events}. */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    MoveResult create(@Valid @RequestBody CreateGameRequest request) {
        return service.create(request.toNewGame());
    }

    @GetMapping("/games/{id}")
    GameView get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PostMapping("/games/{id}/moves")
    MoveResult move(@PathVariable UUID id, @Valid @RequestBody MoveRequest request) {
        return service.play(id, request.version(), request.move());
    }

    @GetMapping("/stats")
    List<StatsEntry> stats() {
        return service.stats();
    }
}
