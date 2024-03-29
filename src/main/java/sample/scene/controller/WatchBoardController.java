package sample.scene.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.game.Chess;
import sample.game.view.ChatIOController;
import sample.model.chat.Message;
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
    TextField messageHolder;
    @FXML
    Label chatContent;
    @FXML
    Button sendButton;

    private Chess chess;
    ChatIOController chatIOController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatIOController = new ChatIOController(chatReceiver, messageHolder, chatContent,
                sendButton, null, true);
        chess = new Chess(null, grid, notationTable, notationColumn);
        chess.start();
        try {
            chess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        serverStreamer.writeString("ready to watch");
        ArrayList<GameMoveRecord> moves = (ArrayList<GameMoveRecord>) serverStreamer.readObject();
        Platform.runLater(() -> chess.updateBoardAndNotation(moves));
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
                            } else if(line.startsWith("chat")) {
                                Message message = (Message) objectInputStream.readObject();
                                Platform.runLater(() -> chatIOController.addNewMessage(message));
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
        Message message = chatIOController.getMessageToBeSendAndClear();
        serverStreamer.writeString("chat");
        serverStreamer.writeObject(message);
    }
}
