package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import sample.model.util.Color;

public class BishopView extends ChessManView {
    public BishopView(Color color) {
        super(color);
        if (color == Color.WHITE)
            chess_picture = new Image("/image/chessman/7WBishop.png", 40, 40, false, false);
        else
            chess_picture = new Image("/image/chessman/7BBishop.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        //imageview.autosize();
        borderPane = new BorderPane(imageview);
    }
}
