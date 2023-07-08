package sample.game.logic.chessman;

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
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if ((i_dest == i_src ^ j_dest == j_src) && chess_board[i_dest][j_dest].getColor() != color) {
            if (i_dest < i_src) {
                for (int i = i_src - 1; i > i_dest; i--) {
                    if (chess_board[i][j_dest].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
            if (i_dest > i_src) {
                for (int i = i_src + 1; i < i_dest; i++) {
                    if (chess_board[i][j_dest].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
            if (j_dest < j_src) {
                for (int j = j_src - 1; j > j_dest; j--) {
                    if (chess_board[i_dest][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
            if (j_dest > j_src) {
                for (int j = j_src + 1; j < j_dest; j++) {
                    if (chess_board[i_dest][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
