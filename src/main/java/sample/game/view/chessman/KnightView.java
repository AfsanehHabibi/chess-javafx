package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import sample.model.util.Color;

public class KnightView extends ChessManView {
    public KnightView(Color color) {
        super(color);
        if (color == Color.WHITE)
            chess_picture = new Image("/image/chessman/7WKnight.png", 40, 40, false, false);
        else
            chess_picture = new Image("/image/chessman/7BKnight.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        borderPane = new BorderPane(imageview);
    }
}
