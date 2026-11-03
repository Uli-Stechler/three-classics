package com.threeclassics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeclassics.core.Difficulty;
import com.threeclassics.core.GameEngine;
import com.threeclassics.core.GameMode;
import com.threeclassics.core.GameModule;
import com.threeclassics.core.GameType;
import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import com.threeclassics.persistence.Actor;
import com.threeclassics.persistence.GameEntity;
import com.threeclassics.persistence.GameRepository;
import com.threeclassics.persistence.GameStatus;
import com.threeclassics.persistence.MoveEntity;
import com.threeclassics.persistence.MoveRepository;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.random.RandomGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The referee. Every request runs as one transaction: load the game, check the version, validate
 * and apply the human move, let the computer reply, then persist the new state and the moves.
 */
@Service
public class GameService {

    private final GameRegistry registry;
    private final GameRepository games;
    private final MoveRepository moves;
    private final ObjectMapper json;
    private final RandomGenerator rng;
    private final Duration aiBudget;

    public GameService(
            GameRegistry registry,
            GameRepository games,
            MoveRepository moves,
            ObjectMapper json,
            RandomGenerator rng,
            @Value("${games.ai-budget-ms:1000}") long aiBudgetMs) {
        this.registry = registry;
        this.games = games;
        this.moves = moves;
        this.json = json;
        this.rng = rng;
        this.aiBudget = Duration.ofMillis(aiBudgetMs);
    }

    @Transactional
    public MoveResult create(NewGame request) {
        Side previousHumanSide = request.rematchOf() == null ? null : load(request.rematchOf()).getHumanSide();
        Side humanSide = RematchPolicy.humanSide(request, previousHumanSide);
        return start(registry.get(request.type()), request, humanSide, null);
    }

    /** Starts a game from a custom position. Only reachable through the dev/test endpoint. */
    @Transactional
    public MoveResult createFromState(NewGame request, JsonNode state) {
        Side humanSide = RematchPolicy.humanSide(request, null);
        return start(registry.get(request.type()), request, humanSide, state);
    }

    @Transactional(readOnly = true)
    public GameView get(UUID id) {
        GameEntity game = load(id);
        return viewOf(registry.get(game.getType()), game);
    }

    @Transactional
    public MoveResult play(UUID id, long version, JsonNode move) {
        GameEntity game = load(id);
        if (game.getVersion() != version) {
            throw new StaleVersionException(id, version, game.getVersion());
        }
        if (game.getStatus() == GameStatus.FINISHED) {
            throw new IllegalMoveException("The game is already over.");
        }
        return play(registry.get(game.getType()), game, move);
    }

    @Transactional(readOnly = true)
    public List<StatsEntry> stats() {
        Map<GameType, Map<Difficulty, StatsEntry>> grid = new EnumMap<>(GameType.class);
        for (GameType type : GameType.values()) {
            Map<Difficulty, StatsEntry> row = new EnumMap<>(Difficulty.class);
            for (Difficulty difficulty : Difficulty.values()) {
                row.put(difficulty, new StatsEntry(type, difficulty, 0, 0, 0));
            }
            grid.put(type, row);
        }
        for (GameRepository.StatsRow row : games.stats()) {
            GameType type = GameType.valueOf(row.getType());
            Difficulty difficulty = Difficulty.valueOf(row.getDifficulty());
            grid.get(type).put(difficulty,
                    new StatsEntry(type, difficulty, row.getWins(), row.getLosses(), row.getDraws()));
        }
        return grid.values().stream().flatMap(row -> row.values().stream()).toList();
    }

    private <S, M> MoveResult start(GameModule<S, M> module, NewGame request, Side humanSide, JsonNode customState) {
        S state = customState == null ? module.engine().initialState() : parse(customState, module.stateType());
        GameEntity game = games.save(
                new GameEntity(module.type(), request.mode(), request.difficulty(), humanSide, write(state)));
        S before = state;
        List<GameEvent> events = new ArrayList<>();
        state = letComputerPlay(module, game, state, 0, events);
        return persist(module, game, before, state, events);
    }

    private <S, M> MoveResult play(GameModule<S, M> module, GameEntity game, JsonNode moveJson) {
        GameEngine<S, M> engine = module.engine();
        S state = parse(game.getState(), module.stateType());
        if (game.getMode() == GameMode.VS_AI && engine.sideToMove(state) != game.getHumanSide()) {
            throw new IllegalMoveException("It is not your turn.");
        }
        M move = parseMove(moveJson, module.moveType());
        int ply = (int) moves.countByGameId(game.getId());
        S before = state;
        List<GameEvent> events = new ArrayList<>();
        state = applyAndRecord(module, game, state, move, ply++, events);
        state = letComputerPlay(module, game, state, ply, events);
        return persist(module, game, before, state, events);
    }

    /** In games against the computer, plays computer moves until it is the human's turn again. */
    private <S, M> S letComputerPlay(GameModule<S, M> module, GameEntity game, S state, int ply, List<GameEvent> events) {
        GameEngine<S, M> engine = module.engine();
        while (game.getMode() == GameMode.VS_AI
                && engine.outcome(state).isEmpty()
                && engine.sideToMove(state) != game.getHumanSide()) {
            M move = module.ai().chooseMove(state, game.getDifficulty(), rng, aiBudget);
            state = applyAndRecord(module, game, state, move, ply++, events);
        }
        return state;
    }

    private <S, M> S applyAndRecord(GameModule<S, M> module, GameEntity game, S state, M move, int ply, List<GameEvent> events) {
        GameEngine<S, M> engine = module.engine();
        Side mover = engine.sideToMove(state);
        S next = engine.apply(state, move, rng);
        M recorded = engine.recorded(state, move, next);
        Actor actor = Actor.of(game.getMode(), mover, game.getHumanSide());
        moves.save(new MoveEntity(game.getId(), ply, actor, write(recorded)));
        events.add(new GameEvent(ply, mover, actor, recorded, engine.notation(recorded), next));
        return next;
    }

    private <S, M> MoveResult persist(GameModule<S, M> module, GameEntity game, S before, S state, List<GameEvent> events) {
        game.setState(write(state));
        module.engine().outcome(state).ifPresent(o -> game.finish(o.winner(), o.reason(), o.points()));
        games.saveAndFlush(game);
        return new MoveResult(view(module, game, state), before, events);
    }

    private <S, M> GameView viewOf(GameModule<S, M> module, GameEntity game) {
        return view(module, game, parse(game.getState(), module.stateType()));
    }

    private <S, M> GameView view(GameModule<S, M> module, GameEntity game, S state) {
        GameEngine<S, M> engine = module.engine();
        Optional<Outcome> outcome = engine.outcome(state);
        Side toMove = engine.sideToMove(state);
        boolean humansTurn = outcome.isEmpty()
                && (game.getMode() == GameMode.LOCAL || toMove == game.getHumanSide());

        List<HistoryEntry> history = new ArrayList<>();
        for (MoveEntity row : moves.findByGameIdOrderByPlyAsc(game.getId())) {
            M move = parse(row.getMove(), module.moveType());
            history.add(new HistoryEntry(
                    row.getPly(), sideOf(row.getActor(), game.getHumanSide()), row.getActor(),
                    move, engine.notation(move)));
        }
        return new GameView(
                game.getId(),
                game.getType(),
                game.getMode(),
                game.getDifficulty(),
                game.getHumanSide(),
                game.getVersion(),
                outcome.isPresent() ? GameStatus.FINISHED : GameStatus.IN_PROGRESS,
                toMove,
                state,
                humansTurn ? List.copyOf(engine.clientMoves(state)) : List.of(),
                humansTurn ? engine.moveGraph(state) : null,
                outcome.orElse(null),
                history.isEmpty() ? null : history.getLast(),
                history);
    }

    private static Side sideOf(Actor actor, Side humanSide) {
        return switch (actor) {
            case HUMAN -> humanSide;
            case AI -> humanSide.opponent();
            case P1 -> Side.P1;
            case P2 -> Side.P2;
        };
    }

    private GameEntity load(UUID id) {
        return games.findById(id).orElseThrow(() -> new GameNotFoundException(id));
    }

    private <M> M parseMove(JsonNode node, Class<M> type) {
        try {
            return json.treeToValue(node, type);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new IllegalMoveException("The move could not be read.");
        }
    }

    private <T> T parse(JsonNode node, Class<T> type) {
        try {
            return json.treeToValue(node, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid " + type.getSimpleName() + ": " + e.getOriginalMessage(), e);
        }
    }

    private <T> T parse(String text, Class<T> type) {
        try {
            return json.readValue(text, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored " + type.getSimpleName() + " is unreadable", e);
        }
    }

    private String write(Object value) {
        try {
            return json.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialise " + value.getClass().getSimpleName(), e);
        }
    }
}
