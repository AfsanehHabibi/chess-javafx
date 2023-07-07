package sample;

import sample.server.GameManager;
import sample.user.User;
import sample.tournament.TournamentGame;

import java.util.ArrayList;

public class Game {
    User opponent;
    User whitePlayer;
    User blackPlayer;
    boolean isRated;
    boolean withClock;
    Color color;
    Clock clock;
    GameResult result;
    public ArrayList<String> moves=new ArrayList<>();
    //remove this dependency later by making server keep game managers rather than games
    private GameManager gameManager;

    public Game(GameRequestInformation request, User opponent) {
        this.isRated = request.rated;
        this.withClock = request.isTime;
        this.clock =request.clock;
        this.opponent=opponent;
    }

    public Game(GameRequestInformation request, User whitePlayer, User blackPlayer) {
        this.isRated = request.rated;
        this.withClock = request.isTime;
        this.clock = request.clock;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public Game(TournamentGame game, Color black, User opponent) {
        color=black;
        this.isRated = game.getIsRated();
        this.withClock = true;
        this.clock =game.getClock();
        this.opponent=opponent;

    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return opponent.getUsername()+
                " "+result.name()+
                " "+opponent.getRating()+
                " "+color;
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
}
