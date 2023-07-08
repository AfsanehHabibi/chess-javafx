package sample.model.chat;

import sample.user.User;

import java.io.Serializable;

public class Message implements Serializable {
    String text;
    User sender;
    User receiver;
    MessageTarget target;

    public Message(String text, User sender) {
        this.text = text;
        this.sender = sender;
    }

    public Message(String text, MessageTarget target) {
        this.text = text;
        this.target = target;
    }

    public MessageTarget getTarget() {
        return target;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
