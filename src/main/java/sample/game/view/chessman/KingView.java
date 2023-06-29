package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import sample.Color;

public class KingView extends ChessManView {
    public KingView(Color color) {
        super(color);
        if (color == Color.White)
            chess_picture = new Image("/image/chessman/7WKing.png", 35, 35, false, false);
        else
            chess_picture = new Image("/image/chessman/7BKing.png", 35, 35, false, false);
        imageview.setImage(chess_picture);
        borderPane = new BorderPane(imageview);
    }
}
