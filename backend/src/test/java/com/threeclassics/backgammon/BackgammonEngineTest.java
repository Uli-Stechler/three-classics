package com.threeclassics.backgammon;

import static com.threeclassics.backgammon.BackgammonMove.BAR;
import static com.threeclassics.backgammon.BackgammonMove.OFF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.threeclassics.backgammon.BackgammonMove.Kind;
import com.threeclassics.backgammon.BackgammonMove.Step;
import com.threeclassics.backgammon.BackgammonState.Notice;
import com.threeclassics.backgammon.BackgammonState.NoticeKind;
import com.threeclassics.backgammon.BackgammonState.Phase;
import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BackgammonEngineTest {

    private final BackgammonEngine engine = new BackgammonEngine();

    private List<List<Step>> turns(BackgammonState state) {
        return engine.legalMoves(state).stream().map(BackgammonMove::steps).toList();
    }

    private BackgammonMove turn(BackgammonState state, Step... steps) {
        return new BackgammonMove(Kind.TURN, List.of(steps), state.dice());
    }

    private static Step step(int from, int to, int die) {
        return new Step(from, to, die, false);
    }

    @Test
    void setupHasFifteenEachOn24_13_8_6AndPipCountsOf167() {
        BackgammonState start = engine.initialState();
        BackgammonState expected = Board.empty()
                .ivory(24, 2).ivory(13, 5).ivory(8, 3).ivory(6, 5)
                .obsidian(24, 2).obsidian(13, 5).obsidian(8, 3).obsidian(6, 5)
                .toRoll(Side.P1);
        assertThat(start.points()).isEqualTo(expected.points());
        assertThat(start.offP1() + start.offP2() + start.barP1() + start.barP2()).isZero();
        assertThat(BackgammonEngine.pipCount(start, Side.P1)).isEqualTo(167);
        assertThat(BackgammonEngine.pipCount(start, Side.P2)).isEqualTo(167);
        assertThat(start.phase()).isEqualTo(Phase.OPENING);
        assertThat(engine.legalMoves(start)).containsExactly(BackgammonMove.ROLL);
    }

    @Test
    void theOpeningRollRerollsTiesAndTheHigherDiePlaysBothDice() {
        BackgammonState start = engine.initialState();
        BackgammonState rolled = engine.apply(start, BackgammonMove.ROLL, new ScriptedDice(3, 3, 5, 2));
        assertThat(rolled.sideToMove()).isEqualTo(Side.P1);
        assertThat(rolled.phase()).isEqualTo(Phase.MOVE);
        assertThat(rolled.dice()).containsExactly(5, 2);
        assertThat(rolled.opening()).containsExactly(5, 2);
        assertThat(turns(rolled)).allSatisfy(sequence ->
                assertThat(sequence).extracting(Step::die).containsExactlyInAnyOrder(5, 2));
        assertThat(engine.recorded(start, BackgammonMove.ROLL, rolled))
                .isEqualTo(new BackgammonMove(Kind.OPENING, List.of(), List.of(5, 2)));

        BackgammonState obsidianStarts = engine.apply(start, BackgammonMove.ROLL, new ScriptedDice(1, 6));
        assertThat(obsidianStarts.sideToMove()).isEqualTo(Side.P2);
        assertThat(obsidianStarts.dice()).containsExactly(1, 6);
    }

    @Test
    void barEntryForADieOfNLandsOnTheOpponentsNPoint() {
        BackgammonState state = Board.empty().ivoryBar(1).ivory(6, 14).obsidian(12, 15).toMove(Side.P1, 3, 5);
        assertThat(turns(state)).extracting(List::getFirst)
                .containsOnly(step(BAR, 22, 3), step(BAR, 20, 5));
        // The mover's 22-point is the opponent's 3-point.
        assertThat(25 - 22).isEqualTo(3);
    }

    @Test
    void theBarMustBeClearedFirst() {
        BackgammonState state = Board.empty().ivoryBar(1).ivory(13, 1).ivory(6, 13).obsidian(12, 15).toMove(Side.P1, 3, 5);
        assertThat(turns(state)).isNotEmpty().allSatisfy(sequence -> assertThat(sequence.getFirst().from()).isEqualTo(BAR));

        BackgammonState two = Board.empty().ivoryBar(2).ivory(6, 13).obsidian(12, 15).toMove(Side.P1, 3, 5);
        assertThat(turns(two)).allSatisfy(sequence -> assertThat(sequence).extracting(Step::from).containsOnly(BAR));
    }

    @Test
    void blockedEntryForfeitsTheTurn() {
        // Obsidian holds its 6- and 5-points, Ivory's only entries for a 6–5.
        BackgammonState state = Board.empty().ivoryBar(1).ivory(6, 14)
                .obsidian(6, 2).obsidian(5, 2).obsidian(12, 11)
                .toRoll(Side.P1);
        BackgammonState after = engine.apply(state, BackgammonMove.ROLL, new ScriptedDice(6, 5));
        assertThat(after.sideToMove()).isEqualTo(Side.P2);
        assertThat(after.phase()).isEqualTo(Phase.ROLL);
        assertThat(after.dice()).containsExactly(6, 5);
        assertThat(after.notices()).containsExactly(new Notice(NoticeKind.NO_MOVES, Side.P1));
    }

    @Test
    void aCloseoutPassesTheTurnWithoutRolling() {
        BackgammonState state = Board.empty().ivoryBar(1).ivory(6, 14)
                .obsidian(1, 2).obsidian(2, 2).obsidian(3, 2).obsidian(4, 2).obsidian(5, 2).obsidian(6, 2).obsidian(8, 3)
                .toMove(Side.P2, 1, 2);
        BackgammonMove safe = turn(state, step(8, 7, 1), step(8, 6, 2));
        assertThat(engine.legalMoves(state)).contains(safe);

        BackgammonState after = engine.apply(state, safe, new ScriptedDice());
        assertThat(BackgammonEngine.isClosedOut(after, Side.P1)).isTrue();
        assertThat(after.notices()).containsExactly(new Notice(NoticeKind.CLOSED_OUT, Side.P1));
        assertThat(after.sideToMove()).isEqualTo(Side.P2);
        assertThat(after.phase()).isEqualTo(Phase.ROLL);
    }

    @Test
    void twoOrMoreCheckersBlockAPointButCanBeJumpedOver() {
        // Obsidian's 15-point is Ivory's 10-point.
        BackgammonState state = Board.empty().ivory(13, 1).ivory(1, 14).obsidian(15, 2).obsidian(20, 13).toMove(Side.P1, 3, 1);
        assertThat(turns(state)).containsExactly(List.of(step(13, 12, 1), step(12, 9, 3)));
    }

    @Test
    void hittingSendsABlotToTheBar() {
        BackgammonState state = Board.empty().ivory(13, 1).ivory(1, 14).obsidian(15, 1).obsidian(20, 14).toMove(Side.P1, 3, 6);
        BackgammonMove hit = turn(state, new Step(13, 10, 3, true), step(10, 4, 6));
        assertThat(engine.legalMoves(state)).contains(hit);

        BackgammonState after = engine.apply(state, hit, new ScriptedDice());
        assertThat(after.barP2()).isEqualTo(1);
        assertThat(after.points().get(9)).isZero();
        assertThat(after.points().get(3)).isEqualTo(1);
        assertThat(after.sideToMove()).isEqualTo(Side.P2);
    }

    @Test
    void doublesGiveFourMoves() {
        BackgammonState state = Board.empty()
                .ivory(24, 2).ivory(13, 5).ivory(8, 3).ivory(6, 5)
                .obsidian(24, 2).obsidian(13, 5).obsidian(8, 3).obsidian(6, 5)
                .toMove(Side.P1, 2, 2);
        assertThat(turns(state)).isNotEmpty().allSatisfy(sequence -> assertThat(sequence).hasSize(4));
    }

    @Test
    void thePlayerMustUseAsManyDiceAsPossible() {
        // Playing the 3 with the 7-point checker would leave the 6 unplayable, so that is not allowed.
        BackgammonState state = Board.empty().ivory(20, 1).ivory(7, 1).ivory(1, 13)
                .obsidian(11, 2).obsidian(16, 13)
                .toMove(Side.P1, 6, 3);
        List<List<Step>> turns = turns(state);
        assertThat(turns).isNotEmpty().allSatisfy(sequence -> assertThat(sequence).hasSize(2));
        assertThat(turns).noneMatch(sequence -> sequence.getFirst().equals(step(7, 4, 3)));
        assertThat(turns).contains(List.of(step(7, 1, 6), step(20, 17, 3)));
    }

    @Test
    void whenOnlyOneDieCanBePlayedItMustBeTheLarger() {
        // Either die alone can be played, but never both, so the 6 must be used.
        BackgammonState state = Board.empty().ivory(20, 1).ivory(1, 14).obsidian(14, 2).obsidian(16, 13).toMove(Side.P1, 6, 3);
        assertThat(turns(state)).containsExactly(List.of(step(20, 14, 6)));
        assertThatThrownBy(() -> engine.apply(state, turn(state, step(20, 17, 3)), new ScriptedDice()))
                .isInstanceOf(IllegalMoveException.class);
    }

    @Test
    void noBearingOffUntilAllFifteenAreHome() {
        BackgammonState state = Board.empty().ivory(7, 1).ivory(6, 13).ivory(5, 1).obsidian(1, 15).toMove(Side.P1, 6, 5);
        List<List<Step>> turns = turns(state);
        assertThat(turns).noneMatch(sequence -> sequence.getFirst().to() == OFF);
        // Once the last checker comes home, the other die may bear off.
        assertThat(turns).contains(List.of(step(7, 1, 6), step(5, OFF, 5)));
    }

    @Test
    void aHigherDieBearsOffFromTheHighestPoint() {
        BackgammonState state = Board.empty().ivory(3, 1).ivory(2, 1).obsidian(1, 15).toMove(Side.P1, 6, 5);
        assertThat(turns(state)).containsExactlyInAnyOrder(
                List.of(step(3, OFF, 6), step(2, OFF, 5)),
                List.of(step(3, OFF, 5), step(2, OFF, 6)));
    }

    @Test
    void aCheckerOnAHigherPointMustMoveInsteadOfBearingOffALowerOne() {
        BackgammonState state = Board.empty().ivory(6, 1).ivory(2, 1).obsidian(1, 15).toMove(Side.P1, 4, 1);
        assertThat(turns(state)).isNotEmpty().noneMatch(sequence -> sequence.getFirst().equals(step(2, OFF, 4)));
        assertThat(turns(state)).contains(List.of(step(6, 2, 4), step(2, 1, 1)));
    }

    @Test
    void beingHitDuringBearOffStopsBearingOff() {
        BackgammonState state = Board.empty().ivoryBar(1).ivory(6, 5).ivory(5, 5).ivory(4, 3).obsidian(12, 15).toMove(Side.P1, 6, 5);
        assertThat(turns(state)).isNotEmpty()
                .allSatisfy(sequence -> assertThat(sequence).extracting(Step::to).doesNotContain(OFF));
    }

    @Test
    void scoresSingleGammonAndBackgammon() {
        assertThat(engine.outcome(Board.empty().obsidian(6, 12).toRoll(Side.P2)))
                .contains(Outcome.win(Side.P1, "Single game (1 point)", 1));
        assertThat(engine.outcome(Board.empty().obsidian(6, 15).toRoll(Side.P2)))
                .contains(Outcome.win(Side.P1, "Gammon (2 points)", 2));
        // Obsidian's 20-point is inside Ivory's home board.
        assertThat(engine.outcome(Board.empty().obsidian(6, 14).obsidian(20, 1).toRoll(Side.P2)))
                .contains(Outcome.win(Side.P1, "Backgammon (3 points)", 3));
        assertThat(engine.outcome(Board.empty().obsidian(6, 14).obsidianBar(1).toRoll(Side.P2)))
                .contains(Outcome.win(Side.P1, "Backgammon (3 points)", 3));
        assertThat(engine.outcome(Board.empty().ivory(1, 1).obsidian(6, 3).toRoll(Side.P1))).isEmpty();
    }

    @Test
    void aTurnMustBeOneOfTheLegalSequences() {
        BackgammonState state = Board.empty().ivory(13, 1).ivory(1, 14).obsidian(15, 2).obsidian(20, 13).toMove(Side.P1, 3, 1);
        assertThatThrownBy(() -> engine.apply(state, turn(state, step(13, 10, 3), step(10, 9, 1)), new ScriptedDice()))
                .isInstanceOf(IllegalMoveException.class);
        assertThatThrownBy(() -> engine.apply(state, BackgammonMove.ROLL, new ScriptedDice(1, 1)))
                .isInstanceOf(IllegalMoveException.class);
    }

    @Test
    void theStepGraphHoldsExactlyTheLegalSequences() {
        BackgammonState maxDice = Board.empty().ivory(20, 1).ivory(7, 1).ivory(1, 13)
                .obsidian(11, 2).obsidian(16, 13).toMove(Side.P1, 6, 3);
        for (BackgammonState state : List.of(startingPosition(1, 1), startingPosition(6, 4), maxDice)) {
            TurnGraph graph = engine.moveGraph(state);
            Set<List<Step>> paths = new HashSet<>();
            walk(graph, 0, new ArrayList<>(), paths);
            assertThat(paths).isEqualTo(new HashSet<>(turns(state)));
            // Each distinct result is offered once to the client.
            assertThat(engine.clientMoves(state).size()).isLessThanOrEqualTo(paths.size());
        }
    }

    private static BackgammonState startingPosition(int die1, int die2) {
        return Board.empty()
                .ivory(24, 2).ivory(13, 5).ivory(8, 3).ivory(6, 5)
                .obsidian(24, 2).obsidian(13, 5).obsidian(8, 3).obsidian(6, 5)
                .toMove(Side.P1, die1, die2);
    }

    private static void walk(TurnGraph graph, int node, List<Step> path, Set<List<Step>> out) {
        if (graph.nodes().get(node).complete()) {
            out.add(List.copyOf(path));
        }
        for (int[] edge : graph.nodes().get(node).steps()) {
            path.add(new Step(edge[0], edge[1], edge[2], edge[3] == 1));
            walk(graph, edge[4], path, out);
            path.removeLast();
        }
    }

    @Test
    void notationNamesTheBarHitsAndBearingOff() {
        BackgammonMove move = new BackgammonMove(Kind.TURN,
                List.of(new Step(BAR, 22, 3, true), step(6, OFF, 6)), List.of(3, 6));
        assertThat(engine.notation(move)).isEqualTo("3–6: bar/22* 6/off");
        assertThat(engine.notation(new BackgammonMove(Kind.ROLL, List.of(), List.of(4, 4)))).isEqualTo("Rolled 4–4");
    }
}
