package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class SignUp extends FatherController implements Initializable{
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
    String url="/image/icon/icons8-customer-50.png";
    boolean edit=false;
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
            System.out.println("no");
            return;
        }
        Thread send=new Thread(()->{
            try {
                String info;
                if(edit)
                    info="edit ";
                else
                    info="add ";
                objectOutputStream.writeUTF(info+ first_name.getText() + " " +
                        last_name.getText() + " " +
                        password.getText() + " " + email.getText()+" "
                        +url
                );
                objectOutputStream.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadMainScene();
    }

    private void loadMainScene() {
        super.loadPage("main_scene");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final String[] recieve = new String[1];
        Thread send=new Thread(()->{
            try {
                objectOutputStream.writeUTF("is edit");
                objectOutputStream.flush();
                recieve[0] =objectInputStream.readUTF();
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
        if(recieve[0].startsWith("no"))
            return;
        edit=true;
        String[] info=recieve[0].split(" ");
        first_name.setText(info[1]);
        last_name.setText(info[2]);
        email.setText(info[3]);
        try{
                user_picture.setImage(new Image(info[4]));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
