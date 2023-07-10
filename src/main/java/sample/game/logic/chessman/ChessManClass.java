package sample.game.logic.chessman;


import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

import java.io.Serializable;

public abstract class ChessManClass implements Serializable {
    Color color;

    abstract boolean canMoveNormal(Move move, ChessGameLogic game);

    public Color getColor() {
        return color;
    }

    @Override
    public abstract ChessManClass clone();

    public boolean canMove(Move move, ChessGameLogic game) {
        if (IsProtectedByOpponent(game, color)) {
            return ReIsProtectedByOpponent(game, color, move);
        }
        return canMoveNormal(move, game);
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

    private boolean ReIsProtectedByOpponent(ChessGameLogic game, Color color, Move move) {
        if (!canMoveNormal(move, game))
            return false;
        ChessGameLogic copy = game.clone();
        copy.move(move);
        return !IsProtectedByOpponent(copy, color);

    }

    public boolean IsProtectedByOpponent(ChessGameLogic game, Color color) {
        ChessManClass[][] chess_board = game.getChessboard();
        int i_king = positionIKing(chess_board, color);
        int j_king = positionJKing(chess_board, color);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Move move = new Move(i, j, i_king, j_king);
                if (chess_board[i][j].canMoveNormal(move, game) &&
                        chess_board[i][j].getColor() != color) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract String getChessPieceName();

    private char getColorChar() {
        if (color == null)
            return 'N';
        return switch (color) {
            case WHITE -> 'W';
            case BLACK -> 'B';
        };
    }

    @Override
    public String toString() {
        return "" + getCharName() + "" + getColorChar();
    }

    public char getCharName() {
        return getChessPieceName().charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessManClass)) return false;
        ChessManClass that = (ChessManClass) o;
        return color == that.color && getChessPieceName().equals(that.getChessPieceName());
    }

    @Override
    public int hashCode() {
        return color != null ? color.hashCode() : 0;
    }
}
