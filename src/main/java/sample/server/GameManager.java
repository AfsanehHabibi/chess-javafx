package sample.server;

import sample.game.logic.ChessGameLogic;
import sample.game.logic.util.NotationManager;
import sample.game.model.Move;
import sample.model.game.Game;
import sample.model.game.GameMoveRecord;
import sample.model.game.GameRequestInformation;
import sample.model.game.GameResult;
import sample.model.util.Color;
import sample.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static sample.server.Server.*;

public class GameManager {
    Game game;
    ChessGameLogic currentBoard;
    private ClientHandler whiteClient;
    private ClientHandler blackClient;
    private ArrayList<ClientHandler> audiences;
    private boolean isWhiteJoined;
    private boolean isBlackJoined;
    Color turn;

    public GameManager(ClientHandler client1, ClientHandler client2,
                                   GameRequestInformation information) {
        User user1 = client1.getLogin_player();
        User user2 = client2.getLogin_player();
        whiteClient = null;
        blackClient = null;
        if (Objects.equals(information.getSeekerPlayer(), user1)) {
            if (information.getColor() == Color.BLACK) {
                blackClient = client1;
                whiteClient = client2;
            } else {
                blackClient = client2;
                whiteClient = client1;
            }
        } else if (Objects.equals(information.getSeekerPlayer(), user2)) {
            if (information.getColor() == Color.BLACK) {
                blackClient = client2;
                whiteClient = client1;
            } else {
                blackClient = client1;
                whiteClient = client2;
            }
        }
        whiteClient.isPlaying = true;
        blackClient.isPlaying = true;
        //opponent = clients.get(Integer.parseInt(strings[2]));
        game = new Game(information, whiteClient.getLogin_player(), blackClient.getLogin_player());
        game.setGameManager(this);
        currentBoard = new ChessGameLogic();
        on_going.add(game);
        gameRequests.removeAll(client1.requests);
        gameRequests.removeAll(client2.requests);
        client1.requests.clear();
        client2.requests.clear();
        client1.setCurrentGameAndChatManager(this);
        client2.setCurrentGameAndChatManager(this);
        notifyStartOfTheGame();
        turn = Color.WHITE;
        audiences = new ArrayList<>();
    }

    private void notifyStartOfTheGame() {
        whiteClient.notifyStartOfTheGame();
        blackClient.notifyStartOfTheGame();
    }

    public void readyToStart(ClientHandler client) {
        if (client == blackClient)
            isBlackJoined = true;
        if (client == whiteClient)
            isWhiteJoined = true;
        if (isBlackJoined && isWhiteJoined) {
            whiteClient.sendColorAndClock(Color.WHITE, game.getClock());
            blackClient.sendColorAndClock(Color.BLACK, game.getClock());
            notifyPlayerToMove(currentBoard.clone());
        }
    }

    public void move(ClientHandler client, Move move) {
        if (!((turn == Color.BLACK && client == blackClient)
                || (turn == Color.WHITE && client == whiteClient)))
            return;
        if (currentBoard.canMove(move)) {
            ChessGameLogic beforeMoveBoard = currentBoard.clone();
            currentBoard.move(move);

            //!!! do not change this, sending the object without cloning it leads to unexpected
            // results
            ChessGameLogic afterMoveBoard = currentBoard.clone();
            String notation = new NotationManager().getNotation(move, beforeMoveBoard, afterMoveBoard);
            GameMoveRecord moveRecord = new GameMoveRecord(notation, beforeMoveBoard, afterMoveBoard, move);
            game.moves.add(moveRecord);
            whiteClient.sendNewGameMove(moveRecord);
            blackClient.sendNewGameMove(moveRecord);

            for (ClientHandler audience : audiences) {
                audience.sendNewGameMove(moveRecord);
            }
            updateTurn();

            notifyPlayerToMove(afterMoveBoard);
        }
    }

    private void notifyPlayerToMove(ChessGameLogic lastVersion) {
        if (turn == Color.BLACK)
            blackClient.notifyToMove(lastVersion);
        else
            whiteClient.notifyToMove(lastVersion);
    }

    private void updateTurn() {
        turn = turn == Color.BLACK ? Color.WHITE :Color.BLACK;
    }

    public void requestDraw(ClientHandler client) {
        if (isNotPlayer(client)) return;
        getOpponent(client).sendDrawRequest();
    }

    protected ClientHandler getOpponent(ClientHandler client) {
        if (client == blackClient)
            return whiteClient;
        if (client == whiteClient)
            return blackClient;
        return null;
    }

    public void acceptLose(ClientHandler client) {
        if (isNotPlayer(client)) return;
        if (client != blackClient && client != whiteClient)
            return;
        /*if (in_tournament) {
            tournamentGame.setWinner(opponent.login_player);
            in_tournament = false;
            opponent.in_tournament = false;
        }*/
        GameResult result = GameResult.WHITE_WON;
        if (client == whiteClient)
            result = GameResult.WHITE_LOST;
        doEndGameStuff(result);
        if (game.isRated())
            User.calculateRatings(false, getOpponent(client).getLogin_player(), client.getLogin_player());
        client.notifyGameLose();
        getOpponent(client).notifyGameWin();
    }

    public void addToAudiences(ClientHandler client) {
        if (!audiences.contains(client)) {
            audiences.add(client);
            client.notifyGameWatch();
        }
    }

    public void acceptDraw(ClientHandler client) {
        if (isNotPlayer(client)) return;
        /*if (in_tournament) {
            tournamentGame.setWinner(null);
            in_tournament = false;
            opponent.in_tournament = false;
        }*/
        doEndGameStuff(GameResult.DRAW);
        if (game.isRated())
            User.calculateRatings(true, getOpponent(client).getLogin_player(), client.getLogin_player());
        client.notifyDraw();
        getOpponent(client).notifyDraw();
    }

    //rename this
    private void doEndGameStuff(GameResult result) {
        for (ClientHandler audience : audiences)
            audience.notifyGameIsOver();
        game.setResult(result);
        on_going.remove(game);
        whiteClient.getLogin_player().addGame(game);
        blackClient.getLogin_player().addGame(game);
        blackClient.isPlaying = false;
        whiteClient.isPlaying = false;
    }

    protected boolean isNotPlayer(ClientHandler client) {
        return (client != blackClient && client != whiteClient);
    }

    public void updateWatch(ClientHandler client) {
        if (isNotInAudiences(client)) return;
        client.sendPastMovesAndBoard(game.moves);
    }

    protected boolean isNotInAudiences(ClientHandler client) {
        return !audiences.contains(client);
    }

    protected List<ClientHandler> getAudiences() {
        return audiences;
    }

    protected List<ClientHandler> getPlayers() {
        ArrayList<ClientHandler> players = new ArrayList<>();
        players.add(blackClient);
        players.add(whiteClient);
        return players;
    }
}
