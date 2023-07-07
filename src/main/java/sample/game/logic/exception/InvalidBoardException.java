package sample.game.logic.exception;

public class InvalidBoardException extends Exception {
    public InvalidBoardException() {
        super("Invalid board");
    }
}
