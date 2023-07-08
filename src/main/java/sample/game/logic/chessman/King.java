package sample.game.logic.chessman;

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
    public char getChessPieceName() {
        return 'K';
    }


    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        return chess_board[i_dest][j_dest].getColor() != chess_board[i_src][j_src].getColor()
                && Math.abs(i_dest - i_src) <= 1 && Math.abs(j_dest - j_src) <= 1
                && !(i_dest == i_src && j_dest == j_src);
    }
}
