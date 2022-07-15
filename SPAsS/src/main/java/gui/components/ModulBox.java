package gui.components;


import app.module.Modul;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;


/**
 * Customized Pane, die ein Modul als viereckige Box anzeigt.
 * Enthält alle Modul-Informationen, sowie bestanden, Note und Löschen.
 */

public class ModulBox extends Pane implements PropertyChangeListener {

    public static final DataFormat modulBoxFormat = new DataFormat("application.Modul");
    final int maxWidth = 550;
    final int minWidth = 1;
    int modulNummer;
    CheckBox bestandenCheckBox;
    Modul modul;
    Label modulName;
    Label cpLabel;
    Label noteLabel;

    public ModulBox(String namen, int cp, int modulNummer, List<Integer> abhaengigkeiten, Boolean bestanden, Modul modul) {
        VBox modulVBox = new VBox();
        HBox subBox = new HBox();
        this.modul = modul;

        String modulNummerAsString = Integer.toString(modulNummer);
        char chatAt1 = modulNummerAsString.charAt(1);
        modul.addPropertyChangeListener(this);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Entfernen");

        contextMenu.getItems().add(menuItem);
        modulVBox.setOnContextMenuRequested(event -> {
            contextMenu.show(modulVBox, event.getScreenX(), event.getScreenY());

            contextMenu.getItems().get(0).setOnAction(event1 -> {
                //löschen des Moduls aus der Liste
                System.out.println("Trying to remove from listview|| Modul: " + modul);
                modul.pseudoDelete();

            });

        });

        switch (chatAt1) {
            case '1':
                getStyleClass().add("modulBox1");
                break;
            case '2':
                getStyleClass().add("modulBox2");
                break;
            case '3':
                getStyleClass().add("modulBox3");
                break;
            case '4':
                getStyleClass().add("modulBox4");
                break;
        }


        this.modulNummer = modulNummer;
        modulName = new Label(namen);
        modulName.setMaxWidth(200);
        modulName.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        modulName.getStyleClass().add("modulBoxH1");
        modulName.setOpacity(1);
        
        cpLabel = new Label(cp + " CP");
        cpLabel.setMaxWidth(200);
        cpLabel.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        cpLabel.getStyleClass().add("modulBoxH1");

        
        noteLabel = new Label(String.valueOf(modul.getNote()));
        noteLabel.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        noteLabel.getStyleClass().add("modulBoxH1");

        bestandenCheckBox = new CheckBox();
        bestandenCheckBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        bestandenCheckBox.getStyleClass().add("checkbox");
        bestandenCheckBox.setSelected(bestanden);

        int width = (int) ((maxWidth-minWidth) * (cp / 15.0)) + minWidth;
        setPrefWidth(width);

        subBox.getChildren().addAll(cpLabel, noteLabel, bestandenCheckBox);
        modulVBox.getChildren().addAll(modulName, subBox);

        getChildren().addAll(modulVBox);

        /* TODO Abhängigkeiten

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Label test = new Label(abhaengigkeiten.toString());
                test.getStyleClass().add("modulBoxH1");
                modulVBox.getChildren().add(test);

            }
        });

        */
        bestandenCheckBox.setOnMouseClicked((event) -> {
            modul.setBestanden(!modul.isBestanden());
        });




    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "x":
                setLayoutX((int) evt.getNewValue());
                break;
            case "y":
                setLayoutY((int) evt.getNewValue());
                break;
            case Modul.BESTANDEN_CHANGE_EVENT:
                Boolean changed = (Boolean) evt.getNewValue();
                bestandenCheckBox.selectedProperty().setValue(changed);
                break;
            case Modul.NAME_CHANGE_EVENT:
                modulName.setText((String) evt.getNewValue());
                break;
            case Modul.CP_CHANGE_EVENT:
                int cpInt = (int) evt.getNewValue();
                cpLabel.setText(cpInt + " CP");
                break;
            case Modul.NOTE_CHANGE_EVENT:
                double note = (double) evt.getNewValue();
                noteLabel.setText(String.valueOf(note));
            default:
                break;
        }
    }

    public int getModulNummer() {
        return this.modulNummer;
    }
}
