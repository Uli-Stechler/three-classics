package com.threeclassics.checkers;

import static com.threeclassics.checkers.Position.jump;
import static com.threeclassics.checkers.Position.move;
import static com.threeclassics.checkers.Position.sq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class CheckersEngineTest {

    private final CheckersEngine engine = new CheckersEngine();
    private final Random rng = new Random(1);

    private CheckersState play(CheckersState state, CheckersMove move) {
        return engine.apply(state, move, rng);
    }

    private static List<Integer> destinations(List<CheckersMove> moves) {
        return moves.stream().map(CheckersMove::to).sorted().toList();
    }

    @Test
    void setupHasTwelvePerSideOnDarkSquaresAndDarkMovesFirst() {
        CheckersState start = engine.initialState();
        String board = start.board();
        assertThat(board.chars().filter(c -> c == 'd').count()).isEqualTo(12);
        assertThat(board.chars().filter(c -> c == 'l').count()).isEqualTo(12);
        for (int square = 0; square < 64; square++) {
            if (board.charAt(square) != CheckersState.EMPTY) {
                assertThat((square / 8 + square % 8) % 2).as("square %d", square).isEqualTo(1);
            }
        }
        // Each player's bottom-left corner is a playable square holding their piece.
        assertThat(board.charAt(sq("a1"))).isEqualTo('d');
        assertThat(board.charAt(sq("h8"))).isEqualTo('l');
        assertThat(start.sideToMove()).isEqualTo(Side.P1);
        assertThat(engine.legalMoves(start)).hasSize(7);
    }

    @Test
    void menCannotMoveBackward() {
        CheckersState dark = Position.empty().put('d', "d4").put('l', "a7").toMove(Side.P1);
        assertThat(destinations(engine.legalMoves(dark))).containsExactly(sq("c5"), sq("e5"));

        CheckersState light = Position.empty().put('l', "d4").put('d', "a1").toMove(Side.P2);
        assertThat(destinations(engine.legalMoves(light))).containsExactly(sq("c3"), sq("e3"));
    }

    @Test
    void kingsMoveBothWaysButOnlyOneSquare() {
        CheckersState state = Position.empty().put('D', "d4").put('l', "a7").toMove(Side.P1);
        assertThat(destinations(engine.legalMoves(state)))
                .containsExactlyInAnyOrder(sq("c5"), sq("e5"), sq("c3"), sq("e3"));
    }

    @Test
    void aForcedCaptureRemovesEveryNonCapture() {
        CheckersState state = Position.empty().put('d', "c3", "a3").put('l', "d4").toMove(Side.P1);
        assertThat(engine.legalMoves(state)).containsExactly(jump(List.of("c3", "e5"), List.of("d4")));
        assertThatThrownBy(() -> play(state, move("a3", "b4")))
                .isInstanceOf(IllegalMoveException.class)
                .hasMessageContaining("must capture");
    }

    @Test
    void aCaptureRemovesTheJumpedPiece() {
        CheckersState state = Position.empty().put('d', "c3").put('l', "d4", "h8").toMove(Side.P1);
        CheckersState after = play(state, jump(List.of("c3", "e5"), List.of("d4")));
        assertThat(after.board().charAt(sq("d4"))).isEqualTo(CheckersState.EMPTY);
        assertThat(after.board().charAt(sq("e5"))).isEqualTo('d');
        assertThat(after.sideToMove()).isEqualTo(Side.P2);
    }

    @Test
    void aMultiJumpMustContinue() {
        CheckersState state = Position.empty().put('d', "c3").put('l', "d4", "f6", "a7").toMove(Side.P1);
        List<CheckersMove> legal = engine.legalMoves(state);
        assertThat(legal).containsExactly(jump(List.of("c3", "e5", "g7"), List.of("d4", "f6")));
        assertThatThrownBy(() -> play(state, jump(List.of("c3", "e5"), List.of("d4"))))
                .isInstanceOf(IllegalMoveException.class);
    }

    @Test
    void thePlayerMayChooseAShorterCaptureOverALongerOne() {
        CheckersState state = Position.empty()
                .put('d', "c3", "h2")
                .put('l', "d4", "f6", "g3", "a7")
                .toMove(Side.P1);
        assertThat(engine.legalMoves(state)).containsExactlyInAnyOrder(
                jump(List.of("c3", "e5", "g7"), List.of("d4", "f6")),
                jump(List.of("h2", "f4"), List.of("g3")));
    }

    @Test
    void aManCrownedMidJumpEndsTheMove() {
        // Once crowned on f8 it could jump g7 as a king, but the move must stop.
        CheckersState state = Position.empty().put('d', "d6").put('l', "e7", "g7").toMove(Side.P1);
        List<CheckersMove> legal = engine.legalMoves(state);
        assertThat(legal).containsExactly(jump(List.of("d6", "f8"), List.of("e7")));
        CheckersState after = play(state, legal.getFirst());
        assertThat(after.board().charAt(sq("f8"))).isEqualTo('D');
        assertThat(after.board().charAt(sq("g7"))).isEqualTo('l');
    }

    @Test
    void aKingCanCaptureBackward() {
        CheckersState state = Position.empty().put('D', "d6").put('l', "c5").toMove(Side.P1);
        assertThat(engine.legalMoves(state)).containsExactly(jump(List.of("d6", "b4"), List.of("c5")));
    }

    @Test
    void noLegalMovesLoses() {
        CheckersState state = Position.empty().put('d', "a3").put('l', "b4", "c5").toMove(Side.P1);
        assertThat(engine.outcome(state)).contains(Outcome.win(Side.P2, "No legal moves left"));
        assertThat(engine.legalMoves(state)).isEmpty();
    }

    @Test
    void noPiecesLoses() {
        CheckersState state = Position.empty().put('l', "b4").toMove(Side.P1);
        assertThat(engine.outcome(state)).contains(Outcome.win(Side.P2, "No pieces left"));
    }

    @Test
    void fortyMovesEachWithoutCaptureOrManMoveIsADraw() {
        CheckersState state = Position.empty().put('D', "a1").put('L', "h8").toMove(Side.P1, 79);
        CheckersState after = play(state, move("a1", "b2"));
        assertThat(after.quietPlies()).isEqualTo(80);
        assertThat(engine.outcome(after)).contains(Outcome.draw("Draw by 40-move rule"));
    }

    @Test
    void aManMoveResetsTheFortyMoveCount() {
        CheckersState state = Position.empty().put('d', "c3").put('L', "h8").toMove(Side.P1, 79);
        CheckersState after = play(state, move("c3", "d4"));
        assertThat(after.quietPlies()).isZero();
        assertThat(engine.outcome(after)).isEmpty();
    }

    @Test
    void threefoldRepetitionIsADraw() {
        CheckersState state = Position.empty().put('D', "a1").put('L', "h8").toMove(Side.P1);
        List<CheckersMove> cycle = List.of(move("a1", "b2"), move("h8", "g7"), move("b2", "a1"), move("g7", "h8"));
        for (CheckersMove next : cycle) state = play(state, next);
        assertThat(engine.outcome(state)).as("second occurrence").isEmpty();
        for (CheckersMove next : cycle) state = play(state, next);
        assertThat(engine.outcome(state)).contains(Outcome.draw("Draw by threefold repetition"));
        assertThat(engine.legalMoves(state)).isEmpty();
    }

    @Test
    void notationUsesAlgebraicSquares() {
        assertThat(engine.notation(move("c3", "d4"))).isEqualTo("c3–d4");
        assertThat(engine.notation(jump(List.of("c3", "e5", "g7"), List.of("d4", "f6")))).isEqualTo("c3×e5×g7");
    }
}
