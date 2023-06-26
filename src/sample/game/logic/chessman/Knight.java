package sample.game.logic.chessman;

import sample.Color;
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
    public char getChessPieceName() {
        return 'N';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        return chess_board[i_dest][j_dest].getColor() != color &&
                (Math.abs(i_dest - i_src) == 1 && Math.abs(j_dest - j_src) == 2 ||
                        Math.abs(i_dest - i_src) == 2 && Math.abs(j_dest - j_src) == 1);
    }
}
