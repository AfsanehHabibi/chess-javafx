package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.Main.objectInputStream;
import static sample.Main.objectOutputStream;

public class Controller extends FatherController implements Initializable {
    static Label copy_notations;
    static Thread this_thread;
    static Thread thread_op;
    Chess chess;
    GridPane temp_grid;
    static boolean count = true;
    static Clock game_clock;
    static Clock op_game_clock;
    @FXML
    GridPane grid;
    @FXML
    Label game_time;
    @FXML
    Label op_clock;
    @FXML
    Label notations;
    @FXML
    Button main_button;
    @FXML
    Button draw;
    @FXML
    ChoiceBox<String> chatReciever;
    @FXML
    TextField massage;
    @FXML
    Label chat;
    @FXML
    Button close_button;
    @FXML
    Button send_button;
    boolean istime = false;
    Object key = new Object();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copy_notations = notations;
        Task time;
        Task time_2;
        final Color[] first_color = new Color[1];
        boolean fal = false;
        final Integer[] seconds = new Integer[1];
        seconds[0] = 0;
        chatReciever.getItems().add("opponent");
        chatReciever.getItems().add("group");
        chatReciever.setValue("opponent");
        time = new Task() {
            @Override
            protected Object call() throws Exception {
                synchronized (key) {
                    while (game_clock.passSecond()) {
                        synchronized (this) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Platform.runLater(() -> game_time.setText(game_clock.showTime())
                        );
                        if (!count) {
                            key.notify();
                            key.wait();
                        }
                    }
                    if (game_clock.sendData()) {
                        objectOutputStream.writeUTF("player time finished");
                        objectOutputStream.flush();
                    }
                }
                return null;
            }
        };
        time_2 = new Task() {
            @Override
            protected Object call() throws Exception {
                synchronized (key) {
                    while (op_game_clock.passSecond()) {
                        synchronized (this) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Platform.runLater(() -> op_clock.setText(op_game_clock.showTime())
                        );
                        if (count) {
                            key.notify();
                            key.wait();
                        }
                    }
                }
                return null;
            }
        };

        Task t0 = new Task() {
            @Override
            protected Object call() throws Exception {
                objectOutputStream.writeUTF("ready to play");
                objectOutputStream.flush();
                String lin = objectInputStream.readUTF();
                if (lin.equals("start")) {
                    first_color[0] = (Color) objectInputStream.readObject();
                    Object object = objectInputStream.readObject();
                    if (object != null) {
                        game_clock = (Clock) object;
                        op_game_clock = new Clock(game_clock);
                        istime = true;
                        if (first_color[0] == Color.White) {
                            count = true;
                            this_thread = new Thread(time);
                            thread_op = new Thread(time_2);
                            this_thread.start();
                            thread_op.start();
                        } else {
                            count = false;
                            this_thread = new Thread(time);
                            thread_op = new Thread(time_2);
                            thread_op.start();
                            this_thread.start();
                        }
                    }
                }
                return null;
            }
        };
        Thread first = new Thread(t0);
        first.start();
        try {
            first.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Task task = new Task() {
            @Override
            protected Object call() {
                while (true) {
                    try {
                        if (objectInputStream.available() != 0) {
                            String line = objectInputStream.readUTF();
                            if (line.equals("finish lose")) {
                                main_button.setVisible(true);
                                if (istime) {
                                    op_game_clock.stopThread();
                                    game_clock.stopThread();
                                }
                                Platform.runLater(() -> {
                                    String add;
                                    if (chess.color == Color.White) {
                                        add = "0 1";
                                    } else
                                        add = "1 0";
                                    notations.setText(notations.getText() + " " + add);
                                });
                                break;
                            } else if (line.equals("finish win")) {
                                main_button.setVisible(true);
                                if (istime) {
                                    game_clock.stopThread();
                                    op_game_clock.stopThread();
                                }

                                Platform.runLater(() -> {
                                    String add;
                                    if (chess.color == Color.White) {
                                        add = "1 0";
                                    } else
                                        add = "0 1";
                                    notations.setText(notations.getText() + " " + add);
                                });
                                break;
                            } else if (line.equals("want a draw")) {
                                Platform.runLater(() -> draw.setText("opponent\nasked"));
                                continue;
                            } else if (line.equals("finish draw")) {
                                main_button.setVisible(true);
                                if (istime) {
                                    game_clock.stopThread();
                                    op_game_clock.stopThread();
                                }
                                Platform.runLater(() -> notations.setText(notations.getText() + " 0.5 0.5"));
                                break;
                            } else if (line.startsWith("chat op")) {
                                Platform.runLater(() -> chat.setText(chat.getText() + "\n" + line.substring(7)));
                                continue;
                            }
                            Color color = (Color) objectInputStream.readObject();
                            Controller.count = !Controller.count;
                            System.out.println(line.charAt(0) - '0');
                            Platform.runLater(() -> {
                                chess.chessboard[line.charAt(0) - '0'][line.charAt(1) - '0'].finalMove(
                                        line.charAt(0) - '0', line.charAt(1) - '0',
                                        line.charAt(2) - '0', line.charAt(3) - '0',
                                        chess.chessboard,temp_grid, false
                                );
                                notations.setText(notations.getText() + " " + line.substring(5));
                                chess.setClicks(color);
                                System.out.println(color);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fal)
                        break;
                }
                return null;
            }
        };
        Thread t = new Thread(task);
        t.start();
        temp_grid = grid;
        chess = new Chess(first_color[0], grid);
        chess.start();
        try {
            chess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid.add(chess.chessboard[i][j].borderPane, j, i);
            }
        }*/
    }

    public void closeChat() {
        chat.setVisible(false);
        send_button.setVisible(false);
        close_button.setVisible(false);
        massage.setVisible(false);
        chatReciever.setVisible(false);
    }

    public void sendMassage() {
        String add;
        Platform.runLater(() -> notations.setText(notations.getText() + "\n" + massage.getText()));
        if (chatReciever.getValue().equals("opponent")) add = "op";
        else
            add = "group";
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("chat " + add + " " + massage.getText());
                objectOutputStream.flush();
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
        massage.clear();
    }

    public void askDraw() {
        Platform.runLater(() -> {
            if (!draw.getText().equals("Draw")) {
                Thread send = new Thread(() -> {
                    try {
                        objectOutputStream.writeUTF("accept draw");
                        objectOutputStream.flush();
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
            }
        });
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("want draw");
                objectOutputStream.flush();
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
    }

    public void loadMain() {
        super.loadPage("main_scene");
    }

    public void askLose() {
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("accept lose");
                objectOutputStream.flush();
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
    }
}

