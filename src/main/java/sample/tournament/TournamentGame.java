package sample.tournament;

import sample.model.util.Clock;
import sample.user.User;

public class TournamentGame {
    User whitePlayer;
    User blackPlayer;
    User winner;
    boolean isRated;
    Clock clock;

    public void setWhitePlayer(User white) {
        this.whitePlayer = white;
    }

    public void setBlackPlayer(User black) {
        this.blackPlayer = black;
    }

    public String format(){
        String result;
        if(winner==null){
            result="Draw by Agreement";
        }else if(winner== whitePlayer){
            result="Black is Checkmated";
        }else{
            result="White is Checkmated";
        }
        return whitePlayer.getUsername()+"@"+
                blackPlayer.getUsername()+"@"+
                result;
    }

    public TournamentGame(boolean isRated, Clock clock) {
        this.isRated = isRated;
        this.clock = clock;
    }

    public void setWinner(User winner){
        this.winner=winner;
    }

    @Override
    public String toString() {
        return "TournamentGame{" +
                "white=" + whitePlayer.getFirstName() +
                ", black=" + blackPlayer.getFirstName() +
                '}';
    }

    public User getWhitePlayer() {
        return whitePlayer;
    }

    public User getBlackPlayer() {
        return blackPlayer;
    }

    public boolean getIsRated() {
        return isRated;
    }

    public Clock getClock() {
        return clock;
    }
}
