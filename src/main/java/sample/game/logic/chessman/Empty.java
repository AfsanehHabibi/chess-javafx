package sample.game.logic.chessman;

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
    public boolean canMoveNormal(int i_src, int j_src, int i_dest, int j_dest, ChessGameLogic game) {
        return false;
    }

    @Override
    public char getChessPieceName() {
        return 0;
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
