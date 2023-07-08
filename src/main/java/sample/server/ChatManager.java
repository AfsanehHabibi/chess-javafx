package sample.server;

import sample.model.chat.Message;
import sample.model.chat.MessageTarget;

public class ChatManager {
    GameManager gameManager;

    public ChatManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void messageOpponent(ClientHandler client, Message message) {
        if (gameManager.isNotPlayer(client)) return;
        gameManager.getOpponent(client).sendMessage(message);
    }

    public void messageAudiences(ClientHandler client, Message message) {
        if (gameManager.isNotInAudiences(client) && gameManager.isNotPlayer(client)) return;
        for (ClientHandler audience : gameManager.getAudiences())
            if (audience != client) audience.sendMessage(message);
        if (gameManager.isNotPlayer(client)) {
            for (ClientHandler player : gameManager.getPlayers())
                player.sendMessage(message);
        } else {
            gameManager.getOpponent(client).sendMessage(message);
        }
    }

    public void message(ClientHandler client, Message message) {
        if (message.getTarget() == MessageTarget.OPPONENT)
            messageOpponent(client, message);
        if (message.getTarget() == MessageTarget.GROUP)
            messageAudiences(client, message);
    }
}
