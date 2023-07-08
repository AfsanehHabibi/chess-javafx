package sample.scene.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import sample.game.Chess;
import sample.game.view.ChatIOController;
import sample.model.chat.Message;
import sample.model.game.GameMoveRecord;
import sample.model.util.Clock;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.client.Client.objectInputStream;
import static sample.client.Client.objectOutputStream;

public class GameController extends FatherController implements Initializable {
    static Thread this_thread;
    static Thread thread_op;

    Chess chess;
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
    TableView<String> notations;
    @FXML
    TableColumn<String, String> moves;
    @FXML
    Button mainButton;
    @FXML
    Button draw;
    @FXML
    ChoiceBox<String> chatReceiver;
    @FXML
    TextField messageHolder;
    @FXML
    Label chatContent;
    @FXML
    Button closeButton;
    @FXML
    Button sendButton;

    boolean isTime = false;
    Object key = new Object();
    ChatIOController chatIOController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task time;
        Task time_2;
        final Color[] first_color = new Color[1];
        chatIOController = new ChatIOController(chatReceiver, messageHolder,
                chatContent, sendButton, closeButton, false);
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
                        isTime = true;
                        if (first_color[0] == Color.WHITE) {
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
                                mainButton.setVisible(true);
                                if (isTime) {
                                    op_game_clock.stopThread();
                                    game_clock.stopThread();
                                }
                                /*Platform.runLater(() -> {
                                    String add;
                                    if (chess.getColor() == Color.WHITE) {
                                        add = "0 1";
                                    } else
                                        add = "1 0";
                                    notations.setText(notations.getText() + " " + add);
                                });*/
                                break;
                            } else if (line.equals("finish win")) {
                                mainButton.setVisible(true);
                                if (isTime) {
                                    game_clock.stopThread();
                                    op_game_clock.stopThread();
                                }

                                /*Platform.runLater(() -> {
                                    String add;
                                    if (chess.getColor() == Color.WHITE) {
                                        add = "1 0";
                                    } else
                                        add = "0 1";
                                    notations.setText(notations.getText() + " " + add);
                                });*/
                                break;
                            } else if (line.equals("want a draw")) {
                                Platform.runLater(() -> draw.setText("opponent\nasked"));
                            } else if (line.equals("finish draw")) {
                                mainButton.setVisible(true);
                                if (isTime) {
                                    game_clock.stopThread();
                                    op_game_clock.stopThread();
                                }
                                break;
                            } else if (line.startsWith("chat")) {
                                Message message = (Message) objectInputStream.readObject();
                                Platform.runLater(() -> chatIOController.addNewMessage(message));
                            } else if (line.startsWith("new move")) {
                                GameMoveRecord moveRecord = (GameMoveRecord) objectInputStream.readObject();
                                Platform.runLater(() -> chess.updateBoardAndNotation(moveRecord));
                            }else if (line.startsWith("allow to move")) {
                                ChessGameLogic gameLogic = (ChessGameLogic) objectInputStream.readObject();
                                Platform.runLater(() -> chess.setClicks(gameLogic));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        Thread t = new Thread(task);
        t.start();
        chess = new Chess(first_color[0], grid, notations, moves);
        chess.start();
        try {
            chess.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeChat() {
        chatIOController.closeChat();
    }

    public void sendMassage() {
        Message message = chatIOController.getMessageToBeSendAndClear();
        Thread send = new Thread(() -> {
            try {
                objectOutputStream.writeUTF("chat");
                objectOutputStream.flush();
                objectOutputStream.writeObject(message);
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

