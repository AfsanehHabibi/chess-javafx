package sample;

import java.io.Serializable;

public class GameRequestInformation implements Serializable{
    Player seek_player;
    int client_id;
    boolean rated;
    boolean isTime;
    Color color;
    Clock clock;
    int player_code;

    public Player getSeek_player() {
        return seek_player;
    }


    public GameRequestInformation(Player seek_player, int client_id, boolean rated, boolean isTime, Color color,
            int player_code,String time) {
        this.seek_player = seek_player;
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
                " seek_player= " + seek_player.username +
                " seek_player_rating= " + seek_player.rating +
                " ,client_id= " + client_id +
                " ,rated= " + rated +
                " ,color= " + color +
                " ,player_code= "+player_code+
                " "+(clock==null ? "no clock":clock.toString())+
                " }";
    }
}
