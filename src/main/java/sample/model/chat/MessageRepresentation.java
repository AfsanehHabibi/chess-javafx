package sample.model.chat;

public class MessageRepresentation {
    public String stringifyMessage(Message message) {
        if (message.sender == null || message.sender.getUsername() == null)
            return "unknown: " + message.text;
        else
            return message.sender.getUsername() + ": " + message.text;
    }
}
