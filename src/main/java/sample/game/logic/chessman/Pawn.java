package sample.game.logic.chessman;

import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class Pawn extends ChessManClass {

    private boolean enPassent = false;

    public Pawn(Color color) {
        this.color = color;
    }

    @Override
    public ChessManClass clone() {
        return new Pawn(color);
    }

    @Override
    public char getChessPieceName() {
        return 0;
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if (chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor() &&
                chess_board[i_dest][j_dest] instanceof Pawn && i_dest == i_src && Math.abs(j_dest - j_src) == 1) {
            if (((Pawn) chess_board[i_dest][j_dest]).enPassent)
                return true;
        }
        if (chess_board[i_dest][j_dest].getColor() == Color.BLACK) {
            return chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor()
                    && i_dest == i_src - 1 && (j_dest + 1 == j_src || j_dest - 1 == j_src);
        } else if (chess_board[i_dest][j_dest].getColor() == Color.WHITE) {
            return chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor()
                    && i_dest - 1 == i_src && (j_dest + 1 == j_src || j_dest - 1 == j_src);
        } else if (color == Color.WHITE) {
            return i_src == 6 && i_dest == 4 && j_dest == j_src || i_dest + 1 == i_src && j_dest == j_src;
        } else if (color == Color.BLACK) {
            return i_src == 1 && i_dest == 3 && j_dest == j_src || i_dest - 1 == i_src && j_dest == j_src;
        }
        return false;
    }

    public void setEnPassent(boolean enPassent) {
        this.enPassent = enPassent;
    }

    @Override
    public char getCharName() {
        return 'P';
    }
}
