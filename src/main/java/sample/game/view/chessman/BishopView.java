package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import sample.Color;

public class BishopView extends ChessManView {
    public BishopView(Color color) {
        super(color);
        if (color == Color.White)
            chess_picture = new Image("7WBishop.png", 40, 40, false, false);
        else
            chess_picture = new Image("7BBishop.png", 40, 40, false, false);
        imageview.setImage(chess_picture);
        //imageview.autosize();
        borderPane = new BorderPane(imageview);
    }
}
