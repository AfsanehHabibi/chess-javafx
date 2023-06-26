package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import sample.Color;

public class QueenView extends ChessManView {
    public QueenView(Color color) {
        super(color);
        if (color == Color.White)
            chess_picture = new Image("7WQueen.png", 40, 40, false, false);
        else
            chess_picture = new Image("7BQueen.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane = new BorderPane(imageview);
    }
}
