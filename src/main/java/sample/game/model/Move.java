package sample.game.model;

import java.io.Serializable;

public class Move implements Serializable {
    private final int iSrc;
    private final int jSrc;
    private final int iDes;
    private final int jDes;

    public Move(int iSrc, int jSrc, int iDes, int jDes) {
        this.iSrc = iSrc;
        this.jSrc = jSrc;
        this.iDes = iDes;
        this.jDes = jDes;
    }

    public int getISrc() {
        return iSrc;
    }

    public int getJSrc() {
        return jSrc;
    }

    public int getIDes() {
        return iDes;
    }

    public int getJDes() {
        return jDes;
    }
}
