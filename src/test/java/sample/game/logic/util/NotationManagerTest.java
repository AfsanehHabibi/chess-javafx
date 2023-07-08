package sample.game.logic.util;

import org.junit.Test;
import sample.game.logic.ChessGameLogic;
import sample.game.model.Move;

import static org.junit.Assert.*;

public class NotationManagerTest {
    NotationManager notationManager;

    @Test
    public void whenAPawnMoves_TheNotationIsCorrect() {
        notationManager = new NotationManager();
        ChessGameLogic board = new ChessGameLogic();
        assertEquals("d3", notationManager.getNotation(new Move(6, 3, 5, 3), board, null));
    }
}