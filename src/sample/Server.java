package sample;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static sample.Server.*;


public class Server  {
    static Vector<ClientHandler> clients=new Vector<>();
    static Vector<Player> players=new Vector<>();
    static Vector<GameRequestInformation> gameRequests=new Vector<>();
    static Vector<Tournament> tournaments=new Vector<>();
    static Vector<Game> on_going=new Vector<>();
    static int size=0;
    public static void main(String[] args) {
        try {
            Scanner t_in=new Scanner(new File("Tournaments.txt"));
            while (t_in.hasNext()){
                String read=t_in.nextLine();
                System.out.println(read);
                if(read.startsWith("tournament")){
                    DateFormat format = new SimpleDateFormat("yyyy MM dd HH mm ss", Locale.ENGLISH);
                    tournaments.add(new RoundRobinTournament(t_in.nextLine(),Long.parseLong(t_in.nextLine()),
                            Boolean.valueOf(t_in.nextLine()),t_in.nextLine(),tournaments.size(),
                            Integer.parseInt(t_in.nextLine()),format.parse(t_in.nextLine())));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public static ClientHandler findPlayer(Player player){
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
        client.opponent=Server.findPlayer(game.white);
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

class ClientHandler implements Runnable {
    Socket socket;
    Player login_player;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    int id;
    boolean in_tournament=false;
    Boolean isLoggedin;
    Boolean isconnected;
    boolean isWatching;
    Game watch_game;
    boolean turn=false;
    Boolean isPlaying;
    Tournament tournament;
    ArrayList<GameRequestInformation> requests;
    ClientHandler opponent;
    Game current_game;
    TournamentGame tournamentGame;
    ArrayList<ClientHandler> audiences=new ArrayList<>();
    boolean join_game;

    ClientHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        isPlaying=false;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream=new ObjectInputStream(socket.getInputStream());
        isLoggedin = false;
        isconnected=true;
        requests=new ArrayList<>();
    }

    @Override
    public void run() {
        while (isconnected) {
            try {
                String receive = objectInputStream.readUTF();
                if(receive.startsWith("login")){
                    boolean find=false;
                    String[] split= receive.split(" ");
                    for (Player player:players) {
                        try {
                            if (player.getUsername().equals(split[1]) && player.getPassword().equals(split[2])) {
                                objectOutputStream.writeUTF("Player found");
                                login_player=player;
                                isLoggedin=true;
                                objectOutputStream.flush();
                                find=true;
                                break;
                            }
                        }catch (Exception e){
                            System.out.println(e);
                        }
                    }
                    if(!find) {
                        objectOutputStream.writeUTF("Player doesn't exist");
                        objectOutputStream.flush();
                    }
                }else if(receive.startsWith("ready edit")){
                    String line=objectInputStream.readUTF();
                    if(line.equals("is edit")){
                        objectOutputStream.writeUTF("yes "+login_player.getFirst_name() + " " +
                                login_player.getLast_name() + " " +
                                login_player.email+" "
                                +login_player.url
                        );
                        objectOutputStream.flush();
                    }
                }
                else if(receive.startsWith("is edit")){
                    objectOutputStream.writeUTF("no");
                    objectOutputStream.flush();
                }
                else if(receive.startsWith("game in Tournament")){
                    String[] strings= receive.split(" ");
                    if(strings[4].equals("Join")) {
                        if (tournaments.get(Integer.parseInt(strings[3])).addPlayer(login_player)) {
                            objectOutputStream.writeUTF("Leave");
                            objectOutputStream.flush();
                        } else {
                            objectOutputStream.writeUTF("Capacity full");
                            objectOutputStream.flush();
                        }
                    }if(strings[4].equals("Leave")) {
                        if (tournaments.get(Integer.parseInt(strings[3])).removePlayer(login_player)) {
                            objectOutputStream.writeUTF("Join");
                            objectOutputStream.flush();
                        } else {
                            objectOutputStream.writeUTF("Can't resign");
                            objectOutputStream.flush();
                        }
                    }else if(strings[4].equals("Tournament")){
                        if(tournaments.get(Integer.parseInt(strings[3])).players.contains(login_player)){
                            objectOutputStream.writeUTF("load score board");
                            tournament=tournaments.get(Integer.parseInt(strings[3]));
                            objectOutputStream.flush();
                        }else{
                            objectOutputStream.writeUTF("accesses  denied");
                            objectOutputStream.flush();
                        }
                    }
                }
                else if(receive.startsWith("add")){
                    String[] strings= receive.split(" ");
                    Player player=new Player(strings[1],strings[2],strings[3],strings[4]
                            ,Player.makeUserName(players,strings[1],strings[2]),strings[5]);
                    players.add(player);
                    login_player=player;
                }
                else if(receive.startsWith("seek")){
                    String[] strings= receive.split(" ");
                    GameRequestInformation temp;
                    if(strings.length<6){
                        temp=new GameRequestInformation(login_player,id,Boolean.valueOf(strings[1])
                                ,Boolean.valueOf(strings[2]),strings[3].equals("Black")
                                ?Color.Black: Color.White,requests.size(),null);
                    }
                    else {
                        temp = new GameRequestInformation(login_player, id, Boolean.valueOf(strings[1])
                                , Boolean.valueOf(strings[2]), strings[3].equals("Black")
                                ? Color.Black : Color.White,requests.size(), strings[4] + " " + strings[5]);
                        System.out.println(strings[4] + " " + strings[5]);
                    }
                    gameRequests.add(temp);
                    requests.add(temp);
                }else if(receive.startsWith("reply of game")){
                    System.out.println("result of game");
                    String[] strings=receive.split(" ");
                    //login_player.games.get(0)
                    String confirmation=objectInputStream.readUTF();
                    if(confirmation.equals("ready to receive notations")){
                        System.out.println("ready to receive notations");
                        System.out.println("login player moves result\n"+login_player.games.get(0).moves);
                        for (String move : login_player.games.get(0).moves) {
                            objectOutputStream.writeUTF(move);
                            System.out.println(move);
                            objectOutputStream.flush();
                        }
                        objectOutputStream.writeUTF("over");
                        objectOutputStream.flush();
                    }
                }
                else if(receive.startsWith("games information")){
                    for (GameRequestInformation gameRequest : gameRequests) {
                        if (!gameRequest.getSeek_player().equals(login_player)) {
                            objectOutputStream.writeUTF(gameRequest.toString());
                            objectOutputStream.flush();
                        }
                    }
                    objectOutputStream.writeUTF("over");
                    objectOutputStream.flush();
                    for (int i = 0; i <on_going.size() ; i++) {
                        objectOutputStream.writeUTF(on_going.get(i).toString()+" "+i);
                        objectOutputStream.flush();
                    }
                    objectOutputStream.writeUTF("over");
                    objectOutputStream.flush();
                }else if(receive.startsWith("watch")){
                    String[] strings=receive.split(" ");
                    isWatching=true;
                    watch_game=on_going.get(Integer.parseInt(strings[1]));
                    Objects.requireNonNull(findPlayer(watch_game.opponent)).audiences.add(this);
                    Objects.requireNonNull(findPlayer(watch_game.opponent).opponent).audiences.add(this);
                    objectOutputStream.writeUTF("watch this");
                    objectOutputStream.flush();
                }else if(receive.equals("ready to watch")){
                    for (int i = 0; i <watch_game.moves.size() ; i++) {
                        objectOutputStream.writeUTF(watch_game.moves.get(i));
                        objectOutputStream.flush();
                    }
                    objectOutputStream.writeUTF("over");
                    objectOutputStream.flush();
                }
                else if(receive.startsWith("exit")){
                    isconnected=false;
                    if(isPlaying){
                        for (int i = 0; i <audiences.size() ; i++) {
                            audiences.get(i).objectOutputStream.writeUTF("over");
                            audiences.get(i).objectOutputStream.flush();
                        }
                        if(in_tournament){
                            tournamentGame.setWinner(opponent.login_player);
                            in_tournament=false;
                            opponent.in_tournament=false;
                        }
                        current_game.setResult(GameResult.Lose);
                        opponent.current_game.setResult(GameResult.Win);
                        login_player.addGame(current_game);
                        opponent.login_player.addGame(opponent.current_game);
                        if(current_game.isRated)
                            Player.calculateRatings(false,opponent.login_player,login_player);
                        isPlaying=false;
                        opponent.isPlaying=false;
                        opponent.objectOutputStream.writeUTF("finish win");
                        opponent.objectOutputStream.flush();
                    }
                }
                else if(receive.startsWith("tournaments information")){
                    for (int i = 0; i <tournaments.size() ; i++) {
                        String temp;
                        if(tournaments.get(i).tournamentState!=TournamentState.Joining)
                            temp="Tournament closed";
                        else if(tournaments.get(i).players.contains(login_player))
                            temp="Leave";
                        else
                            temp="Join";
                            objectOutputStream.writeUTF(tournaments.get(i).toString()+" "+temp);
                            objectOutputStream.flush();
                    }
                    objectOutputStream.writeUTF("over");
                    objectOutputStream.flush();
                }else if(receive.startsWith("edit")){
                    String[] strings= receive.split(" ");
                    try {
                        login_player.setFirst_name(strings[2]);
                        login_player.setLast_name(strings[3]);
                        login_player.setPassword(strings[4]);
                        login_player.setEmail(strings[5]);
                        login_player.setUrl(strings[6]);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(receive.equals("score board")){
                    String[] strings=tournament.getLastRound();
                    for (int i = 0; i <strings.length ; i++) {
                        objectOutputStream.writeUTF(strings[i]);
                        objectOutputStream.flush();
                    }
                    objectOutputStream.writeUTF("over");
                    objectOutputStream.flush();
                }
                else if(receive.startsWith("game with")){
                    String[] strings= receive.split(" ");
                    isPlaying=true;
                    opponent=clients.get(Integer.parseInt(strings[2]));
                    opponent.opponent=this;
                    current_game=new Game(opponent.requests.get(Integer.parseInt(strings[3])),opponent.login_player);
                    opponent.current_game=new Game(opponent.requests.get(Integer.parseInt(strings[3])),this.login_player);
                    on_going.add(current_game);
                    turn=false;
                    if(opponent.requests.get(Integer.parseInt(strings[3])).color==Color.Black){
                        opponent.current_game.setColor(Color.Black);
                        this.current_game.setColor(Color.White);
                    }
                    else {
                        opponent.current_game.setColor(Color.White);
                        this.current_game.setColor(Color.Black);
                    }
                    gameRequests.removeAll(opponent.requests);
                    gameRequests.removeAll(this.requests);
                    opponent.requests.clear();
                    this.requests.clear();
                    opponent.isPlaying=true;
                    System.out.println(Integer.parseInt(strings[2]));
                    clients.get(Integer.parseInt(strings[2])).objectOutputStream.writeUTF("game starting");
                    clients.get(Integer.parseInt(strings[2])).objectOutputStream.flush();
                    objectOutputStream.writeUTF("game starting");
                    objectOutputStream.flush();
                }else if(receive.equals("accept draw")){
                    if(in_tournament){
                        tournamentGame.setWinner(null);
                        in_tournament=false;
                        opponent.in_tournament=false;
                    }
                    for (int i = 0; i <audiences.size() ; i++) {
                        audiences.get(i).objectOutputStream.writeUTF("over");
                        audiences.get(i).objectOutputStream.flush();
                    }
                    on_going.remove(current_game);
                    current_game.setResult(GameResult.Draw);
                    opponent.current_game.setResult(GameResult.Draw);
                    login_player.addGame(current_game);
                    opponent.login_player.addGame(opponent.current_game);
                    System.out.println("login player moves\n"+login_player.games.get(0).moves);
                    if(current_game.isRated)
                    Player.calculateRatings(true,opponent.login_player,login_player);
                    isPlaying=false;
                    opponent.isPlaying=false;
                    objectOutputStream.writeUTF("finish draw");
                    objectOutputStream.flush();
                    opponent.objectOutputStream.writeUTF("finish draw");
                    opponent.objectOutputStream.flush();
                }
                else if(receive.startsWith("chat")){
                    if(receive.startsWith("chat op")) {
                        opponent.objectOutputStream.writeUTF(receive);
                        opponent.objectOutputStream.flush();
                    }else {
                        for (ClientHandler clientHandler:audiences) {
                            clientHandler.objectOutputStream.writeUTF(receive);
                            clientHandler.objectOutputStream.flush();
                        }
                    }
                }
                else if(receive.equals("games history")){
                    for (int i = 0; i <login_player.games.size() ; i++) {
                            objectOutputStream.writeUTF(login_player.games.get(i).toString());
                            objectOutputStream.flush();
                    }
                    objectOutputStream.writeUTF("over");
                    objectOutputStream.flush();
                }
                else if(receive.equals("want draw")){
                    opponent.objectOutputStream.writeUTF("want a draw");
                    opponent.objectOutputStream.flush();
                }
                else if(receive.equals("player time finished")|| receive.equals("player checkmate")||
                        receive.equals("accept lose") ){
                    if(in_tournament){
                        tournamentGame.setWinner(opponent.login_player);
                        in_tournament=false;
                        opponent.in_tournament=false;
                    }
                    for (int i = 0; i <audiences.size() ; i++) {
                        audiences.get(i).objectOutputStream.writeUTF("over");
                        audiences.get(i).objectOutputStream.flush();
                    }
                    on_going.remove(current_game);
                    current_game.setResult(GameResult.Lose);
                    opponent.current_game.setResult(GameResult.Win);
                    login_player.addGame(current_game);
                    opponent.login_player.addGame(opponent.current_game);
                    System.out.println("login player moves\n"+login_player.games.get(0).moves);
                    if(current_game.isRated)
                    Player.calculateRatings(false,opponent.login_player,login_player);
                    objectOutputStream.writeUTF("finish lose");
                    objectOutputStream.flush();
                    isPlaying=false;
                    opponent.isPlaying=false;
                    opponent.objectOutputStream.writeUTF("finish win");
                    opponent.objectOutputStream.flush();
                }
                else if (isPlaying ) {
                    if(receive.startsWith("ready to play")){
                        join_game=true;
                        if(opponent.join_game){
                            if(opponent.current_game.color==Color.White){
                                opponent.turn=true;
                            }
                            else if(this.current_game.color==Color.White){
                                turn=true;
                            }
                            objectOutputStream.writeUTF("start");
                            objectOutputStream.flush();
                            objectOutputStream.writeObject(this.current_game.color);
                            objectOutputStream.flush();
                            objectOutputStream.writeObject(current_game.clock);
                            objectOutputStream.flush();
                            opponent.objectOutputStream.writeUTF("start");
                            opponent.objectOutputStream.flush();
                            opponent.objectOutputStream.writeObject(opponent.current_game.color);
                            opponent.objectOutputStream.flush();
                            opponent.objectOutputStream.writeObject(opponent.current_game.clock);
                            opponent.objectOutputStream.flush();

                        }
                    }
                    else if (turn){
                        current_game.moves.add(receive);
                        opponent.current_game.moves.add(receive);
                    opponent.objectOutputStream.writeUTF(receive);
                    opponent.objectOutputStream.flush();
                    opponent.objectOutputStream.writeObject(opponent.current_game.color);
                    opponent.objectOutputStream.flush();
                        for (int i = 0; i <audiences.size() ; i++) {
                            audiences.get(i).objectOutputStream.writeUTF(receive);
                            audiences.get(i).objectOutputStream.flush();
                        }
                    opponent.turn = true;
                    turn = false;
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            try {
                objectInputStream.close();
                objectOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}