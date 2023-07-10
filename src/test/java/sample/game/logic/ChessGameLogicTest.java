package sample.game.logic;

import org.junit.Test;
import sample.game.model.Move;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChessGameLogicTest {

    @Test
    public void whenGivenTooChessGameLogicWithSameBoard_TheyShouldBeEqual() {
        ChessGameLogic gameLogic1 = new ChessGameLogic();
        ChessGameLogic gameLogic2 = new ChessGameLogic();
        Move move1 = new Move(1, 0, 2, 0);
        Move move2 = new Move(1, 3, 2, 3);
        gameLogic1.move(move1);
        gameLogic1.move(move2);

        gameLogic2.move(move2);
        gameLogic2.move(move1);
        assertEquals(gameLogic1, gameLogic2);
    }


    @Test
    public void whenGivenTooChessGameLogicWithDifferentBoard_TheyShouldNotBeEqual() {
        ChessGameLogic gameLogic1 = new ChessGameLogic();
        ChessGameLogic gameLogic2 = new ChessGameLogic();
        gameLogic1.move(new Move(1, 0, 2, 0));

        gameLogic2.move(new Move(1, 3, 2, 3));
        assertNotEquals(gameLogic1, gameLogic2);
    }


    @Test
    public void whenDeserializingAChessManTheObjectIsEqualToStartingObject() throws IOException, ClassNotFoundException {
        ChessGameLogic original = new ChessGameLogic();
        original.move(new Move(1, 6, 2, 6));
        original.move(new Move(6, 4, 5, 4));
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