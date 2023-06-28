package sample.game.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import sample.*;
import sample.game.logic.ChessGameLogic;
import sample.game.logic.chessman.*;
import sample.game.view.chessman.*;

public class ChessboardIOController {
    private ChessGameLogic gameLogic;
    private GridPane gridPane;
    private Chess chess;
    private ChessManView[][] chessManViews;

    public ChessboardIOController(ChessGameLogic gameLogic, GridPane gridPane, Chess chess) {
        this.gameLogic = gameLogic;
        this.gridPane = gridPane;
        this.chess = chess;
        this.chessManViews = new ChessManView[8][8];
    }

    public void drawChessboard(ChessManClass[][] chessboard) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessManViews[i][j] = viewBuilder(chessboard[i][j]);
            }
        }
    }

    public void updateChessboard(int i_src, int j_src, int i_dest, int j_dest, GridPane gridPane, ChessManClass[][] chess_board) {
        gridPane.getChildren().remove(chessManViews[i_dest][j_dest].borderPane);
        gridPane.getChildren().remove(chessManViews[i_src][j_src].borderPane);
        drawChessboard(chess_board);
        gridPane.add(chessManViews[i_src][j_src].borderPane, j_src, i_src);
        gridPane.add(chessManViews[i_dest][j_dest].borderPane, j_dest, i_dest);
    }

    public void setClicks(Color playerTurnColor, boolean lose) {
        if (lose) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isClickable(playerTurnColor, i, j)) {
                    setOnMouseClick(i, j);
                }
            }
        }
    }

    private boolean isClickable(Color playerTurnColor, int i, int j) {
        return gameLogic.getChessboard()[i][j].getColor() == playerTurnColor && !(gameLogic.getChessboard()[i][j] instanceof Empty);
    }

    public void setOnMouseClick(int i, int j) {
        int finalJ = j;
        int finalI = i;
        chessManViews[i][j].borderPane.setOnMouseClicked((event) -> {
            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    turnOffBorder(chessManViews[k][l].borderPane);
                }
            }
            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    if (gameLogic.canMove(finalI, finalJ, k, l)) {
                        chessManViews[k][l].borderPane.setStyle(
                                " -fx-border-color: black ;-fx-border-width: 4;-fx-border-style: solid;");
                        System.out.println(finalI + " " + finalJ + " " + k + " " + l);
                        int finalL = l;
                        int finalK = k;
                        chessManViews[k][l].borderPane.setOnMouseClicked((event1) -> {
                            System.out.println("second");
                            //chessboard[finalI][finalJ].
                            chess.finalMove(finalI, finalJ, finalK, finalL, true);
                        });
                    }
                }
            }
        });
    }

    private void turnOffBorder(BorderPane borderPane) {
        borderPane.setStyle(null);
    }

    public void turnOffClicks() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessManViews[i][j].borderPane.setOnMouseClicked(null);
            }
        }
    }

    public void setCells() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                gridPane.add(chessManViews[i][j].borderPane, j, i);
            }
        }
    }

    public ChessManView viewBuilder(ChessManClass chessMan) {
        if (chessMan instanceof Empty)
            return new EmptyView(chessMan.getColor());
        if (chessMan instanceof Bishop)
            return new BishopView(chessMan.getColor());
        if (chessMan instanceof Queen)
            return new QueenView(chessMan.getColor());
        if (chessMan instanceof King)
            return new KingView(chessMan.getColor());
        if (chessMan instanceof Pawn)
            return new PawnView(chessMan.getColor());
        if (chessMan instanceof Rook)
            return new RookView(chessMan.getColor());
        if (chessMan instanceof Knight)
            return new KnightView(chessMan.getColor());
        return null;
    }
}
