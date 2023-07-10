package sample.scene.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController extends FatherController implements Initializable{
    @FXML
    TextField first_name;
    @FXML
    TextField last_name;
    @FXML
    TextField email;
    @FXML
    PasswordField password;
    @FXML
    PasswordField re_password;
    @FXML
    ImageView user_picture;
    @FXML
    Label password_warn;
    @FXML
    Label email_warn;

    String url = "/image/icon/icons8-customer-50.png";
    boolean edit = false;

    public void imageChoose(){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            url=selectedFile.getAbsoluteFile().toURI().toString();
            user_picture.setImage(new Image(url));
        }
    }
    public void passwordWarn(){
        if ((password.getText()).length()<5) {
            password_warn.setText("must be at least 5 characters.");
        }
        else {
            password_warn.setText("");
        }
    }

    public void emailWarn(){
        if(!checkEmailValidity())
            email_warn.setText("email format is invalid");
        else
            email_warn.setText("");
    }

    private boolean checkAccountValidity(){
        if(first_name.getText().equals("") || first_name.getText()==null)
            return false;
        if(last_name.getText().equals("") || first_name.getText()==null)
            return false;
        return (checkEmailValidity() && checkPasswordValidity() );
    }

    private boolean checkPasswordValidity() {
        return (password.getText()).equals(re_password.getText()) && (password.getText()).length() >= 5;
    }

    private boolean checkEmailValidity(){
        return (email.getText().matches("(.+)@(.+).com"));
    }

    public void signUp(){
        if (!checkAccountValidity()) {
            return;
        }
        String info;
        if(edit)
            info="edit ";
        else
            info="add ";
        serverStreamer.writeString(info+ first_name.getText() + " " +
                last_name.getText() + " " +
                password.getText() + " " + email.getText() + " "
                + url);
        loadMainScene();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverStreamer.writeString("is edit");
        String receive = serverStreamer.readString();
        if(receive.startsWith("no"))
            return;
        edit = true;
        String[] info = receive.split(" ");
        first_name.setText(info[1]);
        last_name.setText(info[2]);
        email.setText(info[3]);
        try {
            user_picture.setImage(new Image(info[4]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
