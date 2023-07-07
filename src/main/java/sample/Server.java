package sample;

import sample.tournament.RoundRobinTournament;
import sample.tournament.Tournament;
import sample.tournament.TournamentGame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server  {
    static Vector<ClientHandler> clients=new Vector<>();
    static Vector<User> users =new Vector<>();
    static Vector<GameRequestInformation> gameRequests=new Vector<>();
    static Vector<Tournament> tournaments=new Vector<>();
    static Vector<Game> on_going=new Vector<>();
    static int size=0;
    public static void main(String[] args) {
        readTournamentsFile();

        try {
                ServerSocket serverSocket=new ServerSocket(5642);
                Socket clientSocket;
                while (true){
                    clientSocket=serverSocket.accept();
                    ClientHandler clientHandler=new ClientHandler(clientSocket, size);
                    size++;
                    System.out.println(clientHandler.id);
                    clients.add(clientHandler);
                    Thread thread=new Thread(clientHandler);
                    thread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    FileOutputStream out =new FileOutputStream("r.txt");
                    DataOutputStream d=new DataOutputStream(out);
                    d.writeUTF("finally executed");
                    d.flush();
                    d.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    private static void readTournamentsFile() {
        try {
            Scanner t_in=new Scanner(new File("Tournaments.txt"));
            while (t_in.hasNext()){
                String read=t_in.nextLine();
                System.out.println(read);
                if(read.startsWith("tournament")){
                    DateFormat format = new SimpleDateFormat("yyyy MM dd HH mm ss", Locale.ENGLISH);
                    tournaments.add(new RoundRobinTournament(t_in.nextLine(),Long.parseLong(t_in.nextLine()),
                            Boolean.parseBoolean(t_in.nextLine()),t_in.nextLine(),tournaments.size(),
                            Integer.parseInt(t_in.nextLine()),format.parse(t_in.nextLine())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ClientHandler findPlayer(User player){
        for (ClientHandler client : clients) {
            if (client.login_player != null && client.login_player.equals(player)) {
                return client;
            }
        }
        return null;
    }
    public static void prepareTournamentGame(ClientHandler client, TournamentGame game){
        client.isPlaying=true;
        client.in_tournament=true;
        client.opponent=Server.findPlayer(game.getWhitePlayer());
        client.opponent.opponent=client;
        client.opponent.in_tournament=true;
        client.tournamentGame=game;
        client.opponent.tournamentGame=game;
        client.current_game=new Game(game,Color.Black,client.opponent.login_player);
        client.opponent.current_game=new Game(game,Color.White,client.login_player);
        client.turn=false;
        gameRequests.removeAll(client.opponent.requests);
        gameRequests.removeAll(client.requests);
        client.opponent.requests.clear();
        client.requests.clear();
        client.opponent.isPlaying=true;
        try {
            client.opponent.objectOutputStream.writeUTF("tournament game start");
            client.opponent.objectOutputStream.flush();
            client.objectOutputStream.writeUTF("tournament game start");
            client.objectOutputStream.flush();
        }catch (Exception e){e.printStackTrace();}
    }
}

