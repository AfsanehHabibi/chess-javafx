package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main extends Application {

    public static Stage stage;
    static Thread t;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Platform.setImplicitExit(false);
        Task read=new Task() {
            @Override
            protected Object call() throws Exception {
                Socket socket=new Socket("localhost",5642);
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
                System.out.println("connected");
                return null;
            }
        };
        t = new Thread(read);
        t.setDaemon(true);
        t.start();
        t.join();
        try {
            stage=primaryStage;
            stage.setResizable(false);
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            primaryStage.setTitle("CHESS");
            primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/image/icon/chess_icon_blue.jpg")));
            primaryStage.setScene(new Scene(root, 600, 600));
            primaryStage.setOnCloseRequest((E)->{
                try{
                    objectOutputStream.writeUTF("exit");
                    objectOutputStream.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            primaryStage.show();
        }catch (Exception e){
            System.out.println("loader error");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
