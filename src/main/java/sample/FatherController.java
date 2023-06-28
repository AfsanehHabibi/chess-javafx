package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import static sample.Main.stage;

public class FatherController {
    public void loadPage(String name){
        try {
            Parent root = FXMLLoader.load(getClass().getResource(name+".fxml"));
            stage.setScene(new Scene(root, 600, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
