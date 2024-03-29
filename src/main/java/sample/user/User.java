package sample.user;

import sample.model.game.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class User implements Serializable {
    transient String firstName;
    transient String lastName;
    String username;
    transient String password;
    transient String email;
    transient String url;
    long rating = 1200;
    transient int numberOfLose = 0;
    transient int numberOfWin = 0;
    transient int numberOfGames = 0;
    transient ArrayList<Game> games;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public User(String firstName, String lastName, String password, String email, String username, String url){
        this.firstName = firstName;
        this.lastName = lastName;
        this.password=password;
        this.email=email;
        this.username=username;
        this.url=url;
        games=new ArrayList<>();
    }

    public static String makeUserName(Vector<User> users, String first_name, String last_name){
        String temp=first_name.charAt(0)+"."+last_name;
        int counter=1;
        while (!isUserNameAvailable(temp,users)) {
            temp = temp + counter;
            counter++;
        }
        return temp;
    }

    public static void calculateRatings(boolean draw, User winner, User loser){
        winner.numberOfGames++;
        loser.numberOfGames++;
        if(!draw){
            winner.numberOfWin++;
            loser.numberOfLose++;
        }
        long temp=winner.rating;
        winner.rating+=getK(winner.numberOfGames,winner.rating)*(winner.numberOfWin -winner.numberOfLose +((
                loser.rating-winner.rating
                )/400));
        loser.rating+=getK(loser.numberOfGames,loser.rating)*(loser.numberOfWin -loser.numberOfLose +((
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

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void addGame(Game game){
        games.add(game);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public long getRating() {
        return rating;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    @Override
    public String toString() {
        return "Player{" +
                "first_name='" + firstName + '\'' +
                ", last_name='" + lastName + '\'' +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username);
    }
}
