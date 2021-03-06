package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class ShowGameResult extends FatherController implements Initializable {
    ArrayList<String> games_info = new ArrayList<>();
    @FXML
    VBox game_list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread send = new Thread(() -> {
            try {
                synchronized (objectInputStream) {
                    objectOutputStream.writeUTF("games history");
                    System.out.println("gggggg");
                    objectOutputStream.flush();
                    String receive = objectInputStream.readUTF();
                    while (!receive.startsWith("over")) {
                        games_info.add(receive);
                        receive = objectInputStream.readUTF();
                    }
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
        System.out.println(games_info);
        for (String aGames_info : games_info) {
            String[] strings = aGames_info.split(" ");
            HBox temp = new HBox();
            temp.getChildren().addAll(new Label(strings[0]),
                    new Label(strings[1]), new Label(strings[2]), new Label(
                            strings[3]
                    ));
            temp.setOnMouseClicked((E)->{
                Thread data_send=new Thread(()->{
                   try{
                       objectOutputStream.writeUTF("reply of game "+'0');
                       objectOutputStream.flush();
                   }catch (IOException e){
                       e.printStackTrace();
                   }
                });
                data_send.start();
                try {
                    data_send.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(this::loadReplyScene);
            });
            game_list.getChildren().add(temp);
        }
    }

    public void loadMain() {
        super.loadPage("main_scene");
    }
    private void loadReplyScene() {
        super.loadPage("reply");
    }
}
