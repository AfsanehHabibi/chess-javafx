package sample.tournament;

import sample.Clock;
import sample.Server;
import sample.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Tournament {
    String name;
    long minRating;
    boolean isRated;
    int size;
    LinkedList<User> players;
    TournamentGame[][] results;
    Thread tourTread;
    Clock game_time;
    Date date_start;
    TournamentState tournamentState;
    int id;
    int last_played_round=0;

    public Tournament(String name, long minRating, boolean isRated, String game_time, int id, int size, Date date_start) {
        this.name = name;
        this.size=size;
        this.minRating = minRating;
        this.isRated = isRated;
        this.game_time = new Clock(game_time);
        this.id=id;
        this.date_start=date_start;
        System.out.println(date_start);
        startThread();
        tournamentState=TournamentState.Joining;
        players=new LinkedList<>();
    }

    public String[] getLastRound(){
        String[] output=new String[results[last_played_round].length];
        for (int i = 0; i <results[last_played_round].length ; i++) {
            output[i]=results[last_played_round][i].format();
        }
        return output;
    }
    protected void startThread() {
        tourTread =new Thread(()->{
            synchronized (this){
                try {
                    DateFormat format=new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    System.out.println(format.format(new Date(System.currentTimeMillis())));
                    wait(date_start.getTime()- System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("done");
        });
        tourTread.start();
    }
    public double getScoreTraditional(TournamentGame[][] results, int rounds, User player){
        double score=0;
        for (int i = 0; i <rounds ; i++) {
            for (int j = 0; j <results[0].length ; j++) {
                if ((results[i][j].blackPlayer == player || results[i][j].whitePlayer == player)) {
                    if (results[i][j].winner == null) {
                        score += 0.5;
                    } else if (player == results[i][j].winner) {
                        score +=1;
                    }
                    break;
                }
            }
        }
        return score;
    }


    public void informPlayers(int round){
        for (int i = 0; i <results[0].length ; i++) {
            if( Server.findPlayer(results[round][i].blackPlayer)!=null &&Server.findPlayer(results[round][i].whitePlayer)!=null ){
                Server.prepareTournamentGame(Server.findPlayer(results[round][i].blackPlayer),results[round][i]);
            }
        }
    }
    public boolean addPlayer(User player){
        if (players.size()<size){
            players.add(player);
            return true;
        }
        else return false;
    }
    void takeBreak() {
        synchronized (this){
            try {
                System.out.println(2*game_time.getMillis());
                wait(2L *game_time.getMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return
                name  +
                " " + minRating +
                " " + isRated +
                " " + game_time.toString()
                +" "+ id
                +" "+ size
                +" "+tournamentState;
    }

    public boolean removePlayer(User login_player) {
        if(players.contains(login_player) && tournamentState==TournamentState.Joining){
            players.remove(login_player);
            return true;
        }
        return false;
    }

    public TournamentState getTournamentState() {
        return tournamentState;
    }

    public boolean isInTournament(User user) {
        return players.contains(user);
    }
}
