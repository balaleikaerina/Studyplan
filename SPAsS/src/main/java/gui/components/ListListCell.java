package gui.components;

import app.module.Modul;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Custom ListCell for the ModulListView.
 * @param <T>
 */

public class ListListCell<T> extends ListCell<SemesterListView<Modul>> {
    private Label semZahl;
    private SemesterListView<Modul> semListView;
    private Pane view;
    HBox hBox;

    public ListListCell() {
        hBox = new HBox();
        semZahl = new Label();

        hBox.getChildren().addAll(semZahl);

        view = new HBox();
        view.getChildren().add(hBox);
        this.setGraphic(view);

    }

    @Override
    protected void updateItem(SemesterListView<Modul> semListView, boolean empty) {
        super.updateItem(semListView, empty);
        if(semListView != null) {
            semZahl.setText(Integer.toString(semListView.getSemesterzahl()));

            //hBox.getChildren().add(semListView);
            setGraphic(view);
        } else {

            setGraphic(null);
        }
    }
}
