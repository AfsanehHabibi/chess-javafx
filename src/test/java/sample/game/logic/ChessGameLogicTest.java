package sample.game.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChessGameLogicTest {

    @Test
    public void whenGivenTooChessGameLogicWithSameBoard_TheyShouldBeEqual() {
        ChessGameLogic gameLogic1 = new ChessGameLogic();
        ChessGameLogic gameLogic2 = new ChessGameLogic();
        gameLogic1.move(1, 0, 2, 0);
        gameLogic1.move(1, 3, 2, 3);

        gameLogic2.move(1, 3, 2, 3);
        gameLogic2.move(1, 0, 2, 0);
        assertEquals(gameLogic1, gameLogic2);
    }


    @Test
    public void whenGivenTooChessGameLogicWithDifferentBoard_TheyShouldNotBeEqual() {
        ChessGameLogic gameLogic1 = new ChessGameLogic();
        ChessGameLogic gameLogic2 = new ChessGameLogic();
        gameLogic1.move(1, 0, 2, 0);

        gameLogic2.move(1, 3, 2, 3);
        assertNotEquals(gameLogic1, gameLogic2);
    }


    @Test
    public void whenDeserializingAChessManTheObjectIsEqualToStartingObject() throws IOException, ClassNotFoundException {
        ChessGameLogic original = new ChessGameLogic();
        original.move(1, 6, 2, 6);
        original.move(6, 4, 5, 4);
        // serialize the object
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(original);
        oos.close();

        // deserialize the object
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        ChessGameLogic deserialized = (ChessGameLogic) ois.readObject();
        ois.close();

        // compare the original object with the deserialized object
        assertEquals(original, deserialized);
    }
}