package sample.game.logic.exception;

public class BoardWrongSizeException extends Exception {
    public BoardWrongSizeException() {
        super("Invalid board size");
    }
}
