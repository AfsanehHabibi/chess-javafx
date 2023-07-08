package sample.game.view;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sample.model.chat.Message;
import sample.model.chat.MessageRepresentation;
import sample.model.chat.MessageTarget;

public class ChatIOController {
    ChoiceBox<String> chatReceiver;
    TextField messageHolder;
    Label chatContent;
    Button sendButton;
    Button closeButton;
    MessageRepresentation messageRepresentation;

    public ChatIOController(ChoiceBox<String> chatReceiver, TextField messageHolder,
                            Label chatContent, Button sendButton,
                            Button closeButton, boolean isWatching) {
        this.chatReceiver = chatReceiver;
        this.messageHolder = messageHolder;
        this.chatContent = chatContent;
        this.sendButton = sendButton;
        this.closeButton = closeButton;
        chatReceiver.getItems().add("Group");
        if (!isWatching) {
            chatReceiver.getItems().add("Opponent");
            chatReceiver.setValue("Opponent");
        } else {
            chatReceiver.setValue("Group");
        }
        messageRepresentation = new MessageRepresentation();
    }

    public void addNewMessage(Message message) {
        MessageRepresentation r = new MessageRepresentation();
        System.out.println(r.stringifyMessage(message));
        chatContent.setText(chatContent.getText() + "\n" +
                messageRepresentation.stringifyMessage(message));
    }

    public Message getMessageToBeSendAndClear() {
        String value = messageHolder.getText();
        messageHolder.clear();
        MessageTarget target = MessageTarget.GROUP;
        if (chatReceiver != null && chatReceiver.getValue().equals("Opponent"))
            target = MessageTarget.OPPONENT;
        return new Message(value, target);
    }

    public void closeChat() {
        chatContent.setVisible(false);
        sendButton.setVisible(false);
        closeButton.setVisible(false);
        messageHolder.setVisible(false);
        chatReceiver.setVisible(false);
    }
}
