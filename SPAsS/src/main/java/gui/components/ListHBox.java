package gui.components;

import app.module.Modul;
import javafx.event.EventHandler;
import domain.main.ModulListeDomain;
import exceptions.ModulExistiertNichtException;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class ListHBox extends HBox {

    public SemesterListView<Modul> getSemListView() {
        return semListView;
    }

    private SemesterListView<Modul> semListView;


    private ModulListeDomain modulListeDomain;
    private int semesterZahl;

    /**
     * Customized ListHBox in der die einzelnen Semester angezeigt werden.
     * @param semesterZahl
     */

    public ListHBox(int semesterZahl, ModulListeDomain modulListeDomain) {
        this.semesterZahl = semesterZahl;
        this.modulListeDomain = modulListeDomain;
        Label semZahl = new Label(semesterZahl + "");
        semZahl.setAlignment(Pos.BASELINE_CENTER);
        semZahl.getStyleClass().add("semester-zahl");


        BorderPane semZahlBackground = new BorderPane();
        semZahlBackground.setPrefWidth(80);
        semZahlBackground.setPrefHeight(50);
        semZahlBackground.getStyleClass().add("list-hbox-background");

        semZahlBackground.setCenter(semZahl);

        semListView = new SemesterListView<>(semesterZahl);
        semListView.setCellFactory((e) -> new ModulListCell());

        setEffect(new DropShadow(4, 2, 2, new Color(0, 0, 0, 0.1)));
        setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
        setHgrow(semListView, Priority.ALWAYS);

        getChildren().addAll(semZahlBackground, semListView);


    }

    /**
     * Fügt ein Modul hinzu (Wird als Event registriert)
     * @param modulNummer ModulNummer um das Modul zu finden.
     */

    public void addModuleToView(int modulNummer) {
        try {
            Modul modulToAdd = modulListeDomain.getModulFromList(modulNummer);
            semListView.getItems().add(0, modulToAdd);
        } catch (ModulExistiertNichtException e) {
            e.printStackTrace();
        }
    }

    public void removeModuleFromView(Modul modul) {
        semListView.getItems().remove(modul);
    }

    public int getSemesterZahl() {
        return semesterZahl;
    }
}


