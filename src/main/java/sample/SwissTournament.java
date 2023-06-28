package sample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SwissTournament extends Tournament{

    int[] number_of_whites;
    public SwissTournament(String name, long min_rating, boolean isRated, String game_time, int id, int size, Date date_start) {
        super(name, min_rating, isRated, game_time, id, size, date_start);
        number_of_whites=new int[size];
    }
    @Override
    protected void startThread(){
        tour_tread=new Thread(()->{
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
            setRound(0);
            System.out.println("after draw");
            for (int i = 0; i <results[0].length ; i++) {
                System.out.println(results[0][i].toString());
            }
            tournamentState=TournamentState.Drawing;
            takeBreak();
            tournamentState=TournamentState.Playing;
            System.out.println("after break");
            int round=0;
            while(round<size-1) {
                informPlayers(round);
                round++;
                setRound(round);
                takeBreak();
                System.out.println("games done");
            }
        });
        tour_tread.start();
    }

    public void setRound(int round){
        if(round==1) {
            for (int i = 0; i <size-1 ; i+=2) {
                TournamentGame game=new TournamentGame(isRated,game_time);
                game.setWhitePlayer(players.get(i));
                game.setBlackPlayer(players.get(i+1));
                results[round][i]=game;
            }
        }
        else {

        }
    }
}
