package sample.scene.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

import static sample.Main.objectOutputStream;
import static sample.Main.stage;

public class FatherController {

    public void loadPage(String name){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/"+name+".fxml"));
            stage.setScene(new Scene(root, 600, 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadShowTournamentsScene() {
        loadPage("show_tournaments");
    }

    public void loadGameHistoryScene(){
        loadPage("show_game_result");
    }

    public void loadAboutScene(){
        loadPage("about");
    }

    public void loadSignUpScene() {
        loadPage("sign_up");
    }

    public void loadMainScene() {
        loadPage("main_scene");
    }

    public void loadGameBoardScene() {
        loadPage("sample");
    }

    public void loadWatchBoardScene() {
        loadPage("watch_board");
    }

    public void loadRequestGameScene() {
        loadPage("request_game");
    }

    public void loadReplyScene() {
        loadPage("reply");
    }

    public void loadSeekGameScene() {
        loadPage("seek_game");
    }
}
