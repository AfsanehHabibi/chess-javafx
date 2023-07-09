package sample.game.logic.util;

import sample.game.logic.ChessGameLogic;
import sample.game.model.Move;

public class NotationManager {
    public String getNotation(Move move, ChessGameLogic beforeBoard,
                              ChessGameLogic afterBoard) {
        String square = (char)('a' + move.getJDes()) + String.valueOf(8 - move.getIDes());
        String isCaptured = beforeBoard.isEmpty(move.getIDes(), move.getJDes()) ? "":"x";
        return beforeBoard.getChessboard()[move.getISrc()][move.getJSrc()].getChessPieceName() + isCaptured + square;
        //TODO(incomplete)
    }
}
