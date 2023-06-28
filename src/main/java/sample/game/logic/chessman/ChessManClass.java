package sample.game.logic.chessman;


import sample.Color;
import sample.game.logic.ChessGameLogic;

public abstract class ChessManClass {
    Color color;

    abstract boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game);

    public Color getColor() {
        return color;
    }

    @Override
    public abstract ChessManClass clone();

    public boolean canMove(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        if (IsProtectedByOpponent(game, color)) {
            return ReIsProtectedByOpponent(game, color, i_src, j_src, i_dest, j_dest);
        }
        return canMoveNormal(i_src, j_src, i_dest, j_dest, game);
    }

    private int positionJKing(ChessManClass[][] chess_board, Color color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == chess_board[i][j].getColor())
                    if (chess_board[i][j] instanceof King)
                        return j;
            }
        }
        return 0;
    }

    private int positionIKing(ChessManClass[][] chess_board, Color color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == chess_board[i][j].getColor())
                    if (chess_board[i][j] instanceof King)
                        return i;
            }
        }
        return 0;
    }

    private boolean ReIsProtectedByOpponent(ChessGameLogic game, Color color, int i_src, int j_src, int i_dest, int j_dest) {
        if (!canMoveNormal(i_src, j_src, i_dest, j_dest, game))
            return false;
        ChessGameLogic copy = game.clone();
        copy.move(i_src, j_src, i_dest, j_dest);
        return !IsProtectedByOpponent(copy, color);

    }

    public boolean IsProtectedByOpponent(ChessGameLogic game, Color color) {
        ChessManClass[][] chess_board = game.getChessboard();
        int i_king = positionIKing(chess_board, color);
        int j_king = positionJKing(chess_board, color);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chess_board[i][j].canMoveNormal(i, j, i_king, j_king, game) &&
                        chess_board[i][j].getColor() != color) {
                    return true;
                }
            }
        }
        return false;
    }

    public char getName(int j_dest) {
        return (char) (j_dest + 'a');
    }

    public abstract char getChessPieceName();
}
