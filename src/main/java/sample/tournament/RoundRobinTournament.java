package sample.tournament;

import sample.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class RoundRobinTournament extends Tournament {

    public RoundRobinTournament(String name, long min_rating, boolean isRated, String clock, int id, int size, Date date_start) {
        super(name, min_rating, isRated, clock, id, size, date_start);
        results=new TournamentGame[size-1][size/2];
    }

    @Override
    protected void startThread(){
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
            draw();
            System.out.println("after draw");
            for (int i = 0; i <results[0].length ; i++) {
                System.out.println(results[0][i].toString());
            }
            tournamentState=TournamentState.Drawing;
            takeBreak();
            tournamentState=TournamentState.Playing;
            int round=0;
            while(round<size-1) {
                informPlayers(round);
                round++;
                last_played_round=round;
                takeBreak();
            }
        });
        tourTread.start();
    }

    private void draw() {
        for (int i = 0; i <size-1 ; i++) {
            setRound(i);
        }
    }

    public void setRound(int round){
        if(round!=1) {
            rotatePlayers( players);
        }
            if (players.size() % 2 == 0) {
                for (int i = 0; i <players.size()/2; i++) {
                    TournamentGame game=new TournamentGame(isRated,game_time);
                    game.setWhitePlayer(players.get(i));
                    results[round][i]=game;
                }
                for (int i = players.size()-1,counter=0; i >=players.size()/2; i--,counter++) {
                    results[round][counter].setBlackPlayer(players.get(i));
                }
            }
    }

    private void rotatePlayers(LinkedList<User> players) {
        User temp=players.getLast();
        players.removeLast();
        players.add(1,temp);
    }
}
