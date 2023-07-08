package sample.scene.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.client.Client.objectInputStream;
import static sample.client.Client.objectOutputStream;

public class ScoreBoardController extends FatherController implements Initializable{
    ArrayList<String> board=new ArrayList<>();
    @FXML
    VBox board1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread send=new Thread(()->{
            try {
                synchronized (objectInputStream) {
                    objectOutputStream.writeUTF("score boars");
                    objectOutputStream.flush();
                    String receive = objectInputStream.readUTF();
                    while (!receive.startsWith("over")) {
                        board.add(receive);
                        receive = objectInputStream.readUTF();
                    }
                    objectInputStream.notifyAll();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String s : board) {
            String[] strings = s.split("@");
            HBox temp = new HBox();
            temp.setSpacing(20);
            temp.getChildren().addAll(new Label(strings[0]), new Label(strings[1]), new Label(strings[2]));
            board1.getChildren().add(temp);
        }
    }
}
