package sample.game;

import javafx.scene.layout.GridPane;
import sample.game.logic.ChessGameLogic;
import sample.game.logic.chessman.ChessManClass;
import sample.game.logic.chessman.Empty;
import sample.game.model.Move;
import sample.game.view.ChessboardIOController;
import sample.model.util.Color;

import java.io.IOException;

import static sample.client.Client.objectOutputStream;


public class Chess extends Thread {
    Color color;
    private ChessGameLogic gameLogic;
    private ChessboardIOController chessboardIOController;

    public Chess(Color color, GridPane gridPane) {
        this.color = color;
        this.gameLogic = new ChessGameLogic();
        this.chessboardIOController = new ChessboardIOController(gameLogic, gridPane, this);
    }

    @Override
    public void run() {
        chessboardIOController.drawChessboard(gameLogic.getChessboard());
        try {
            chessboardIOController.setCells();
        } catch (IllegalStateException ignored) {}
        super.run();
    }

    private boolean checkLose(Color color) {
        boolean mat = gameLogic.isCheckmate(color);
        if (mat) {
            Thread send = new Thread(() -> {
                try {
                    objectOutputStream.writeUTF("player checkmate");
                    objectOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            send.start();
            try {
                send.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mat;
    }

    public void finalMove(int i_src, int j_src, int i_dest, int j_dest, boolean send_data) {
        if (gameLogic.getChessboard()[i_src][j_src] instanceof Empty)
            return;
        boolean x = !(gameLogic.getChessboard()[i_dest][j_dest] instanceof Empty);
        if (send_data) {
            sendData(i_src, j_src, i_dest, j_dest, gameLogic.getChessboard(), x);
            chessboardIOController.turnOffClicks();
        }
    }

    void sendData(int i_src, int j_src, int i_dest, int j_dest, ChessManClass[][] chess_board, boolean isCaptured) {
        String notation = "";
        notation += chess_board[i_dest][j_dest].getChessPieceName();
        if (isCaptured)
            notation += 'x';
        notation += chess_board[i_dest][j_dest].getName(j_dest);
        int temp = 8 - i_dest;
        notation += temp;
        try {
            objectOutputStream.writeUTF(notation);
            objectOutputStream.flush();
            objectOutputStream.writeObject(new Move(i_src, j_src, i_dest, j_dest));
            objectOutputStream.flush();
            //Controller.count = !Controller.count;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(ChessGameLogic gameLogic) {
        chessboardIOController.clearBoard();
        chessboardIOController.drawChessboard(gameLogic.getChessboard());
        try {
            chessboardIOController.setCells();
        } catch (IllegalStateException ignored) {}
    }

    public void setClicks(ChessGameLogic gameLogic) {
        chessboardIOController.setClicks(color, checkLose(color), gameLogic);
    }

    public Color getColor() {
        return color;
    }
}
