package sample.game.view.chessman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import sample.Color;

public abstract class ChessManView {
    public BorderPane borderPane = new BorderPane();
    Image chess_picture;
    ImageView imageview = new ImageView();
    Color color;

    public ChessManView(Color color) {
        this.color = color;
    }
}
