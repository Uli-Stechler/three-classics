package com.threeclassics.backgammon;

import static com.threeclassics.backgammon.BackgammonMove.BAR;
import static com.threeclassics.backgammon.BackgammonMove.OFF;
import static com.threeclassics.backgammon.BackgammonState.CHECKERS;

import com.threeclassics.backgammon.BackgammonMove.Kind;
import com.threeclassics.backgammon.BackgammonMove.Step;
import com.threeclassics.backgammon.BackgammonState.Notice;
import com.threeclassics.backgammon.BackgammonState.NoticeKind;
import com.threeclassics.backgammon.BackgammonState.Phase;
import com.threeclassics.core.GameEngine;
import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * Backgammon for a single game (no doubling cube). Dice are rolled here, from the injected random
 * generator, when a ROLL move is applied; turns that cannot be played pass automatically.
 */
public final class BackgammonEngine implements GameEngine<BackgammonState, BackgammonMove> {

    @Override
    public BackgammonState initialState() {
        Integer[] points = new Integer[24];
        Arrays.fill(points, 0);
        // Each side: 2 on its 24-point, 5 on 13, 3 on 8, 5 on 6. P2's point q is P1's 25 - q.
        points[23] = 2;
        points[12] = 5;
        points[7] = 3;
        points[5] = 5;
        points[0] = -2;
        points[11] = -5;
        points[16] = -3;
        points[18] = -5;
        return new BackgammonState(List.of(points), 0, 0, 0, 0, Side.P1, Phase.OPENING, List.of(), null, List.of(), null);
    }

    @Override
    public Side sideToMove(BackgammonState state) {
        return state.sideToMove();
    }

    @Override
    public List<BackgammonMove> legalMoves(BackgammonState state) {
        if (outcome(state).isPresent()) {
            return List.of();
        }
        if (state.phase() != Phase.MOVE) {
            return List.of(BackgammonMove.ROLL);
        }
        List<Integer> dice = state.dice();
        return Turns.legal(Position.of(state, state.sideToMove()), dice.get(0), dice.get(1)).stream()
                .map(steps -> new BackgammonMove(Kind.TURN, steps, dice))
                .toList();
    }

    /** One turn per distinct resulting position; the order of play is in {@link #moveGraph}. */
    @Override
    public List<BackgammonMove> clientMoves(BackgammonState state) {
        if (state.phase() != Phase.MOVE || outcome(state).isPresent()) {
            return legalMoves(state);
        }
        Position position = Position.of(state, state.sideToMove());
        List<Integer> dice = state.dice();
        return Turns.distinct(position, Turns.legal(position, dice.get(0), dice.get(1))).stream()
                .map(steps -> new BackgammonMove(Kind.TURN, steps, dice))
                .toList();
    }

    @Override
    public TurnGraph moveGraph(BackgammonState state) {
        if (state.phase() != Phase.MOVE || outcome(state).isPresent()) {
            return null;
        }
        Position position = Position.of(state, state.sideToMove());
        return TurnGraph.of(position, Turns.legal(position, state.dice().get(0), state.dice().get(1)));
    }

    @Override
    public BackgammonState apply(BackgammonState state, BackgammonMove move, RandomGenerator rng) {
        if (outcome(state).isPresent()) {
            throw new IllegalMoveException("The game is already over.");
        }
        if (state.phase() == Phase.MOVE) {
            if (move.kind() != Kind.TURN || !legalMoves(state).contains(move)) {
                throw new IllegalMoveException("That isn't a legal way to play " + dice(state.dice()) + ".");
            }
            return playTurn(state, move);
        }
        if (move.kind() != Kind.ROLL) {
            throw new IllegalMoveException("Roll the dice first.");
        }
        return state.phase() == Phase.OPENING ? openingRoll(state, rng) : roll(state, roll(rng), roll(rng));
    }

    /** One die each, re-rolling ties; the higher die starts and plays both dice. */
    private BackgammonState openingRoll(BackgammonState state, RandomGenerator rng) {
        int p1;
        int p2;
        do {
            p1 = roll(rng);
            p2 = roll(rng);
        } while (p1 == p2);
        Side starter = p1 > p2 ? Side.P1 : Side.P2;
        BackgammonState rolled = new BackgammonState(state.points(), state.barP1(), state.barP2(), state.offP1(), state.offP2(),
                starter, Phase.MOVE, List.of(p1, p2), List.of(p1, p2), List.of(), state.lastTurn());
        return passIfStuck(rolled, List.of());
    }

    BackgammonState roll(BackgammonState state, int die1, int die2) {
        BackgammonState rolled = new BackgammonState(state.points(), state.barP1(), state.barP2(), state.offP1(), state.offP2(),
                state.sideToMove(), Phase.MOVE, List.of(die1, die2), null, List.of(), state.lastTurn());
        return passIfStuck(rolled, List.of());
    }

    /** If the roll can't be played at all, the turn passes: "No legal moves". */
    private BackgammonState passIfStuck(BackgammonState rolled, List<Notice> notices) {
        if (!legalMoves(rolled).isEmpty()) {
            return rolled;
        }
        List<Notice> passed = new ArrayList<>(notices);
        passed.add(new Notice(NoticeKind.NO_MOVES, rolled.sideToMove()));
        return handOver(rolled, rolled.sideToMove().opponent(), passed);
    }

    private BackgammonState playTurn(BackgammonState state, BackgammonMove move) {
        Side mover = state.sideToMove();
        Position position = Position.of(state, mover);
        move.steps().forEach(position::apply);
        BackgammonState played = withPosition(state, position, mover, move);
        if (outcome(played).isPresent()) {
            return played;
        }
        return handOver(played, mover.opponent(), List.of());
    }

    /**
     * Gives the turn to {@code next}, who must roll. A player on the bar facing six closed points
     * can't enter at all, so they pass without rolling until a point opens.
     */
    private BackgammonState handOver(BackgammonState state, Side next, List<Notice> notices) {
        List<Notice> passed = new ArrayList<>(notices);
        Side toRoll = next;
        if (isClosedOut(state, next)) {
            passed.add(new Notice(NoticeKind.CLOSED_OUT, next));
            toRoll = next.opponent();
        }
        return new BackgammonState(state.points(), state.barP1(), state.barP2(), state.offP1(), state.offP2(),
                toRoll, Phase.ROLL, state.dice(), state.opening(), passed, state.lastTurn());
    }

    static boolean isClosedOut(BackgammonState state, Side side) {
        Position position = Position.of(state, side);
        if (position.mine[BAR] == 0) return false;
        for (int die = 1; die <= 6; die++) {
            if (position.theirs[BAR - die] < 2) return false;
        }
        return true;
    }

    private static BackgammonState withPosition(BackgammonState state, Position position, Side mover, BackgammonMove move) {
        int barMine = position.mine[BAR];
        int offMine = position.mine[OFF];
        int barTheirs = position.theirs[0];
        boolean p1 = mover == Side.P1;
        return new BackgammonState(
                position.pointsFor(mover),
                p1 ? barMine : barTheirs,
                p1 ? barTheirs : barMine,
                p1 ? offMine : state.offP1(),
                p1 ? state.offP2() : offMine,
                mover, Phase.MOVE, state.dice(), null, List.of(), move);
    }

    @Override
    public Optional<Outcome> outcome(BackgammonState state) {
        Side winner = state.offP1() == CHECKERS ? Side.P1 : state.offP2() == CHECKERS ? Side.P2 : null;
        if (winner == null) {
            return Optional.empty();
        }
        Position loser = Position.of(state, winner.opponent());
        if (loser.mine[OFF] > 0) {
            return Optional.of(Outcome.win(winner, "Single game (1 point)", 1));
        }
        // The winner's home board is the loser's points 19–24.
        boolean trapped = loser.mine[BAR] > 0;
        for (int p = 19; p <= 24; p++) {
            trapped |= loser.mine[p] > 0;
        }
        return trapped
                ? Optional.of(Outcome.win(winner, "Backgammon (3 points)", 3))
                : Optional.of(Outcome.win(winner, "Gammon (2 points)", 2));
    }

    @Override
    public BackgammonMove recorded(BackgammonState before, BackgammonMove move, BackgammonState after) {
        if (move.kind() != Kind.ROLL) {
            return move;
        }
        if (before.phase() == Phase.OPENING) {
            return new BackgammonMove(Kind.OPENING, List.of(), after.opening());
        }
        return new BackgammonMove(Kind.ROLL, List.of(), after.dice());
    }

    @Override
    public String notation(BackgammonMove move) {
        return switch (move.kind()) {
            case ROLL -> move.dice() == null ? "Roll" : "Rolled " + dice(move.dice());
            case OPENING -> "Opening roll " + dice(move.dice());
            case TURN -> dice(move.dice()) + ": " + move.steps().stream().map(BackgammonEngine::step).collect(Collectors.joining(" "));
        };
    }

    private static String step(Step step) {
        String from = step.from() == BAR ? "bar" : String.valueOf(step.from());
        String to = step.to() == OFF ? "off" : String.valueOf(step.to());
        return from + "/" + to + (step.hit() ? "*" : "");
    }

    private static String dice(List<Integer> dice) {
        return dice.get(0) + "–" + dice.get(1);
    }

    /** Pips left for a side: the starting position is 167 each. */
    public static int pipCount(BackgammonState state, Side side) {
        return Position.of(state, side).pips();
    }

    private static int roll(RandomGenerator rng) {
        return 1 + rng.nextInt(6);
    }
}
