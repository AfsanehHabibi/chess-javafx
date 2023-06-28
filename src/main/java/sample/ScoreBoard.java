package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class ScoreBoard extends FatherController implements Initializable{
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
        for (int i = 0; i < board.size() ; i++) {
            String[] strings = board.get(i).split("@");
            HBox temp = new HBox();
            Button join_button = new Button(strings[7]);
            temp.setSpacing(20);
            temp.getChildren().addAll(new Label(strings[0]), new Label(strings[1]), new Label(strings[2]));
            board1.getChildren().add(temp);
        }
    }
}
