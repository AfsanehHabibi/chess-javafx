package sample.game.logic.util;

import sample.game.logic.ChessGameLogic;
import sample.game.model.Move;

public class NotationManager {
    public String getNotation(Move move, ChessGameLogic beforeBoard,
                              ChessGameLogic afterBoard) {
        String square = (char)('a' + move.jDes) + String.valueOf(8 - move.iDes);
        String isCaptured = beforeBoard.isEmpty(move.iDes, move.jDes) ? "":"x";
        return beforeBoard.getChessboard()[move.iSrc][move.jSrc].getChessPieceName() + isCaptured + square;
        //TODO(incomplete)
    }
}
