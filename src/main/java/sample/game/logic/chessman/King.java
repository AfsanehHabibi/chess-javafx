package sample.game.logic.chessman;

import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class King extends ChessManClass {

    public King(Color color) {
        this.color = color;
    }

    @Override
    public ChessManClass clone() {
        return new King(color);
    }

    @Override
    public String getChessPieceName() {
        return "K";
    }


    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        return chess_board[move.getIDes()][move.getJDes()].getColor() != chess_board[move.getISrc()][move.getJSrc()].getColor()
                && Math.abs(move.getIDes() - move.getISrc()) <= 1 && Math.abs(move.getJDes() - move.getJSrc()) <= 1
                && !(move.getIDes() == move.getISrc() && move.getJDes() == move.getJSrc());
    }
}
