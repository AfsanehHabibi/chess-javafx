package sample.scene.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sample.model.game.Game;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;

import static sample.client.Client.objectInputStream;
import static sample.client.Client.objectOutputStream;

public class RequestGameController extends FatherController implements Initializable {
    @FXML
    VBox game_list;
    ArrayList<String> gameRequests =new ArrayList<>();
    Vector<Game> onGoingGames;
    static Thread waitForGame;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       refresh();
        if (waitForGame == null || !waitForGame.isAlive()) {
            waitForGame = new Thread(() -> {
                while (true) {
                    try {
                        synchronized (objectInputStream) {
                            if (objectInputStream.available() != 0) {
                                String re = objectInputStream.readUTF();
                                if (re.startsWith("game start") ) {
                                    Platform.runLater(this::loadGameBoardScene);
                                    break;
                                } else if (re.startsWith("watch")){
                                    Platform.runLater(this::loadWatchBoardScene);
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

    public void refresh(){
        game_list.getChildren().clear();
        Thread send=new Thread(()->{
            try {
                synchronized (objectInputStream) {
                    objectOutputStream.writeUTF("games information");
                    objectOutputStream.flush();
                    String receive = objectInputStream.readUTF();
                    while (!receive.startsWith("over")) {
                        gameRequests.add(receive);
                        receive = objectInputStream.readUTF();
                    }
                    onGoingGames = (Vector<Game>) objectInputStream.readObject();
                    objectInputStream.notifyAll();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String gameRequest : gameRequests) {
            String[] strings = gameRequest.split(" ");
            HBox temp = new HBox();
            temp.setSpacing(10);
            temp.getChildren().addAll(new Label(strings[2]),
                    new Label(Boolean.getBoolean(strings[8]) ? "Rated" : "UnRated"), new Label(strings[10]),
                    new Label(
                            strings[13] + " " + strings[14]
                    ));
            System.out.println(strings[11]);
            temp.setOnMouseClicked((event) -> {
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String chose_op = strings[6];
                        objectOutputStream.writeUTF("game with " + chose_op + " " + strings[12]);
                        System.out.println(strings[12]);
                        objectOutputStream.flush();
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
            game_list.getChildren().add(temp);
        }
        game_list.getChildren().add(new Label("watch"));
        for (Game onGoingGame : onGoingGames) {
            HBox temp = new HBox();
            temp.setSpacing(10);
            temp.getChildren().addAll(new Label(onGoingGame.getWhitePlayer().getUsername()),
                    new Label(onGoingGame.getBlackPlayer().getUsername()),
                    new Label(String.valueOf(onGoingGame.isRated())
                    ));
            temp.setOnMouseClicked((event) -> {
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        synchronized (objectInputStream) {
                            String gameId = onGoingGame.getId();
                            System.out.println(onGoingGame.getId());
                            objectOutputStream.writeUTF("watch " + gameId);
                            objectOutputStream.flush();
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
            game_list.getChildren().add(temp);
        }
    }
}
