package sample.game.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.game.Chess;

import java.util.List;

public class NotationBoardIOController {
    private final TableView<String> notationTable;
    private TableColumn<String, String> notationColumn;

    public NotationBoardIOController(TableView<String> notationTable,
                                     TableColumn<String, String> notationColumn) {
        this.notationTable = notationTable;
        this.notationColumn = notationColumn;
        notationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
    }

    public void addAll(List<String> inputs) {
        notationTable.getItems().addAll(inputs);
    }

    public void addToBoard(String notation) {
        notationTable.getItems().add(notation);
    }

    public void setNotationClicks(Chess chess) {
        notationTable.setOnMouseClicked((E) -> {
            if (notationTable.getSelectionModel().getSelectedItem() != null) {
                chess.notifyClickOnNotation(notationTable.getSelectionModel().getSelectedIndex());
                System.out.println(notationTable.getSelectionModel().getSelectedIndex());
            }
        });
    }
}
