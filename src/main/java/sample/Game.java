package sample;

import sample.server.GameManager;
import sample.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Game implements Serializable {
    User whitePlayer;
    User blackPlayer;
    boolean isRated;
    boolean withClock;
    transient Clock clock;
    GameResult result;
    public ArrayList<String> moves = new ArrayList<>();
    //remove this dependency later by making server keep game managers rather than games
    transient private GameManager gameManager;
    String id;

    public Game(GameRequestInformation request, User whitePlayer, User blackPlayer) {
        this.isRated = request.rated;
        this.withClock = request.isTime;
        this.clock = request.clock;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.id = UUID.randomUUID().toString();
    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "whitePlayer=" + whitePlayer.getUsername() +
                ", blackPlayer=" + blackPlayer.getUsername() +
                ", isRated=" + isRated +
                ", withClock=" + withClock +
                ", result=" + result +
                '}';
    }

    public Clock getClock() {
        return clock;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public User getWhitePlayer() {
        return whitePlayer;
    }

    public User getBlackPlayer() {
        return blackPlayer;
    }

    public boolean isWithClock() {
        return withClock;
    }

    public String getId() {
        return id;
    }
}
