package sample.game;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import sample.game.logic.ChessGameLogic;
import sample.game.logic.chessman.ChessManClass;
import sample.game.logic.chessman.Empty;
import sample.game.model.Move;
import sample.game.view.ChessboardIOController;
import sample.game.view.NotationBoardIOController;
import sample.model.game.GameMoveRecord;
import sample.model.util.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sample.client.Client.objectOutputStream;


public class Chess extends Thread {
    Color color;
    private ChessGameLogic gameLogic;
    private ChessboardIOController chessboardIOController;
    private NotationBoardIOController notationBoardIOController;
    private ArrayList<GameMoveRecord> moveRecords;

    public Chess(Color color, GridPane gridPane, TableView<String> tableView,
                 TableColumn<String, String> tableColumn) {
        this.color = color;
        this.gameLogic = new ChessGameLogic();
        this.chessboardIOController = new ChessboardIOController(gameLogic, gridPane, this);
        this.notationBoardIOController = new NotationBoardIOController(tableView, tableColumn);
        moveRecords = new ArrayList<>();
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

    public void updateBoardAndNotation(GameMoveRecord moveRecord) {
        moveRecords.add(moveRecord);
        update(moveRecord.getAfterBoard());
        notationBoardIOController.addToBoard(moveRecord.getNotation());
    }

    public void updateBoardAndNotation(ArrayList<GameMoveRecord> moves) {
        moveRecords.addAll(moves);
        update(moves.get(moves.size()-1).getAfterBoard());
        notationBoardIOController.addAll(getNotations(moves));
    }

    private List<String> getNotations(List<GameMoveRecord> moves) {
        List<String> notations = new ArrayList<>();
        for (GameMoveRecord record: moves)
            notations.add(record.getNotation());
        return notations;
    }

    public void setNotationClicks() {
        notationBoardIOController.setNotationClicks(this);
    }

    public void notifyClickOnNotation(int index) {
        update(moveRecords.get(index).getAfterBoard());
    }
}
