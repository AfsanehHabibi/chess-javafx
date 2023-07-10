package sample.game.logic;

import sample.game.logic.chessman.*;
import sample.game.logic.exception.InvalidBoardException;
import sample.game.model.Move;
import sample.model.util.Color;

import java.io.*;
import java.util.Arrays;

public class ChessGameLogic implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ChessManClass[][] chessboard;

    public ChessGameLogic(ChessManClass[][] chessboard) throws InvalidBoardException {
        if (!isBoardValid(chessboard))
            throw new InvalidBoardException();
        this.chessboard = chessboard;
    }

    public ChessGameLogic() {
        this.chessboard = new ChessManClass[8][8];
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            chessboard[6][i] = new Pawn(Color.WHITE);
        }
        chessboard[7][0] = new Rook(Color.WHITE);
        chessboard[7][7] = new Rook(Color.WHITE);
        chessboard[7][1] = new Bishop(Color.WHITE);
        chessboard[7][6] = new Bishop(Color.WHITE);
        chessboard[7][2] = new Knight(Color.WHITE);
        chessboard[7][5] = new Knight(Color.WHITE);
        chessboard[7][3] = new Queen(Color.WHITE);
        chessboard[7][4] = new King(Color.WHITE);
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard[i][j] = new Empty();
            }
        }
        for (int i = 0; i < 8; i++) {
            chessboard[1][i] = new Pawn(Color.BLACK);
        }
        chessboard[0][0] = new Rook(Color.BLACK);
        chessboard[0][7] = new Rook(Color.BLACK);
        chessboard[0][1] = new Bishop(Color.BLACK);
        chessboard[0][6] = new Bishop(Color.BLACK);
        chessboard[0][2] = new Knight(Color.BLACK);
        chessboard[0][5] = new Knight(Color.BLACK);
        chessboard[0][3] = new Queen(Color.BLACK);
        chessboard[0][4] = new King(Color.BLACK);
    }

    public boolean isCheckmate(Color color) {
        boolean mat = true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (color == chessboard[i][j].getColor())
                    if (chessboard[i][j] instanceof King) {
                        mat = false;
                        break;
                    }
            }
        }
        if (!mat && chessboard[0][0].IsProtectedByOpponent(this, color)) {
            mat = true;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            Move move = new Move(i, j, k, l);
                            if (chessboard[i][j].getColor() == color && chessboard[i][j].canMove(move, this)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return mat;
    }

    @Override
    public ChessGameLogic clone() {
        ChessManClass[][] copy = new ChessManClass[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = chessboard[i][j].clone();
            }
        }
        try {
            return new ChessGameLogic(copy);
        } catch (InvalidBoardException e) {
            throw new RuntimeException();
        }
    }

    public ChessManClass[][] getChessboard() {
        return chessboard;
    }

    public void move(Move move) {
        if (chessboard[move.getISrc()][move.getJSrc()] instanceof Pawn) {
            if (move.getIDes() == 0 || move.getIDes() == 7) {
                chessboard[move.getIDes()][move.getJDes()] = new Queen(chessboard[move.getISrc()][move.getJSrc()].getColor());
            } else {
                Pawn pawn = new Pawn(chessboard[move.getISrc()][move.getJSrc()].getColor());
                pawn.setEnPassent((move.getISrc() == 7 && move.getIDes() == 5) || (move.getISrc() == 1 && move.getIDes() == 3));
                chessboard[move.getIDes()][move.getJDes()] = pawn;
                chessboard[move.getISrc()][move.getJSrc()] = new Empty();
            }
        } else {
            chessboard[move.getIDes()][move.getJDes()] = chessboard[move.getISrc()][move.getJSrc()].clone();
            chessboard[move.getISrc()][move.getJSrc()] = new Empty();
        }
    }

    public boolean canMove(Move move) {
        return chessboard[move.getISrc()][move.getJSrc()].canMove(move, this);
    }

    private boolean isBoardValid(ChessManClass[][] chessboard) {
        if (chessboard == null)
            return false;
        if (chessboard.length != 8)
            return false;
        for (int i = 0; i < 8; i++)
            if (chessboard[i] == null || chessboard[i].length != 8)
                return false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (chessboard[i][j] == null)
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "chessboard:\n" + boardToString();

    }

    private String boardToString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                result.append(chessboard[i][j].toString());
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessGameLogic)) return false;
        ChessGameLogic gameLogic = (ChessGameLogic) o;
        return Arrays.deepEquals(chessboard, gameLogic.chessboard);
    }

    public boolean isEmpty(int i, int j) {
        return chessboard[i][j] instanceof Empty;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessboard);
    }
}
