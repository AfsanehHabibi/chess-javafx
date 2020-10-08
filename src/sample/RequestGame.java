package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
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

public class RequestGame extends FatherController implements Initializable {
    @FXML
    VBox game_list;
    ArrayList<String> games_info=new ArrayList<>();
    ArrayList<String> games_on_info=new ArrayList<>();
    static Thread waitforgame ;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       refresh();
        if (waitforgame == null || !waitforgame.isAlive()) {
            waitforgame = new Thread(() -> {
                boolean g = true;
                while (g) {
                    try {
                        synchronized (objectInputStream) {
                            System.out.println("work" + this.toString());
                            if (objectInputStream.available() != 0) {
                                String re = objectInputStream.readUTF();
                                if (re.startsWith("game start") ) {
                                    Platform.runLater(this::loadGameBoard);
                                    g = false;
                                    break;
                                }else if(re.startsWith("watch")){
                                    Platform.runLater(()->{
                                        loadWatchBoard();
                                    });
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
            waitforgame.start();
        }
    }

    private void loadGameBoard() {
        super.loadPage("sample");
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
                        games_info.add(receive);
                        receive = objectInputStream.readUTF();
                    }
                    receive=objectInputStream.readUTF();
                    while (!receive.startsWith("over")) {
                        games_on_info.add(receive);
                        receive = objectInputStream.readUTF();
                    }
                    objectInputStream.notifyAll();
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
        for (int i = 0; i <games_info.size() ; i++) {
            String[] strings=games_info.get(i).split(" ");
            HBox temp=new HBox();
            temp.setSpacing(10);
            temp.getChildren().addAll(new Label(strings[2]),
                    new Label(Boolean.getBoolean(strings[8])?"Rated":"UnRated"),new Label(strings[10]),
                    new Label(
                            strings[13]+" "+strings[14]
                    ));
            System.out.println(strings[11]);
            temp.setOnMouseClicked((event)->{
                Task task=new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String chose_op =strings[6];
                        objectOutputStream.writeUTF("game with "+ chose_op +" "+strings[12]);
                        System.out.println(strings[12]);
                        objectOutputStream.flush();
                        return null;
                    }
                };
                Thread t=new Thread(task);
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
        for (int i = 0; i <games_on_info.size() ; i++) {
            String[] strings=games_on_info.get(i).split(" ");
            HBox temp=new HBox();
            temp.setSpacing(10);
            temp.getChildren().addAll(new Label(strings[0]),
                    new Label(strings[2]),
                    new Label(strings[3]
                    ));
            //System.out.println(strings[11]);
            temp.setOnMouseClicked((event)->{
                Task task=new Task() {
                    @Override
                    protected Object call() throws Exception {
                        synchronized (objectInputStream) {
                            String chose_op = strings[4];
                            System.out.println(strings[4]);
                            objectOutputStream.writeUTF("watch " + chose_op);
                            objectOutputStream.flush();
                        }
                        return null;
                    }
                };
                Thread t=new Thread(task);
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
    public void loadSeekGame(){
        super.loadPage("seek_game");
    }
    public void loadWatchBoard(){
        super.loadPage("watch_board");
    }
}
