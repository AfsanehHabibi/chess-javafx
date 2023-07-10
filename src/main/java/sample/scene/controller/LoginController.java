package sample.scene.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends FatherController {
    @FXML
    TextField username;
    @FXML
    PasswordField password;

    public void login() {
        serverStreamer.writeString("login " + username.getText() + " " + password.getText());
        String receive = serverStreamer.readString();
        Platform.runLater(()-> {
            if (receive.equals("Player found")) loadMainScene();
        });
    }
}
