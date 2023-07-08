package sample.scene.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import sample.game.Chess;
import sample.model.game.GameMoveRecord;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.client.Client.objectInputStream;
import static sample.client.Client.objectOutputStream;

public class ReplyController extends FatherController implements Initializable {
    @FXML
    GridPane grid;
    @FXML
    TableView<String> notationTable;
    @FXML
    Button main_button;
    @FXML
    TableColumn<String, String> notationColumn;
    Chess chess;
    ArrayList<GameMoveRecord>  information;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("ready to receive notations");
                objectOutputStream.flush();
                information = (ArrayList<GameMoveRecord>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        chess = new Chess(null, grid, notationTable, notationColumn);
        chess.updateBoardAndNotation(information);
        chess.setNotationClicks();
    }
}
