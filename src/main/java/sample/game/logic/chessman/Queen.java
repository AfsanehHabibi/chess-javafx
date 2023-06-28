package sample.game.logic.chessman;

import sample.Color;
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
    public char getChessPieceName() {
        return 'Q';
    }

    @Override
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if (Math.abs(i_dest - i_src) == Math.abs(j_dest - j_src) &&
                chess_board[i_dest][j_dest].getColor() != color) {
            if ((new Bishop(color)).canMoveNormal(i_src, j_src, i_dest, j_dest, game)) {
                return true;
            }
        }
        if ((i_dest == i_src ^ j_dest == j_src) && chess_board[i_dest][j_dest].getColor() != color) {
            return (new Rook(color)).canMoveNormal(i_src, j_src, i_dest, j_dest, game);
        }
        return false;
    }
}
