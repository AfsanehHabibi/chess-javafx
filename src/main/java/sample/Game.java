package sample;

import java.util.ArrayList;

public class Game {
    User opponent;
    boolean isRated;
    boolean withClock;
    Color color;
    Clock clock;
    GameResult result;
    ArrayList<String> moves=new ArrayList<>();

    public Game(GameRequestInformation request, User opponent) {
        this.isRated = request.rated;
        this.withClock = request.isTime;
        this.clock =request.clock;
        this.opponent=opponent;
    }

    public Game(TournamentGame game, Color black, User opponent) {
        color=black;
        this.isRated = game.isRated;
        this.withClock = true;
        this.clock =game.clock;
        this.opponent=opponent;

    }

    public void setResult(GameResult result) {
        this.result = result;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String stringResult(){
        if(result==GameResult.Win)
            return "1";
        if(result==GameResult.Lose)
            return "0";
        else
            return "0.5";
    }

    @Override
    public String toString() {
        return opponent.getUsername()+
                " "+stringResult()+
                " "+opponent.getRating()+
                " "+color;
    }
}
