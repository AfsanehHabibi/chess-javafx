package sample;

public class TournamentGame {
    User white;
    User black;
    User winner;
    boolean isRated;
    Clock clock;

    public void setWhitePlayer(User white) {
        this.white = white;
    }

    public void setBlackPlayer(User black) {
        this.black = black;
    }
    public String format(){
        String result;
        if(winner==null){
            result="Draw by Agreement";
        }else if(winner==white){
            result="Black is Checkmated";
        }else{
            result="White is Checkmated";
        }
        return white.getUsername()+"@"+
                black.getUsername()+"@"+
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
                "white=" + white.first_name +
                ", black=" + black.first_name +
                '}';
    }
}
