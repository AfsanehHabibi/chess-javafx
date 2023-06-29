package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import sample.Color;

public class RookView extends ChessManView {
    public RookView(Color color) {
        super(color);
        if (color == Color.White)
            chess_picture = new Image("/image/chessman/7WRook.png", 40, 40, false, false);
        else
            chess_picture = new Image("/image/chessman/7BRook.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane = new BorderPane(imageview);
    }
}
