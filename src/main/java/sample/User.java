package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class User implements Serializable{
    String first_name;
    String last_name;
    String username;
    String password;
    String email;
    String url;
    long rating=1200;
    int number_of_lose=0;
    int number_of_win=0;
    int number_of_games=0;
    ArrayList<Game> games;

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getPassword() {
        return password;
    }

    public User(String first_name, String last_name, String password, String email, String username, String url){
        this.first_name=first_name;
        this.last_name=last_name;
        this.password=password;
        this.email=email;
        this.username=username;
        this.url=url;
        games=new ArrayList<>();
    }

    static String makeUserName(Vector<User> users, String first_name, String last_name){
        String temp=first_name.charAt(0)+"."+last_name;
        int counter=1;
        while (!isUserNameAvailable(temp,users)) {
            temp = temp + counter;
            counter++;
        }
        return temp;
    }
    public static void calculateRatings(boolean draw, User winner, User loser){
            winner.number_of_games++;
            loser.number_of_games++;
        if(!draw){
            winner.number_of_win++;
            loser.number_of_lose++;
        }
        long temp=winner.rating;
        winner.rating+=getK(winner.number_of_games,winner.rating)*(winner.number_of_win-winner.number_of_lose+((
                loser.rating-winner.rating
                )/400));
        loser.rating+=getK(loser.number_of_games,loser.rating)*(loser.number_of_win-loser.number_of_lose+((
                temp-loser.rating
                )/400));
    }
    private static int getK(int games, long rating){
        if(games<30){
            return 30;
        }
        else if(rating<2400){
            return 30;
        }else{
            return 20;
        }
    }
    private static boolean isUserNameAvailable(String user_name, Vector<User> players){
        for (User player : players) {
            if (player.getUsername().equals(user_name))
                return false;
        }
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }



    public void addGame(Game game){
        games.add(game);
    }
    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public long getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Player{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", rating=" + rating +
                '}';
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
