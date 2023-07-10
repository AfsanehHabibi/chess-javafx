package sample.game.logic.chessman;

import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class Rook extends ChessManClass {

    public Rook(Color color) {
        this.color = color;
    }

    @Override
    public ChessManClass clone() {
        return new Rook(color);
    }

    @Override
    public String getChessPieceName() {
        return "R";
    }

    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if ((move.getIDes() == move.getISrc() ^ move.getJDes() == move.getJSrc()) && chess_board[move.getIDes()][move.getJDes()].getColor() != color) {
            if (move.getIDes() < move.getISrc()) {
                for (int i = move.getISrc() - 1; i > move.getIDes(); i--) {
                    if (chess_board[i][move.getJDes()].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
            if (move.getIDes() > move.getISrc()) {
                for (int i = move.getISrc() + 1; i < move.getIDes(); i++) {
                    if (chess_board[i][move.getJDes()].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
            if (move.getJDes() < move.getJSrc()) {
                for (int j = move.getJSrc() - 1; j > move.getJDes(); j--) {
                    if (chess_board[move.getIDes()][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
            if (move.getJDes() > move.getJSrc()) {
                for (int j = move.getJSrc() + 1; j < move.getJDes(); j++) {
                    if (chess_board[move.getIDes()][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
