package sample.game.view.chessman;

import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import sample.model.util.Color;

public class EmptyView extends ChessManView {
    public EmptyView(Color color) {
        super(color);
        imageview = new ImageView();
        borderPane = new BorderPane(imageview);
    }
}
