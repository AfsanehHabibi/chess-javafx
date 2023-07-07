package sample;

import sample.user.User;

import java.io.Serializable;

public class GameRequestInformation implements Serializable{
    User seekerPlayer;
    int client_id;
    boolean rated;
    boolean isTime;
    Color color;
    Clock clock;
    int player_code;

    public User getSeekerPlayer() {
        return seekerPlayer;
    }

    public GameRequestInformation(User seek_player, int client_id, boolean rated, boolean isTime, Color color,
                                  int player_code, String time) {
        this.seekerPlayer = seek_player;
        this.client_id = client_id;
        this.rated = rated;
        this.isTime = isTime;
        this.color = color;
        System.out.println(isTime+" "+time);
        if(isTime) {
            clock = new Clock(time);
        }
        this.player_code=player_code;
    }

    @Override
    public String toString() {
        return "GameRequestInformation{" +
                " seek_player= " + seekerPlayer.getUsername() +
                " seek_player_rating= " + seekerPlayer.getRating() +
                " ,client_id= " + client_id +
                " ,rated= " + rated +
                " ,color= " + color +
                " ,player_code= "+player_code+
                " "+(clock==null ? "no clock":clock.toString())+
                " }";
    }

    public int getClient_id() {
        return client_id;
    }

    public boolean isRated() {
        return rated;
    }

    public boolean isTime() {
        return isTime;
    }

    public Color getColor() {
        return color;
    }

    public Clock getClock() {
        return clock;
    }

    public int getPlayer_code() {
        return player_code;
    }
}
