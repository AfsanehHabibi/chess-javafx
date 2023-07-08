package sample.scene.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.game.Chess;
import sample.model.game.GameMoveRecord;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.client.Client.objectInputStream;
import static sample.client.Client.objectOutputStream;

public class WatchBoardController extends FatherController implements Initializable {
    @FXML
    GridPane grid;
    @FXML
    TableView<String> notationTable;
    @FXML
    TableColumn<String, String> notationColumn;
    @FXML
    Button mainButton;
    @FXML
    ChoiceBox<String> chatReceiver;
    @FXML
    TextField massage;
    @FXML
    Label chat;
    @FXML
    Button sendButton;

    private Chess chess;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatReceiver.getItems().add("Group");
        chatReceiver.setValue("Group");
        chess = new Chess(null, grid, notationTable, notationColumn);
        chess.start();
        try {
            chess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task t0=new Task() {
            @Override
            protected Object call() throws Exception {
                objectOutputStream.writeUTF("ready to watch");
                objectOutputStream.flush();
                ArrayList<GameMoveRecord> moves = (ArrayList<GameMoveRecord>) objectInputStream.readObject();
                Platform.runLater(() -> chess.updateBoardAndNotation(moves));
                return null;
            }
        };
        Thread first=new Thread(t0);
        first.start();
        try {
            first.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task task=new Task() {
            @Override
            protected Object call() {
                while (true){
                    try {
                        if(objectInputStream.available()!=0) {
                            String line = objectInputStream.readUTF();
                            if(line.equals("over")){
                                mainButton.setVisible(true);
                                break;
                            } else if(line.startsWith("chat ")) {
                                Platform.runLater(()-> chat.setText(chat.getText()+"\n"+line.substring(10)));
                                continue;
                            }
                            GameMoveRecord moveRecord = (GameMoveRecord) objectInputStream.readObject();
                            Platform.runLater(() -> chess.updateBoardAndNotation(moveRecord));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }};
        Thread t=new Thread(task);
        t.start();
    }

    public void sendMassage() {
            Thread send = new Thread(() -> {
                try {
                    objectOutputStream.writeUTF("chat group "+massage.getText());
                    objectOutputStream.flush();
                    //Platform.runLater(()-> notationsHolder.setText(notationsHolder.getText()+" "+massage.getText()));
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
            massage.clear();
    }
}
