package sample;

import sample.game.logic.ChessGameLogic;
import sample.game.model.Move;
import sample.tournament.Tournament;
import sample.tournament.TournamentGame;
import sample.tournament.TournamentState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import static sample.Server.*;

public class ClientHandler implements Runnable {
    Socket socket;
    User login_player;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    int id;
    boolean in_tournament = false;
    Boolean isLoggedIn;
    Boolean isConnected;
    boolean isWatching;
    Game watch_game;
    boolean turn = false;
    Boolean isPlaying;
    Tournament tournament;
    ArrayList<GameRequestInformation> requests;
    ClientHandler opponent;
    Game current_game;
    TournamentGame tournamentGame;
    ArrayList<ClientHandler> audiences = new ArrayList<>();
    boolean join_game;
    ChessGameLogic currentGameLogic;

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
            join_game = true;
            if (opponent.join_game) {
                if (opponent.current_game.color == Color.White) {
                    opponent.turn = true;
                } else if (this.current_game.color == Color.White) {
                    turn = true;
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
        } else if (turn) {
            Move move = null;
            System.out.println(login_player.username);
            try {
                move = (Move) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (currentGameLogic.canMove(move.iSrc, move.jSrc, move.iDes, move.jDes)) {
                currentGameLogic.move(move.iSrc, move.jSrc, move.iDes, move.jDes);

                current_game.moves.add(receive);
                opponent.current_game.moves.add(receive);

                //!!! do not change this, sending the object without cloning it leads to unexpected
                // results
                ChessGameLogic lastVersion = currentGameLogic.clone();
                sendNewGameMove(receive, lastVersion, objectOutputStream);
                sendNewGameMove(receive, lastVersion, opponent.objectOutputStream);

                for (ClientHandler audience : audiences) {
                    sendNewGameMove(receive, lastVersion, audience.objectOutputStream);
                }

                updateTurn();

                notifyPlayerToMove(lastVersion);
            }
        }
    }

    private void notifyPlayerToMove(ChessGameLogic lastVersion) throws IOException {
        opponent.objectOutputStream.writeUTF("allow to move");
        opponent.objectOutputStream.flush();
        opponent.objectOutputStream.writeObject(lastVersion);
        opponent.objectOutputStream.flush();
    }

    private void updateTurn() {
        opponent.turn = true;
        turn = false;
    }

    private void sendNewGameMove(String notation, ChessGameLogic board, ObjectOutputStream outputStream)
            throws IOException {
        outputStream.writeUTF("new move");
        outputStream.flush();
        outputStream.writeObject(board);
        outputStream.flush();
        outputStream.writeUTF(notation);
        outputStream.flush();
    }

    private void playerHasLost() throws IOException {
        if (in_tournament) {
            tournamentGame.setWinner(opponent.login_player);
            in_tournament = false;
            opponent.in_tournament = false;
        }
        for (ClientHandler audience : audiences) {
            audience.objectOutputStream.writeUTF("over");
            audience.objectOutputStream.flush();
        }
        on_going.remove(current_game);
        current_game.setResult(GameResult.Lose);
        opponent.current_game.setResult(GameResult.Win);
        login_player.addGame(current_game);
        opponent.login_player.addGame(opponent.current_game);
        System.out.println("login player moves\n" + login_player.games.get(0).moves);
        if (current_game.isRated)
            User.calculateRatings(false, opponent.login_player, login_player);
        objectOutputStream.writeUTF("finish lose");
        objectOutputStream.flush();
        isPlaying = false;
        opponent.isPlaying = false;
        opponent.objectOutputStream.writeUTF("finish win");
        opponent.objectOutputStream.flush();
    }

    private void requestDraw() throws IOException {
        opponent.objectOutputStream.writeUTF("want a draw");
        opponent.objectOutputStream.flush();
    }

    private void requestGameHistory() throws IOException {
        for (int i = 0; i < login_player.games.size(); i++) {
            objectOutputStream.writeUTF(login_player.games.get(i).toString());
            objectOutputStream.flush();
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
    }

    private void chat(String receive) throws IOException {
        if (receive.startsWith("chat op")) {
            opponent.objectOutputStream.writeUTF(receive);
            opponent.objectOutputStream.flush();
        } else {
            for (ClientHandler clientHandler : audiences) {
                clientHandler.objectOutputStream.writeUTF(receive);
                clientHandler.objectOutputStream.flush();
            }
        }
    }

    private void acceptDraw() throws IOException {
        if (in_tournament) {
            tournamentGame.setWinner(null);
            in_tournament = false;
            opponent.in_tournament = false;
        }
        for (ClientHandler audience : audiences) {
            audience.objectOutputStream.writeUTF("over");
            audience.objectOutputStream.flush();
        }
        on_going.remove(current_game);
        current_game.setResult(GameResult.Draw);
        opponent.current_game.setResult(GameResult.Draw);
        login_player.addGame(current_game);
        opponent.login_player.addGame(opponent.current_game);
        System.out.println("login player moves\n" + login_player.games.get(0).moves);
        if (current_game.isRated)
            User.calculateRatings(true, opponent.login_player, login_player);
        isPlaying = false;
        opponent.isPlaying = false;
        objectOutputStream.writeUTF("finish draw");
        objectOutputStream.flush();
        opponent.objectOutputStream.writeUTF("finish draw");
        opponent.objectOutputStream.flush();
    }

    private void acceptGameRequest(String receive) throws IOException {
        String[] strings = receive.split(" ");
        isPlaying = true;
        opponent = clients.get(Integer.parseInt(strings[2]));
        opponent.opponent = this;
        current_game = new Game(opponent.requests.get(Integer.parseInt(strings[3])), opponent.login_player);
        opponent.current_game = new Game(opponent.requests.get(Integer.parseInt(strings[3])), this.login_player);
        currentGameLogic = new ChessGameLogic();
        opponent.currentGameLogic = currentGameLogic;
        on_going.add(current_game);
        turn = false;
        if (opponent.requests.get(Integer.parseInt(strings[3])).color == Color.Black) {
            opponent.current_game.setColor(Color.Black);
            this.current_game.setColor(Color.White);
        } else {
            opponent.current_game.setColor(Color.White);
            this.current_game.setColor(Color.Black);
        }
        gameRequests.removeAll(opponent.requests);
        gameRequests.removeAll(this.requests);
        opponent.requests.clear();
        this.requests.clear();
        opponent.isPlaying = true;
        System.out.println(Integer.parseInt(strings[2]));
        clients.get(Integer.parseInt(strings[2])).objectOutputStream.writeUTF("game starting");
        clients.get(Integer.parseInt(strings[2])).objectOutputStream.flush();
        objectOutputStream.writeUTF("game starting");
        objectOutputStream.flush();
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
            login_player.setFirst_name(strings[2]);
            login_player.setLast_name(strings[3]);
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
            for (ClientHandler audience : audiences) {
                audience.objectOutputStream.writeUTF("over");
                audience.objectOutputStream.flush();
            }
            if (in_tournament) {
                tournamentGame.setWinner(opponent.login_player);
                in_tournament = false;
                opponent.in_tournament = false;
            }
            current_game.setResult(GameResult.Lose);
            opponent.current_game.setResult(GameResult.Win);
            login_player.addGame(current_game);
            opponent.login_player.addGame(opponent.current_game);
            if (current_game.isRated)
                User.calculateRatings(false, opponent.login_player, login_player);
            isPlaying = false;
            opponent.isPlaying = false;
            opponent.objectOutputStream.writeUTF("finish win");
            opponent.objectOutputStream.flush();
        }
    }

    private void readyToWatchOnGoingGame() throws IOException {
        for (int i = 0; i < watch_game.moves.size(); i++) {
            objectOutputStream.writeUTF(watch_game.moves.get(i));
            objectOutputStream.flush();
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
    }

    private void watchOnGoingGame(String receive) throws IOException {
        String[] strings = receive.split(" ");
        isWatching = true;
        watch_game = on_going.get(Integer.parseInt(strings[1]));
        Objects.requireNonNull(findPlayer(watch_game.opponent)).audiences.add(this);
        Objects.requireNonNull(findPlayer(watch_game.opponent).opponent).audiences.add(this);
        objectOutputStream.writeUTF("watch this");
        objectOutputStream.flush();
    }

    private void requestGamesInformation() throws IOException {
        for (GameRequestInformation gameRequest : gameRequests) {
            if (!gameRequest.getSeek_player().equals(login_player)) {
                objectOutputStream.writeUTF(gameRequest.toString());
                objectOutputStream.flush();
            }
        }
        objectOutputStream.writeUTF("over");
        objectOutputStream.flush();
        for (int i = 0; i < on_going.size(); i++) {
            objectOutputStream.writeUTF(on_going.get(i).toString() + " " + i);
            objectOutputStream.flush();
        }
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
            System.out.println("login player moves result\n" + login_player.games.get(0).moves);
            for (String move : login_player.games.get(0).moves) {
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
                    ? Color.Black : Color.White, requests.size(), null);
        } else {
            temp = new GameRequestInformation(login_player, id, Boolean.parseBoolean(strings[1])
                    , Boolean.parseBoolean(strings[2]), strings[3].equals("Black")
                    ? Color.Black : Color.White, requests.size(), strings[4] + " " + strings[5]);
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
            objectOutputStream.writeUTF("yes " + login_player.getFirst_name() + " " +
                    login_player.getLast_name() + " " +
                    login_player.email + " "
                    + login_player.url
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
}
