package sample.scene.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.Chess;
import sample.game.logic.ChessGameLogic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class WatchBoardController extends FatherController implements Initializable {
    @FXML
    GridPane grid;
    @FXML
    Label notationsHolder;
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
        chess = new Chess(null, grid);
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
                String line = objectInputStream.readUTF();
                StringBuilder notations = new StringBuilder();
                while (!line.equals("over")) {
                    notations.append(" ").append(line);
                    line = objectInputStream.readUTF();
                }
                ChessGameLogic gameLogic = (ChessGameLogic) objectInputStream.readObject();
                String finalNotations = notations.toString();
                Platform.runLater(() -> {
                    notationsHolder.setText(finalNotations);
                    chess.update(gameLogic);
                });
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
                            ChessGameLogic gameLogic = (ChessGameLogic) objectInputStream.readObject();
                            String notation = objectInputStream.readUTF();
                            Platform.runLater(() -> {
                                notationsHolder.setText(notationsHolder.getText() + " " + notation);
                                chess.update(gameLogic);
                            });
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
                    Platform.runLater(()-> notationsHolder.setText(notationsHolder.getText()+" "+massage.getText()));
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
