package sample.game.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import sample.game.Chess;
import sample.game.logic.ChessGameLogic;
import sample.game.logic.chessman.*;
import sample.game.view.chessman.*;
import sample.model.util.Color;

public class ChessboardIOController {
    private ChessGameLogic gameLogic;
    private final GridPane gridPane;
    private final Chess chess;
    private final ChessManView[][] chessManViews;

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
        chessManViews[i][j].borderPane.setOnMouseClicked((event) -> {
            turnBoardBorderOff();
            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    if (gameLogic.canMove(i, j, k, l)) {
                        turnOnBorder(chessManViews[k][l].borderPane);
                        int finalL = l;
                        int finalK = k;
                        chessManViews[k][l].borderPane.setOnMouseClicked((event1) -> chess.finalMove(i, j, finalK, finalL, true));
                    }
                }
            }
        });
    }

    private void turnBoardBorderOff() {
        for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++) {
                turnOffBorder(chessManViews[k][l].borderPane);
            }
        }
    }

    private void turnOnBorder(BorderPane borderPane) {
        borderPane.setStyle(" -fx-border-color: black ;-fx-border-width: 4;-fx-border-style: solid;");
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

    public void setClicks(Color color, boolean checkLose, ChessGameLogic gameLogic) {
        this.gameLogic = gameLogic;
        setClicks(color, checkLose);
    }

    public void clearBoard() {
        gridPane.getChildren().removeAll(gridPane.getChildren());
    }
}
