package sample.model.game;

import sample.game.logic.ChessGameLogic;
import sample.game.model.Move;

import java.io.Serializable;

public class GameMoveRecord implements Serializable {
    String notation;
    ChessGameLogic beforeBoard;
    ChessGameLogic afterBoard;
    Move move;

    public GameMoveRecord(String notation, ChessGameLogic beforeBoard,
                          ChessGameLogic afterBoard, Move move) {
        this.notation = notation;
        this.beforeBoard = beforeBoard;
        this.afterBoard = afterBoard;
        this.move = move;
    }

    public String getNotation() {
        return notation;
    }

    public ChessGameLogic getBeforeBoard() {
        return beforeBoard;
    }

    public ChessGameLogic getAfterBoard() {
        return afterBoard;
    }

    public Move getMove() {
        return move;
    }
}
