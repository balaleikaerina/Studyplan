package gui.components;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Customized JavaFX VBox (Die SettingsForm)
 */
public class SettingsBox extends VBox {

    public SettingsBox(String STUD_NAME, int MAX_CP) {
        getStylesheets().add("libs/css/application.css");

        setPadding(new javafx.geometry.Insets(30, 30, 30, 30));
        setMinWidth(540);

        Label setName = new Label("Dein Name");

        TextField setNameInput = new TextField();

        setNameInput.getStyleClass().add("inputFeld");

        setNameInput.setText(STUD_NAME);

        Label setCP = new Label("Maximal belegbare CP pro Semester");
        setName.getStyleClass().add("settingsLabel");
        setCP.getStyleClass().add("settingsLabel");
        TextField setCPInput = new TextField();
        setCPInput.setText(String.valueOf(MAX_CP));
        setCPInput.getStyleClass().add("inputFeld");

        getChildren().addAll(setName, setNameInput, setCP, setCPInput);
    }
}
