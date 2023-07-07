package sample.scene.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
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

public class ShowTournamentsController extends FatherController implements Initializable{
    @FXML
    VBox tournament_list;
    ArrayList<String> tournaments_info =new ArrayList<>();
    Thread waitForGame;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            Thread send=new Thread(()->{
                try {
                    //synchronized (objectInputStream) {
                        objectOutputStream.writeUTF("tournaments information");
                        objectOutputStream.flush();
                        String recieve = objectInputStream.readUTF();
                        while (!recieve.startsWith("over")) {
                            tournaments_info.add(recieve);
                            recieve = objectInputStream.readUTF();
                        }
                       // objectInputStream.notifyAll();
                    //}
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
            System.out.println(tournaments_info);
        for (String s : tournaments_info) {
            String[] strings = s.split(" ");
            HBox temp = new HBox();
            Button join_button = new Button(strings[8]);
            temp.setSpacing(10);
            temp.getChildren().addAll(new Label(strings[0]), new Label(strings[1]), new Label(strings[2]),
                    new Label(strings[3] + " " + strings[4]),
                    new Label(strings[6]), new Label(strings[7]), join_button);
            join_button.setOnMouseClicked((event) -> {
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String choose_tour = strings[5];
                        Platform.runLater(() -> {
                            join_button.setText(strings[8]);
                        });
                        synchronized (objectInputStream) {
                            objectOutputStream.writeUTF("game in Tournament " + choose_tour + " " +
                                    join_button.getText());
                            System.out.println(strings[5]);
                            objectOutputStream.flush();
                            String receive = objectInputStream.readUTF();
                            Platform.runLater(() -> {
                                join_button.setText(receive);
                            });
                        }
                        return null;
                    }
                };
                Thread t = new Thread(task);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            tournament_list.getChildren().add(temp);
        }
                if (waitForGame == null || !waitForGame.isAlive()) {
                    waitForGame = new Thread(() -> {
                        while (true) {
                            try {
                                synchronized (objectInputStream) {
                                    if (objectInputStream.available() != 0) {
                                        String re = objectInputStream.readUTF();
                                        if (re.startsWith("tournament game start")) {
                                            Platform.runLater(this::loadGameBoardScene);
                                            break;
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            synchronized (this) {
                                try {
                                    wait(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    );
                waitForGame.start();
            }
    }
}
