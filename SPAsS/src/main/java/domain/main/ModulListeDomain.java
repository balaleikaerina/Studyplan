package domain.main;

import app.module.Modul;
import app.module.ModulListe;
import exceptions.ModulExistiertNichtException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ModulListeDomain-Klasse ist die zentrale Verwaltung aller verfügbaren Module.
 */
public class ModulListeDomain implements Iterable<Modul>, PropertyChangeListener {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String NOTE_CHANGED_EVENT = "durchschnitt.changed";

    private ModulListe modulList;

    /**
     * Constructor für die ModulListeDomain. Initialisiert die ModulListe und fügt einen PropertyChangeListener hinzu.
     * @throws IOException falls bei der Initialisierung der ModulListe die ModulListe-JSON nicht gelesen werden kann
     */
    public ModulListeDomain() throws IOException {
        modulList = new ModulListe();
        modulList.addPropertyChangeListener(this);
        
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * Fügt der ModulListe ein Modul hinzu und feuert ein Stundenplan.ADD_MODULE_EVENT
     * @param m | Modul m ist das hinzuzufügende Modul
     */
    public void add(Modul m) {
        modulList.add(m);
        this.pcs.firePropertyChange("modul.add", null, m);
    }

    /**
     * Setzt die Attribute Name, Fachsemester, CP und Note eines bestehenden Moduls aus der ModulListe auf die entsprechenden Attribute eines übergebenen Moduls.
     * Feuert anschließend falls die Note angepasst wurde ein NOTE_CHANGED_EVENT
     * @param modulNummer | ModulNummer des zu verändernden Moduls
     * @param newModule | Modul mit den neuen Werten
     */
    public void editModul(int modulNummer, Modul newModule) {
        try {
            Modul oldModule = getModulFromList(modulNummer);
            oldModule.setName(newModule.getName());
            oldModule.setFachSem(newModule.getFachSem());
            oldModule.setCp(newModule.getCp());
            if(oldModule.getNote() != newModule.getNote()) {
                oldModule.setNote(newModule.getNote());
                this.pcs.firePropertyChange(NOTE_CHANGED_EVENT, null, oldModule.getModulNummer());
            }
        } catch(ModulExistiertNichtException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gibt ein Modul zu einer übergebenen ModulNummer aus der ModulListe zurück, falls es existiert
     * @param modulNummer | ModulNummer des zu suchenden Moduls
     * @return | Das gesuchte Modul, falls es gefunden wird
     * @throws ModulExistiertNichtException | Falls das Modul nicht gefunden wird
     */
    public Modul getModulFromList(int modulNummer) throws ModulExistiertNichtException {
        for(Modul m: modulList) {
            if(m.getModulNummer() == modulNummer) {
                return m;
            }
        }

        throw new ModulExistiertNichtException("Modul existiert nicht");
    }

    public void setModulList(ModulListe modulList) {
        this.modulList = modulList;
    }

    public ModulListe getModulList() {
        return this.modulList;
    }

    @Override
    public Iterator<Modul> iterator() {
        return modulList.iterator();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(ModulListe.DURCHSCHNITT_CHANGE_EVENT)) {
            this.pcs.firePropertyChange(NOTE_CHANGED_EVENT, null, evt.getNewValue());
        }
        
    }

}
