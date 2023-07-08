package sample.game.logic.chessman;

import org.junit.Test;
import sample.model.util.Color;

import java.io.*;

import static org.junit.Assert.*;

public class ChessManClassTest {
    @Test
    public void givenTwoDifferentChessPieceWithSameColor_TheyAreNotEqual() {
        ChessManClass bishop = new Bishop(Color.BLACK);
        ChessManClass queen = new Queen(Color.BLACK);
        assertNotEquals(bishop, queen);
        ChessManClass rook = new Bishop(Color.WHITE);
        ChessManClass pawn = new Queen(Color.WHITE);
        assertNotEquals(rook, pawn);
    }

    @Test
    public void givenSameChessPieceWithSameColor_TheyAreEqual() {
        ChessManClass bishop = new Bishop(Color.BLACK);
        ChessManClass bishop1 = new Bishop(Color.BLACK);
        assertEquals(bishop, bishop1);
        ChessManClass knight = new Knight(Color.WHITE);
        ChessManClass knight1 = new Knight(Color.WHITE);
        assertEquals(knight, knight1);
    }

    @Test
    public void givenSameChessPieceWithDifferentColor_TheyAreNotEqual() {
        ChessManClass king = new King(Color.WHITE);
        ChessManClass king1 = new King(Color.BLACK);
        assertNotEquals(king, king1);
        ChessManClass pawn = new Pawn(Color.WHITE);
        ChessManClass pawn1 = new Pawn(Color.BLACK);
        assertNotEquals(pawn, pawn1);
    }

    @Test
    public void givenTowEmptyChessPiece_TheyAreEqual() {
        ChessManClass empty = new Empty();
        ChessManClass empty1 = new Empty();
        assertEquals(empty, empty1);
    }

    @Test
    public void whenDeserializingAChessManTheObjectIsEqualToStartingObject() throws IOException, ClassNotFoundException {
        ChessManClass original = new Bishop(Color.BLACK);
        // serialize the object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // deserialize the object
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        ChessManClass deserialized = (ChessManClass) ois.readObject();
        ois.close();

        // compare the original object with the deserialized object
        assertEquals(original, deserialized);
    }
}