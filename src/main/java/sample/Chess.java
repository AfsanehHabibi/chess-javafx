package sample;

import javafx.scene.layout.GridPane;
import sample.game.logic.ChessGameLogic;
import sample.game.logic.chessman.ChessManClass;
import sample.game.logic.chessman.Empty;
import sample.game.view.ChessboardIOController;

import java.io.IOException;

import static sample.Main.objectOutputStream;


public class Chess extends Thread {
    Color color;
    private GridPane gridPane;
    public ChessManClass[][] chessboard;
    private ChessGameLogic gameLogic;
    private ChessboardIOController chessboardIOController;

    Chess(Color color, GridPane gridPane) {
        this.color = color;
        this.gridPane = gridPane;
        this.chessboard = new ChessManClass[8][8];
        this.gameLogic = new ChessGameLogic(chessboard);
        this.chessboardIOController = new ChessboardIOController(gameLogic, gridPane, this);
    }

    @Override
    public void run() {
        //Platform.runLater(this::setBoard);
        gameLogic.setBoard();
        chessboardIOController.drawChessboard(gameLogic.getChessboard());
        try {
            chessboardIOController.setCells();
        } catch (IllegalStateException ignored) {
        }
        if (color != null && color == Color.White)
            chessboardIOController.setClicks(color, checkLose(color));
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
        if (chessboard[i_src][j_src] instanceof Empty)
            return;
        boolean x = !(chessboard[i_dest][j_dest] instanceof Empty);
        gameLogic.move(i_src, j_src, i_dest, j_dest);
        chessboardIOController.updateChessboard(i_src, j_src, i_dest, j_dest, gridPane, gameLogic.getChessboard());
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
        System.out.println(chess_board[i_dest][j_dest].getName(j_dest));
        int temp = 8 - i_dest;
        notation += temp;
        Controller.copy_notations.setText(Controller.copy_notations.getText() + " " + notation);
        try {
            objectOutputStream.writeUTF(i_src + "" + j_src + "" + i_dest + "" + j_dest + "@" + notation);
            objectOutputStream.flush();
            Controller.count = !Controller.count;
            System.out.println("send " + i_src + j_src + i_dest + j_dest + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClicks(Color color) {
        chessboardIOController.setClicks(color, checkLose(color));
    }
}
