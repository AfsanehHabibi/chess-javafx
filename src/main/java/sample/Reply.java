package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class Reply extends FatherController implements Initializable {
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
    ArrayList<String> informations = new ArrayList<>();
    ObservableList<String> data = FXCollections.observableArrayList(informations);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("ready to receive notations");
                objectOutputStream.flush();
                String receive = objectInputStream.readUTF();
                while (!receive.equals("over")) {
                    informations.add(receive);
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
        //notations.setEditable(true);
        //for (String move:informations) {
        //}
        //moves.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.toString()));
        moves.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        notations.getItems().addAll(informations);
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
        //Platform.runLater(    ()->    chess.start());
        chess.start();
        try {
            chess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int index = informations.indexOf(selectedItem);
        for (int i = 0; i <= index; i++) {
            final String finalLin = informations.get(i);
            chess.finalMove(
                    finalLin.charAt(0) - '0', finalLin.charAt(1) - '0',
                    finalLin.charAt(2) - '0', finalLin.charAt(3) - '0', false);
        }
    }

    public void loadMain() {
        super.loadPage("main_scene");
    }
}
