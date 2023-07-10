package sample.game.logic.chessman;

import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class Knight extends ChessManClass {

    public Knight(Color color) {
        this.color = color;
    }

    @Override
    public ChessManClass clone() {
        return new Knight(color);
    }

    @Override
    public String getChessPieceName() {
        return "N";
    }

    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        return chess_board[move.getIDes()][move.getJDes()].getColor() != color &&
                (Math.abs(move.getIDes() - move.getISrc()) == 1 && Math.abs(move.getJDes() - move.getJSrc()) == 2 ||
                        Math.abs(move.getIDes() - move.getISrc()) == 2 && Math.abs(move.getJDes() - move.getJSrc()) == 1);
    }
}
