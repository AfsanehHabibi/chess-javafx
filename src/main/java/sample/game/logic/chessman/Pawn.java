package sample.game.logic.chessman;

import sample.game.model.Move;
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
    public String getChessPieceName() {
        return "";
    }

    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        ChessManClass[][] chess_board = game.getChessboard();
        if (chess_board[move.getIDes()][move.getJDes()].getColor() != chess_board[move.getISrc()][move.getJSrc()].getColor() &&
                chess_board[move.getIDes()][move.getJDes()] instanceof Pawn && move.getIDes() == move.getISrc() && Math.abs(move.getJDes() - move.getJSrc()) == 1) {
            if (((Pawn) chess_board[move.getIDes()][move.getJDes()]).enPassent)
                return true;
        }
        if (chess_board[move.getIDes()][move.getJDes()].getColor() == Color.BLACK) {
            return chess_board[move.getIDes()][move.getJDes()].getColor() != chess_board[move.getISrc()][move.getJSrc()].getColor()
                    && move.getIDes() == move.getISrc() - 1 && (move.getJDes() + 1 == move.getJSrc() || move.getJDes() - 1 == move.getJSrc());
        } else if (chess_board[move.getIDes()][move.getJDes()].getColor() == Color.WHITE) {
            return chess_board[move.getIDes()][move.getJDes()].getColor() != chess_board[move.getISrc()][move.getJSrc()].getColor()
                    && move.getIDes() - 1 == move.getISrc() && (move.getJDes() + 1 == move.getJSrc() || move.getJDes() - 1 == move.getJSrc());
        } else if (color == Color.WHITE) {
            return move.getISrc() == 6 && move.getIDes() == 4 && move.getJDes() == move.getJSrc() || move.getIDes() + 1 == move.getISrc() && move.getJDes() == move.getJSrc();
        } else if (color == Color.BLACK) {
            return move.getISrc() == 1 && move.getIDes() == 3 && move.getJDes() == move.getJSrc() || move.getIDes() - 1 == move.getISrc() && move.getJDes() == move.getJSrc();
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
