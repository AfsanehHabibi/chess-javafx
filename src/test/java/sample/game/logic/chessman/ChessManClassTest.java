package sample.game.logic.chessman;

import org.junit.Test;
import sample.Color;

import java.io.*;

import static org.junit.Assert.*;

public class ChessManClassTest {
    @Test
    public void givenTwoDifferentChessPieceWithSameColor_TheyAreNotEqual() {
        ChessManClass bishop = new Bishop(Color.Black);
        ChessManClass queen = new Queen(Color.Black);
        assertNotEquals(bishop, queen);
        ChessManClass rook = new Bishop(Color.White);
        ChessManClass pawn = new Queen(Color.White);
        assertNotEquals(rook, pawn);
    }

    @Test
    public void givenSameChessPieceWithSameColor_TheyAreEqual() {
        ChessManClass bishop = new Bishop(Color.Black);
        ChessManClass bishop1 = new Bishop(Color.Black);
        assertEquals(bishop, bishop1);
        ChessManClass knight = new Knight(Color.White);
        ChessManClass knight1 = new Knight(Color.White);
        assertEquals(knight, knight1);
    }

    @Test
    public void givenSameChessPieceWithDifferentColor_TheyAreNotEqual() {
        ChessManClass king = new King(Color.White);
        ChessManClass king1 = new King(Color.Black);
        assertNotEquals(king, king1);
        ChessManClass pawn = new Pawn(Color.White);
        ChessManClass pawn1 = new Pawn(Color.Black);
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
        ChessManClass original = new Bishop(Color.Black);
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