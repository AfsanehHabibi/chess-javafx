package sample.scene.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import sample.game.Chess;
import sample.model.game.GameMoveRecord;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
        serverStreamer.writeString("ready to receive notations");
        information = (ArrayList<GameMoveRecord>) serverStreamer.readObject();
        chess = new Chess(null, grid, notationTable, notationColumn);
        chess.updateBoardAndNotation(information);
        chess.setNotationClicks();
    }
}
