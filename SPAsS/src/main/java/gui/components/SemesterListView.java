package gui.components;

import app.module.Modul;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;

/**
 * Customized JavaFX ListView für die Semester
 * @param <T>
 */
public class SemesterListView<T> extends ListView<T> {

    int semesterzahl;

    public SemesterListView(int semesterZahl) {
        this.semesterzahl = semesterZahl;
        this.setOrientation(Orientation.HORIZONTAL);
        this.setPrefHeight(100);
        getStyleClass().add("semester-list-view");

    }

    public int getSemesterzahl() {
        return semesterzahl;
    }
}
