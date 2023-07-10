package sample.game.logic.chessman;

import sample.game.model.Move;
import sample.model.util.Color;
import sample.game.logic.ChessGameLogic;

public class Empty extends ChessManClass {
    public Empty() {
        color = null;
    }

    @Override
    public ChessManClass clone() {
        return new Empty();
    }

    @Override
    public boolean canMoveNormal(Move move, ChessGameLogic game) {
        return false;
    }

    @Override
    public String getChessPieceName() {
        return "";
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public char getCharName() {
        return 'E';
    }
}
