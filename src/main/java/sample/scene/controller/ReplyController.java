package sample.scene.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import sample.game.Chess;

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
    TableView<String> notations;
    @FXML
    Button main_button;
    @FXML
    TableColumn<String, String> moves;
    Chess chess;
    GridPane temp_grid;
    ArrayList<String> information = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("ready to receive notations");
                objectOutputStream.flush();
                String receive = objectInputStream.readUTF();
                while (!receive.equals("over")) {
                    information.add(receive);
                    System.out.println(receive);
                    receive = objectInputStream.readUTF();
                }
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
        moves.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        notations.getItems().addAll(information);
        notations.setOnMouseClicked((E) -> {
            if (notations.getSelectionModel().getSelectedItem() != null) {
                updateChessBoard(notations.getSelectionModel().getSelectedItem());
                System.out.println(notations.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void updateChessBoard(String selectedItem) {
        temp_grid = grid;
        grid.getChildren().clear();
        chess = new Chess(null, grid);
        chess.start();
        try {
            chess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int index = information.indexOf(selectedItem);
        for (int i = 0; i <= index; i++) {
            final String finalLin = information.get(i);
            chess.finalMove(
                    finalLin.charAt(0) - '0', finalLin.charAt(1) - '0',
                    finalLin.charAt(2) - '0', finalLin.charAt(3) - '0', false);
        }
    }
}
