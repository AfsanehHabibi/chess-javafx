package sample.game.model;

import java.io.Serializable;

public class Move implements Serializable {
    public int iSrc;
    public int jSrc;
    public int iDes;
    public int jDes;

    public Move(int iSrc, int jSrc, int iDes, int jDes) {
        this.iSrc = iSrc;
        this.jSrc = jSrc;
        this.iDes = iDes;
        this.jDes = jDes;
    }
}
