package sample.game.logic.chessman;

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
    public char getChessPieceName() {
        return 'B';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if (Math.abs(i_dest - i_src) == Math.abs(j_dest - j_src) &&
                chess_board[i_dest][j_dest].getColor() != color) {
            if (i_dest > i_src && j_dest > j_src) {
                for (int i = i_src + 1, j = j_src + 1; i < i_dest && j < j_dest; i++, j++) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            } else if (i_dest > i_src && j_dest < j_src) {
                for (int i = i_src + 1, j = j_src - 1; i < i_dest && j > j_dest; i++, j--) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            } else if (i_dest < i_src && j_dest < j_src) {
                for (int i = i_src - 1, j = j_src - 1; i > i_dest && j > j_dest; i--, j--) {
                    if (chess_board[i][j].getColor() != null) {
                        return false;
                    }
                }
                return true;
            } else if (i_dest < i_src && j_dest > j_src) {
                for (int i = i_src - 1, j = j_src + 1; i > i_dest && j < j_dest; i--, j++) {
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
