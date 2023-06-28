package sample;

import java.io.IOException;

import static sample.Main.objectOutputStream;

public class MainScene extends FatherController{
    public void requestGame(){
        super.loadPage("request_game");
    }
    public void loadShowTournamentsScene() {
        super.loadPage("show_tournaments");
    }
    public void loadGameHistoryScene(){
        super.loadPage("show_game_result");
    }
    public void loadAboutScene(){
        super.loadPage("about");
    }
    public void loadEdit() {
        Thread send=new Thread(()->{
            try {
                objectOutputStream.writeUTF("ready edit");
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        send.setDaemon(true);
        send.start();
        try {
            send.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.loadPage("sign_up");
    }
}
