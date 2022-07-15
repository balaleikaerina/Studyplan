package gui.components;

import app.module.Modul;
import app.stundenplan.StundenplanVerwaltung;
import gui.MainUI;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Das Cell Objekt der ModulListe.
 */

public class ModulListCell extends ListCell<Modul> {

    @Override
    protected void updateItem(Modul m, boolean empty) {
        super.updateItem(m, empty);

        //löschen des alten Inhalts des labels
        setText(null);
        setGraphic(null);

        if(m != null) {
            ModulBox modulBox = new ModulBox(m.getName(), m.getCp(), m.getModulNummer(), m.getAbhaengigkeiten(), m.isBestanden(), m);
            modulBox.setOnDragDetected((event) -> {
                System.out.println("Drag and Drop startet on Modul: " + modulBox.getModulNummer());
                Dragboard dragboard = modulBox.startDragAndDrop(TransferMode.MOVE);
                dragboard.setDragView(modulBox.snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.putString(Integer.toString(modulBox.getModulNummer()));
                dragboard.setContent(content);
                event.consume();
            });
            setGraphic(modulBox);
        }
    }
}
