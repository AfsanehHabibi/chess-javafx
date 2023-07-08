package sample.scene.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.client.Client.*;

public class SeekGameController extends FatherController implements Initializable{
    @FXML
    RadioButton auto_color;
    @FXML
    RadioButton black_color;
    @FXML
    RadioButton white_color;
    @FXML
    ChoiceBox time;
    @FXML
    CheckBox isRated;
    @FXML
    CheckBox isTimed;

    public void sendRequest(){
        Thread send=new Thread(()->{
            try {
                objectOutputStream.writeUTF("seek "+
                isRated.isSelected()+" "+isTimed.isSelected()+" "+
                        (black_color.isSelected()?"Black ":"")+
                                (white_color.isSelected()?"White ":"")+
                                (auto_color.isSelected()?"Auto ":"")+
                                time.getValue()
                        );
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
        loadRequestGameScene();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        time.getItems().add("3 min");
        time.getItems().add("5 min");
        time.getItems().add("10 min");
        time.getItems().add("15 min");
        time.getItems().add("30 min");
        time.getItems().add("1 hour");
        time.getItems().add("2 hour");
        time.setValue("3 min");
    }
}
