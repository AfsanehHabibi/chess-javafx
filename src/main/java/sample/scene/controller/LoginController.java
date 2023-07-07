package sample.scene.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class LoginController extends FatherController {
    @FXML
    TextField username;
    @FXML
    PasswordField password;

    public void login() {
        Thread send =new Thread(()->{
            try {
                objectOutputStream.writeUTF("login " + username.getText() + " " + password.getText());
                objectOutputStream.flush();
                String receive=objectInputStream.readUTF();
                Platform.runLater(()-> {
                    if (receive.equals("Player found")) loadMainScene();
                });
            } catch (Exception ignored){}
        });
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
