package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class WatchBoard extends FatherController implements Initializable{
    @FXML
    GridPane grid;
    @FXML
    Label notations;
    @FXML
    Button main_button;
    @FXML
    ChoiceBox<String> chatReciever;
    @FXML
    TextField massage;
    @FXML
    Label chat;
    @FXML
    Button send_button;
    private Chess chess;
    private GridPane temp_grid;
    private ArrayList<String> moves=new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatReciever.getItems().add("Group");
        chatReciever.setValue("Group");
        temp_grid=grid;
        chess=new Chess(null,grid);
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
                String lin=objectInputStream.readUTF();
                while (!lin.equals("over")) {
                    moves.add(lin);
                    String finalLin = lin;
                    Platform.runLater(() -> {
                        chess.finalMove(
                                finalLin.charAt(0) - '0', finalLin.charAt(1) - '0',
                                finalLin.charAt(2) - '0', finalLin.charAt(3) - '0', false
                        );
                        notations.setText(notations.getText() + " " + finalLin.substring(5));
                    });
                    lin = objectInputStream.readUTF();
                }
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
                                main_button.setVisible(true);
                                break;
                            }else if(line.startsWith("chat ")){
                                Platform.runLater(()-> chat.setText(chat.getText()+"\n"+line.substring(10)));
                                continue;
                            }
                            Platform.runLater(() -> {
                                chess.finalMove(
                                        line.charAt(0) - '0', line.charAt(1) - '0',
                                        line.charAt(2) - '0', line.charAt(3) - '0', false);
                                notations.setText(notations.getText() + " " + line.substring(5));
                            });
                        }
                    }catch (Exception e){
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
                    Platform.runLater(()-> notations.setText(notations.getText()+" "+massage.getText()));
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
    public void loadMain(){
        super.loadPage("main_scene");
    }
}
