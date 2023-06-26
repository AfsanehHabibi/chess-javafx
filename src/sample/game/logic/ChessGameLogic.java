package sample.game.logic;

import sample.*;
import sample.game.logic.chessman.*;

public class ChessGameLogic {

    private ChessManClass[][] chessboard;

    public ChessGameLogic(ChessManClass[][] chessboard) {
        this.chessboard = chessboard;
    }

    public void setBoard() {
        for (int i = 0; i < 8; i++) {
            chessboard[6][i] = new Pawn(Color.White);
        }
        chessboard[7][0] = new Rook(Color.White);
        chessboard[7][7] = new Rook(Color.White);
        chessboard[7][1] = new Bishop(Color.White);
        chessboard[7][6] = new Bishop(Color.White);
        chessboard[7][2] = new Knight(Color.White);
        chessboard[7][5] = new Knight(Color.White);
        chessboard[7][3] = new Queen(Color.White);
        chessboard[7][4] = new King(Color.White);
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                chessboard[i][j] = new Empty();
            }
        }
        for (int i = 0; i < 8; i++) {
            chessboard[1][i] = new Pawn(Color.Black);
        }
        chessboard[0][0] = new Rook(Color.Black);
        chessboard[0][7] = new Rook(Color.Black);
        chessboard[0][1] = new Bishop(Color.Black);
        chessboard[0][6] = new Bishop(Color.Black);
        chessboard[0][2] = new Knight(Color.Black);
        chessboard[0][5] = new Knight(Color.Black);
        chessboard[0][3] = new Queen(Color.Black);
        chessboard[0][4] = new King(Color.Black);
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
        return new ChessGameLogic(copy);
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
            }
        } else {
            chessboard[i_dest][j_dest] = chessboard[i_src][j_src].clone();
            chessboard[i_src][j_src] = new Empty();
        }
    }

    public boolean canMove(int i_src, int j_src, int i_dest, int j_dest) {
        return chessboard[i_src][j_src].canMove(i_src, j_src, i_dest, j_dest, this);
    }
}
