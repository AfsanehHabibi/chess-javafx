package sample.game.logic;

import sample.game.logic.chessman.*;
import sample.game.logic.exception.InvalidBoardException;
import sample.model.util.Color;

import java.io.*;
import java.util.Arrays;

public class ChessGameLogic implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ChessManClass[][] chessboard;

    public ChessGameLogic(ChessManClass[][] chessboard) throws InvalidBoardException {
        if (chessboard == null)
            throw new InvalidBoardException();
        if (chessboard.length != 8)
            throw new InvalidBoardException();
        for (int i = 0; i < 8; i++) {
            if (chessboard[i] == null || chessboard[i].length != 8)
                throw new InvalidBoardException();
        }
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
                            if (chessboard[i][j].getColor() == color && chessboard[i][j].canMove(i, j, k, l, this)) {
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

    public void move(int i_src, int j_src, int i_dest, int j_dest) {
        if (chessboard[i_src][j_src] instanceof Pawn) {
            if (i_dest == 0 || i_dest == 7) {
                chessboard[i_dest][j_dest] = new Queen(chessboard[i_src][j_src].getColor());
            } else {
                Pawn pawn = new Pawn(chessboard[i_src][j_src].getColor());
                pawn.setEnPassent((i_src == 7 && i_dest == 5) || (i_src == 1 && i_dest == 3));
                chessboard[i_dest][j_dest] = pawn;
                chessboard[i_src][j_src] = new Empty();
            }
        } else {
            chessboard[i_dest][j_dest] = chessboard[i_src][j_src].clone();
            chessboard[i_src][j_src] = new Empty();
        }
    }

    public boolean canMove(int i_src, int j_src, int i_dest, int j_dest) {
        return chessboard[i_src][j_src].canMove(i_src, j_src, i_dest, j_dest, this);
    }

    public boolean isBoardValid(ChessManClass[][] chessboard) {
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
}
