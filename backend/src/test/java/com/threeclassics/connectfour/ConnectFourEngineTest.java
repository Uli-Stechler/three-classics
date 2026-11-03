package com.threeclassics.connectfour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.threeclassics.connectfour.ConnectFourState.Cell;
import com.threeclassics.connectfour.ConnectFourState.Direction;
import com.threeclassics.connectfour.ConnectFourState.WinLine;
import com.threeclassics.core.IllegalMoveException;
import com.threeclassics.core.Outcome;
import com.threeclassics.core.Side;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class ConnectFourEngineTest {

    private final ConnectFourEngine engine = new ConnectFourEngine();
    private final Random rng = new Random(1);

    private ConnectFourState drop(ConnectFourState state, int column) {
        return engine.apply(state, new ConnectFourMove(column), rng);
    }

    @Test
    void startsEmptyWithCoralToMoveAndEveryColumnOpen() {
        ConnectFourState start = engine.initialState();
        assertThat(start.sideToMove()).isEqualTo(Side.P1);
        assertThat(engine.legalMoves(start)).extracting(ConnectFourMove::column).containsExactly(0, 1, 2, 3, 4, 5, 6);
        assertThat(engine.outcome(start)).isEmpty();
    }

    @Test
    void discsFallToTheLowestEmptySlotAndTurnsAlternate() {
        ConnectFourState state = drop(drop(engine.initialState(), 3), 3);
        assertThat(state.at(3, 0)).isEqualTo(Side.P1);
        assertThat(state.at(3, 1)).isEqualTo(Side.P2);
        assertThat(state.lastDrop()).isEqualTo(new Cell(3, 1));
        assertThat(state.sideToMove()).isEqualTo(Side.P1);
    }

    @Test
    void winsHorizontallyAtTheLeftEdge() {
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                .......
                OOO....
                XXX....
                """, Side.P1);
        ConnectFourState won = drop(state, 3);
        assertWin(won, Side.P1, "Four in a row (horizontal)");
        assertThat(won.winningLines()).containsExactly(line(Direction.HORIZONTAL, 0, 0, 1, 0, 4));
    }

    @Test
    void winsHorizontallyAtTheRightEdge() {
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                .......
                X......
                XXX.OOO
                """, Side.P2);
        ConnectFourState won = drop(state, 3);
        assertWin(won, Side.P2, "Four in a row (horizontal)");
        assertThat(won.winningLines()).containsExactly(line(Direction.HORIZONTAL, 3, 0, 1, 0, 4));
    }

    @Test
    void winsVerticallyAtTheTopOfTheEdgeColumn() {
        ConnectFourState state = Boards.parse("""
                .......
                ......X
                ......X
                ......X
                ......O
                O.....O
                """, Side.P1);
        ConnectFourState won = drop(state, 6);
        assertWin(won, Side.P1, "Four in a row (vertical)");
        assertThat(won.winningLines()).containsExactly(line(Direction.VERTICAL, 6, 2, 0, 1, 4));
    }

    @Test
    void winsOnTheRisingDiagonalFromTheCorner() {
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                ..XX...
                .XOO...
                XOOO..X
                """, Side.P1);
        ConnectFourState won = drop(state, 3);
        assertWin(won, Side.P1, "Four in a row (diagonal)");
        assertThat(won.winningLines()).containsExactly(line(Direction.DIAGONAL_UP, 0, 0, 1, 1, 4));
    }

    @Test
    void winsOnTheFallingDiagonalAtTheRightEdge() {
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                ...XO..
                ...OXO.
                ...XXXO
                """, Side.P2);
        ConnectFourState won = drop(state, 3);
        assertWin(won, Side.P2, "Four in a row (diagonal)");
        assertThat(won.winningLines()).containsExactly(line(Direction.DIAGONAL_DOWN, 3, 3, 1, -1, 4));
    }

    @Test
    void aLineOfFiveCountsAsAWin() {
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                .......
                OO.OO..
                XX.XX..
                """, Side.P1);
        ConnectFourState won = drop(state, 2);
        assertWin(won, Side.P1, "Five in a row (horizontal)");
        assertThat(won.winningLines().getFirst().cells()).hasSize(5);
    }

    @Test
    void aDoubleLineHighlightsEveryWinningDisc() {
        // The drop at column 4, row 4 completes the row and the column at once.
        ConnectFourState state = Boards.parse("""
                .......
                .......
                XXX....
                OOOX...
                OOXX...
                XOOX..O
                """, Side.P1);
        ConnectFourState won = drop(state, 3);
        assertWin(won, Side.P1, "Four in a row (horizontal and vertical)");
        assertThat(won.winningLines()).containsExactlyInAnyOrder(
                line(Direction.HORIZONTAL, 0, 3, 1, 0, 4),
                line(Direction.VERTICAL, 3, 0, 0, 1, 4));
        assertThat(won.winningLines().stream().mapToInt(l -> l.cells().size()).sum()).isEqualTo(8);
    }

    @Test
    void aFullColumnIsRejected() {
        ConnectFourState state = Boards.parse("""
                X......
                O......
                X......
                O......
                X......
                O......
                """, Side.P1);
        assertThat(engine.legalMoves(state)).extracting(ConnectFourMove::column).doesNotContain(0);
        assertThatThrownBy(() -> drop(state, 0)).isInstanceOf(IllegalMoveException.class).hasMessageContaining("full");
    }

    @Test
    void columnsOutsideTheBoardAreRejected() {
        assertThatThrownBy(() -> drop(engine.initialState(), 7)).isInstanceOf(IllegalMoveException.class);
        assertThatThrownBy(() -> drop(engine.initialState(), -1)).isInstanceOf(IllegalMoveException.class);
    }

    @Test
    void aFullBoardWithoutFourInARowIsADraw() {
        // Rows alternate AABBAAB / BBAABBA, so no line ever exceeds two. Column 0's top disc is left
        // for gold to drop as the 42nd move.
        ConnectFourState state = Boards.parse("""
                .OXXOOX
                XXOOXXO
                OOXXOOX
                XXOOXXO
                OOXXOOX
                XXOOXXO
                """, Side.P2);
        ConnectFourState full = drop(state, 0);
        assertThat(engine.outcome(full)).contains(Outcome.draw("The board is full"));
        assertThat(engine.legalMoves(full)).isEmpty();
    }

    @Test
    void theGameStopsAcceptingMovesAfterAWin() {
        ConnectFourState state = Boards.parse("""
                .......
                .......
                .......
                .......
                OOO....
                XXX....
                """, Side.P1);
        ConnectFourState won = drop(state, 3);
        assertThat(engine.legalMoves(won)).isEmpty();
        assertThatThrownBy(() -> drop(won, 6)).isInstanceOf(IllegalMoveException.class).hasMessageContaining("over");
    }

    private void assertWin(ConnectFourState state, Side winner, String reason) {
        assertThat(engine.outcome(state)).contains(Outcome.win(winner, reason));
    }

    private static WinLine line(Direction direction, int column, int row, int dc, int dr, int length) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            cells.add(new Cell(column + i * dc, row + i * dr));
        }
        return new WinLine(direction, cells);
    }
}
