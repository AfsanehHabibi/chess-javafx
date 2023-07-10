package sample.game.logic.chessman;

import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class Queen extends ChessManClass {

    public Queen(Color color) {
        this.color = color;
    }

    @Override
    public ChessManClass clone() {
        return new Queen(color);
    }

    @Override
    public String getChessPieceName() {
        return "Q";
    }

    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if (Math.abs(move.getIDes() - move.getISrc()) == Math.abs(move.getJDes() - move.getJSrc()) &&
                chess_board[move.getIDes()][move.getJDes()].getColor() != color) {
            if ((new Bishop(color)).canMoveNormal(move, game)) {
                return true;
            }
        }
        if ((move.getIDes() == move.getISrc() ^ move.getJDes() == move.getJSrc()) && chess_board[move.getIDes()][move.getJDes()].getColor() != color) {
            return (new Rook(color)).canMoveNormal(move, game);
        }
        return false;
    }
}
