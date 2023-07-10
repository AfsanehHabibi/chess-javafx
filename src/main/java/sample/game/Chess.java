package sample.game;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import sample.game.logic.ChessGameLogic;
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

    public void finalMove(Move move, boolean send_data) {
        if (gameLogic.getChessboard()[move.getISrc()][move.getJSrc()] instanceof Empty)
            return;
        if (send_data) {
            sendData(move);
            chessboardIOController.turnOffClicks();
        }
    }

    void sendData(Move move) {
        try {
            objectOutputStream.writeUTF("sending move");
            objectOutputStream.flush();
            objectOutputStream.writeObject(move);
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
        if (moves.size() != 0) {
            moveRecords.addAll(moves);
            update(moves.get(moves.size() - 1).getAfterBoard());
            notationBoardIOController.addAll(getNotations(moves));
        } else {
            // a newly created board
            update(new ChessGameLogic());
        }
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
