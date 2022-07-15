package gui.components;

import app.module.Modul;
import domain.main.ModulListeDomain;
import exceptions.ModulExistiertNichtException;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * JavaFX Customized VBox (Module bearbeiten)
 */

public class EditModuleBox extends VBox {
    private TextField modNameInput, modCpInput, modFsInput, modNoteInput;
    int modulNummer;

    public EditModuleBox(int modulNummer, ModulListeDomain modulListeDomain) {
        getStylesheets().add("libs/css/application.css");
        setPadding(new javafx.geometry.Insets(30, 30, 30, 30));
        setMinWidth(540);
        Modul modul;
        this.modulNummer = modulNummer;
        

        Label modName = new Label("Modulname");
        modNameInput = new TextField();
        modNameInput.getStyleClass().add("inputFeld");
        modName.getStyleClass().add("settingsLabel");

        Label modCp = new Label("CP");
        modCpInput = new TextField();
        modCpInput.getStyleClass().add("inputFeld");
        modCp.getStyleClass().add("settingsLabel");

        Label modFs = new Label("Fachsemester");
        modFsInput = new TextField();
        modFsInput.getStyleClass().add("inputFeld");
        modFs.getStyleClass().add("settingsLabel");

        Label modNote = new Label("Note");
        modNoteInput = new TextField();
        modNoteInput.getStyleClass().add("inputFeld");
        modNote.getStyleClass().add("settingsLabel");

        try {
            modul = modulListeDomain.getModulFromList(modulNummer);
            modNameInput.setText(modul.getName());
            modCpInput.setText(String.valueOf(modul.getCp()));
            modFsInput.setText(String.valueOf(modul.getFachSem()));
            modNoteInput.setText(String.valueOf(modul.getNote()));
        } catch(ModulExistiertNichtException e) {
            e.printStackTrace();
        }

        getChildren().addAll(modName, modNameInput, modCp, modCpInput, modFs, modFsInput, modNote, modNoteInput);
    }

    /**
     * Holt sich die Dateien aus der Form
     * @return Modul
     */
    
    public Modul getModFromInput() {
        String name = modNameInput.getText();
        String save = modCpInput.getText();
        int cp = Integer.parseInt(save.equals("")? "0" : save);
        save = modFsInput.getText();
        int fs = Integer.parseInt(save.equals("")? "0" : save);
        save = modNoteInput.getText();
        double note = Double.parseDouble(save.equals("")? "0" : save);



        return new Modul(modulNummer, name, fs, cp, note);
    }
}
