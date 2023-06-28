package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;
import static sample.Main.stage;

public class Login {
    @FXML
    TextField username;
    @FXML
    PasswordField password;
    public void login(){
        Thread send =new Thread(()->{
            try {
                objectOutputStream.writeUTF("login " + username.getText() + " " + password.getText());
                objectOutputStream.flush();
                String recieve=objectInputStream.readUTF();
                Platform.runLater(()-> {
                    if (recieve.equals("Player found")) {
                        loadMainPage();
                    }
                });
            }catch (Exception e){}
        });
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void loadMainPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/main_scene.fxml"));
            stage.setScene(new Scene(root, 600, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadSignUpPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/sign_up.fxml"));
            stage.setScene(new Scene(root, 600, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
