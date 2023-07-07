package sample.scene.controller;

import java.io.IOException;

import static sample.Main.objectOutputStream;

public class MainSceneController extends FatherController {

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
        loadSignUpScene();
    }
}
