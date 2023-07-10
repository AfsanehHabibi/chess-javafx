package sample.game.logic.chessman;

import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class Bishop extends ChessManClass {

    public Bishop(Color color) {
        this.color = color;
    }

    @Override
    public ChessManClass clone() {
        return new Bishop(color);
    }

    @Override
    public String getChessPieceName() {
        return "B";
    }

    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if (Math.abs(move.getIDes() - move.getISrc()) == Math.abs(move.getJDes() - move.getJSrc()) &&
                chess_board[move.getIDes()][move.getJDes()].getColor() != color) {
            if (move.getIDes() > move.getISrc() && move.getJDes() > move.getJSrc()) {
                for (int i = move.getISrc() + 1, j = move.getJSrc() + 1; i < move.getIDes() && j < move.getJDes(); i++, j++) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            } else if (move.getIDes() > move.getISrc() && move.getJDes() < move.getJSrc()) {
                for (int i = move.getISrc() + 1, j = move.getJSrc() - 1; i < move.getIDes() && j > move.getJDes(); i++, j--) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            } else if (move.getIDes() < move.getISrc() && move.getJDes() < move.getJSrc()) {
                for (int i = move.getISrc() - 1, j = move.getJSrc() - 1; i > move.getIDes() && j > move.getJDes(); i--, j--) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            } else if (move.getIDes() < move.getISrc() && move.getJDes() > move.getJSrc()) {
                for (int i = move.getISrc() - 1, j = move.getJSrc() + 1; i > move.getIDes() && j < move.getJDes(); i--, j++) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
