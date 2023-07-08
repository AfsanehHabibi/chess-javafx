package sample.server;

import sample.model.util.Clock;
import sample.model.util.Color;
import sample.model.game.Game;
import sample.model.game.GameRequestInformation;
import sample.game.logic.ChessGameLogic;
import sample.game.model.Move;
import sample.tournament.Tournament;
import sample.tournament.TournamentGame;
import sample.tournament.TournamentState;
import sample.user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static sample.server.Server.*;

public class ClientHandler implements Runnable {
    Socket socket;
    User login_player;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    int id;
    Boolean isLoggedIn;
    Boolean isConnected;
    boolean isWatching;
    Game watch_game;
    public Boolean isPlaying;
    Tournament tournament;
    ArrayList<GameRequestInformation> requests;
    TournamentGame tournamentGame;
    private GameManager currentGameManager;

    ClientHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        isPlaying = false;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        isLoggedIn = false;
        isConnected = true;
        requests = new ArrayList<>();
    }

    @Override
    public void run() {
        while (isConnected) {
            try {
                String receive = objectInputStream.readUTF();
                System.out.println("received "+receive);
                if (receive.startsWith("login")) {
                    executeClientLoginRequest(receive);
                } else if (receive.startsWith("ready edit")) {
                    executeClientEditRequest();
                } else if (receive.startsWith("is edit")) {
                    executeClientIsEditRequest();
                } else if (receive.startsWith("game in Tournament")) {
                    executeClientGameInTournamentRequest(receive);
                } else if (receive.startsWith("add")) {
                    signUp(receive);
                } else if (receive.startsWith("seek")) {
                    requestNewGame(receive);
                } else if (receive.startsWith("reply of game")) {
                    getGameResult(receive);
                } else if (receive.startsWith("games information")) {
                    requestGamesInformation();
                } else if (receive.startsWith("watch")) {
                    watchOnGoingGame(receive);
                } else if (receive.equals("ready to watch")) {
                    readyToWatchOnGoingGame();
                } else if (receive.startsWith("exit")) {
                    exist();
                } else if (receive.startsWith("tournaments information")) {
                    tournamentsInformation();
                } else if (receive.startsWith("edit")) {
                    editUserInfo(receive);
                } else if (receive.equals("score board")) {
                    getScoreBoard();
                } else if (receive.startsWith("game with")) {
                    acceptGameRequest(receive);
                } else if (receive.equals("accept draw")) {
                    acceptDraw();
                } else if (receive.startsWith("chat")) {
                    chat(receive);
                } else if (receive.equals("games history")) {
                    requestGameHistory();
                } else if (receive.equals("want draw")) {
                    requestDraw();
                } else if (receive.equals("player time finished") || receive.equals("player checkmate") ||
                        receive.equals("accept lose")) {
                    playerHasLost();
                } else if (isPlaying) {
                    play(receive);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            objectInputStream.close();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play(String receive) throws IOException {
        if (receive.startsWith("ready to play")) {
            currentGameManager.readyToStart(this);
        } else {
            Move move = null;
            try {
                move = (Move) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            currentGameManager.move(this, move, receive);
        }
    }

    public void notifyToMove(ChessGameLogic lastVersion) {
        try {
            objectOutputStream.writeUTF("allow to move");
            objectOutputStream.flush();
            objectOutputStream.writeObject(lastVersion);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNewGameMove(String notation, ChessGameLogic board) {
        try {
            objectOutputStream.writeUTF("new move");
            objectOutputStream.flush();
            objectOutputStream.writeObject(board);
            objectOutputStream.flush();
            objectOutputStream.writeUTF(notation);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playerHasLost() throws IOException {
        currentGameManager.acceptLose(this);
    }

    private void requestDraw() throws IOException {
        currentGameManager.requestDraw(this);
    }

    private void requestGameHistory() throws IOException {
        for (int i = 0; i < login_player.getGames().size(); i++) {
            objectOutputStream.writeUTF(login_player.getGames().get(i).toString());
            objectOutputStream.flush();
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
    }

    private void chat(String receive) throws IOException {
        if (receive.startsWith("chat op")) {
            currentGameManager.messageOpponent(this, receive);
        } else {
            currentGameManager.messageAudiences(this, receive);
        }
    }

    private void acceptDraw() throws IOException {
        currentGameManager.acceptDraw(this);
    }

    private void acceptGameRequest(String receive) throws IOException {
        String[] strings = receive.split(" ");
        ClientHandler opponent = clients.get(Integer.parseInt(strings[2]));
        GameRequestInformation request = opponent.requests.get(Integer.parseInt(strings[3]));
        new GameManager(this, opponent,
                request);
    }

    private void getScoreBoard() throws IOException {
        String[] strings = tournament.getLastRound();
        for (String string : strings) {
            objectOutputStream.writeUTF(string);
            objectOutputStream.flush();
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
    }

    private void editUserInfo(String receive) {
        String[] strings = receive.split(" ");
        try {
            login_player.setFirstName(strings[2]);
            login_player.setLastName(strings[3]);
            login_player.setPassword(strings[4]);
            login_player.setEmail(strings[5]);
            login_player.setUrl(strings[6]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tournamentsInformation() throws IOException {
        for (Tournament tournament : tournaments) {
            String temp;
            if (tournament.getTournamentState() != TournamentState.Joining)
                temp = "Tournament closed";
            else if (tournament.isInTournament(login_player))
                temp = "Leave";
            else
                temp = "Join";
            objectOutputStream.writeUTF(tournament + " " + temp);
            objectOutputStream.flush();
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
    }

    private void exist() throws IOException {
        isConnected = false;
        if (isPlaying) {
            currentGameManager.acceptLose(this);
        }
    }

    private void readyToWatchOnGoingGame() throws IOException {
        currentGameManager.updateWatch(this);
    }

    private void watchOnGoingGame(String receive) throws IOException {
        String[] strings = receive.split(" ");
        isWatching = true;
        watch_game = getGameWithId(strings[1]);
        currentGameManager = watch_game.getGameManager();
        currentGameManager.addToAudiences(this);
    }

    private Game getGameWithId(String id) {
        for (Game game : on_going)
            if (game.getId().equals(id))
                return game;
        return null;
    }

    private void requestGamesInformation() throws IOException {
        for (GameRequestInformation gameRequest : gameRequests) {
            if (!gameRequest.getSeekerPlayer().equals(login_player)) {
                objectOutputStream.writeUTF(gameRequest.toString());
                objectOutputStream.flush();
            }
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
        objectOutputStream.writeObject(on_going);
        objectOutputStream.flush();
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
    }

    private void getGameResult(String receive) throws IOException {
        System.out.println("result of game");
        String[] strings = receive.split(" ");
        //login_player.games.get(0)
        String confirmation = objectInputStream.readUTF();
        if (confirmation.equals("ready to receive notations")) {
            System.out.println("ready to receive notations");
            System.out.println("login player moves result\n" + login_player.getGames().get(0).moves);
            for (String move : login_player.getGames().get(0).moves) {
                objectOutputStream.writeUTF(move);
                System.out.println(move);
                objectOutputStream.flush();
            }
            objectOutputStream.writeUTF("over");
            objectOutputStream.flush();
        }
    }

    private void requestNewGame(String receive) {
        String[] strings = receive.split(" ");
        GameRequestInformation temp;
        if (strings.length < 6) {
            temp = new GameRequestInformation(login_player, id, Boolean.parseBoolean(strings[1])
                    , Boolean.parseBoolean(strings[2]), strings[3].equals("Black")
                    ? Color.BLACK : Color.WHITE, requests.size(), null);
        } else {
            temp = new GameRequestInformation(login_player, id, Boolean.parseBoolean(strings[1])
                    , Boolean.parseBoolean(strings[2]), strings[3].equals("Black")
                    ? Color.BLACK : Color.WHITE, requests.size(), strings[4] + " " + strings[5]);
            System.out.println(strings[4] + " " + strings[5]);
        }
        gameRequests.add(temp);
        requests.add(temp);
    }

    private void signUp(String receive) {
        String[] strings = receive.split(" ");
        User player = new User(strings[1], strings[2], strings[3], strings[4]
                , User.makeUserName(users, strings[1], strings[2]), strings[5]);
        users.add(player);
        login_player = player;
    }

    private void executeClientGameInTournamentRequest(String receive) throws IOException {
        String[] strings = receive.split(" ");
        if (strings[4].equals("Join")) {
            if (tournaments.get(Integer.parseInt(strings[3])).addPlayer(login_player)) {
                objectOutputStream.writeUTF("Leave");
                objectOutputStream.flush();
            } else {
                objectOutputStream.writeUTF("Capacity full");
                objectOutputStream.flush();
            }
        }
        if (strings[4].equals("Leave")) {
            if (tournaments.get(Integer.parseInt(strings[3])).removePlayer(login_player)) {
                objectOutputStream.writeUTF("Join");
                objectOutputStream.flush();
            } else {
                objectOutputStream.writeUTF("Can't resign");
                objectOutputStream.flush();
            }
        } else if (strings[4].equals("Tournament")) {
            if (tournaments.get(Integer.parseInt(strings[3])).isInTournament(login_player)) {
                objectOutputStream.writeUTF("load score board");
                tournament = tournaments.get(Integer.parseInt(strings[3]));
            } else {
                objectOutputStream.writeUTF("accesses  denied");
            }
            objectOutputStream.flush();
        }
    }

    private void executeClientIsEditRequest() throws IOException {
        objectOutputStream.writeUTF("no");
        objectOutputStream.flush();
    }

    private void executeClientEditRequest() throws IOException {
        //??? what does this mean
        String line = objectInputStream.readUTF();
        if (line.equals("is edit")) {
            objectOutputStream.writeUTF("yes " + login_player.getFirstName() + " " +
                    login_player.getLastName() + " " +
                    login_player.getEmail() + " "
                    + login_player.getUrl()
            );
            objectOutputStream.flush();
        }
    }

    private void executeClientLoginRequest(String receive) throws IOException {
        boolean find = false;
        String[] split = receive.split(" ");
        for (User player : users) {
            try {
                if (player.getUsername().equals(split[1]) && player.getPassword().equals(split[2])) {
                    objectOutputStream.writeUTF("Player found");
                    login_player = player;
                    isLoggedIn = true;
                    objectOutputStream.flush();
                    find = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!find) {
            objectOutputStream.writeUTF("Player doesn't exist");
            objectOutputStream.flush();
        }
    }

    public User getLogin_player() {
        return login_player;
    }

    public void setCurrentGameManager(GameManager gameManager) {
        this.currentGameManager = gameManager;
    }

    public void notifyStartOfTheGame() {
        try {
            objectOutputStream.writeUTF("game starting");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendColorAndClock(Color color, Clock clock) {
        try {
            objectOutputStream.writeUTF("start");
            objectOutputStream.flush();
            objectOutputStream.writeObject(color);
            objectOutputStream.flush();
            objectOutputStream.writeObject(clock);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this is when opponent has asked for draw
    public void sendDrawRequest() {
        try {
            objectOutputStream.writeUTF("want a draw");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyGameIsOver() {
        try {
            objectOutputStream.writeUTF("over");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyGameLose() {
        try {
            objectOutputStream.writeUTF("finish lose");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyGameWin() {
        try {
            objectOutputStream.writeUTF("finish win");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyGameWatch() {
        try {
            objectOutputStream.writeUTF("watch this");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyDraw() {
        try {
            objectOutputStream.writeUTF("finish draw");
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            objectOutputStream.writeUTF(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPastMovesAndBoard(ArrayList<String> moves, ChessGameLogic board) {
        try {
            for (String move : moves) {
                objectOutputStream.writeUTF(move);
                objectOutputStream.flush();
            }
            objectOutputStream.writeUTF("over");
            objectOutputStream.flush();
            objectOutputStream.writeObject(board);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
