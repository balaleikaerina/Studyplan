package app.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * In dieser Klasse werden die LehrVeranstaltungen instanziiert
 */
public class Modul {

    protected int modulNummer, cp, fachSem;;
    protected String name;
    protected double note;
    protected boolean bestanden;
    protected boolean wintersemester;
    protected boolean sommersemester;
    protected boolean del = false;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String BESTANDEN_CHANGE_EVENT = "module.bestanden.change";
    public static final String NAME_CHANGE_EVENT = "module.name.change";
    public static final String CP_CHANGE_EVENT = "module.cp.change";
    public static final String FS_CHANGE_EVENT = "module.fs.change";
    public static final String NOTE_CHANGE_EVENT = "module.note.change";

    public static final String REMOVE_CHANGE_EVENT = "module.remove";

    List<LehrVeranstaltung> lvs = new ArrayList<>();

    List<Integer> abhaengigkeiten = new ArrayList<>();

    /**
     * Constructor für ein Modul
     * @param modulNummer | Eindeutige ID für ein Modul.
     * @param name | Name des Moduls
     * @param fachSem | Fachsemester in dem das Modul angeboten wird.
     * @param lvs | LehrVeranstaltung (Vorlesung|Praktikum) gespeichert in einer Liste als <LehrVeranstaltung>
     * @param abhaengigkeiten
     */
    @JsonCreator
    public Modul(@JsonProperty("modulNummer") int modulNummer, @JsonProperty("name") String name, @JsonProperty("fachSem") int fachSem, @JsonProperty("lvs") List<LehrVeranstaltung> lvs, @JsonProperty("abhaengigkeiten") List<Integer> abhaengigkeiten, @JsonProperty("wintersemester") boolean wintersemester, @JsonProperty("sommersemester") boolean sommersemester, @JsonProperty("cp") int cp, @JsonProperty("note") double note, @JsonProperty("bestanden") boolean bestanden) {
        this.modulNummer = modulNummer;
        this.name = name;
        this.fachSem = fachSem;
        this.lvs = lvs;
        this.abhaengigkeiten = abhaengigkeiten;
        this.wintersemester = wintersemester;
        this.sommersemester = sommersemester;
        this.cp = cp;
        this.note = note;
        this.bestanden = bestanden;
    }

    /**
     * Constructor für ein Modul
     * @param modulNummer
     * @param name
     * @param fachSem
     * @param cp
     * @param note
     */
    public Modul(int modulNummer, String name, int fachSem, int cp, double note) {
        this.modulNummer = modulNummer;
        this.name = name;
        this.fachSem = fachSem;
        this.cp = cp;
        this.note = note;
    }

    /**
     * Fügt listener hinzu
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Entfernt listener
     * @param listener
     */

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public int getModulNummer() {
        return modulNummer;
    }

    /**
     * Methode zum hinzufügen von LehrVeranstaltungen
     * @param lehrVeranstaltung
     */

    public void addLehrVeranstaltung(LehrVeranstaltung lehrVeranstaltung) {
        lvs.add(lehrVeranstaltung);
    }

    /**
     * Methode zum hinzufügen von Abhängigkeiten
     * @param modul
     */

    public void addAbhaengigkeiten(Modul modul) {
        abhaengigkeiten.add(modul.modulNummer);
    }

    public String getName() {
        return name;
    }

    /**
     * Setter für den Namen des Moduls, pusht einen PropertyChangeEvent.
     * @param name
     */

    public void setName(String name) {
        this.name = name;
        this.pcs.firePropertyChange(NAME_CHANGE_EVENT, null, name);
    }

    public int getCp() {
        return cp;
    }

    /**
     * Setter für die CP des Moduls, pusht einen PropertyChangeEvent.
     * @param cp
     */
    public void setCp(int cp) {
        this.cp = cp;
        this.pcs.firePropertyChange(CP_CHANGE_EVENT, null, cp);
    }

    public double getNote() {
        return note;
    }

    /**
     * Setter für die Note des Moduls, pusht einen PropertyChangeEvent.
     * @param note
     */
    public void setNote(double note) {
        if(note > 4 || note < 1) {
            return;
        }
        this.note = note;
        this.pcs.firePropertyChange(NOTE_CHANGE_EVENT, null, note);
    }

    public void pseudoDelete() {
        del = !del;
        this.pcs.firePropertyChange(REMOVE_CHANGE_EVENT, del, this.modulNummer);
    }

    public int getFachSem() {
        return fachSem;
    }

    /**
     * Setter für das Fachsemester des Moduls, pusht einen PropertyChangeEvent.
     * @param fachSem
     */

    public void setFachSem(int fachSem) {
        this.fachSem = fachSem;
        this.pcs.firePropertyChange(FS_CHANGE_EVENT, null, fachSem);
    }

    public boolean isBestanden() {
        return bestanden;
    }

    /**
     * Methode um ein Fach als bestanden einzutragen.
     * @param bestanden
     */

    public void setBestanden(boolean bestanden) {
        this.bestanden = bestanden;
        this.pcs.firePropertyChange(BESTANDEN_CHANGE_EVENT, null, bestanden);
    }

    public List<LehrVeranstaltung> getLvs() {
        return lvs;
    }

    public void setLvs(List<LehrVeranstaltung> modulListe) {
        this.lvs = modulListe;
    }

    public List<Integer> getAbhaengigkeiten() {
        return abhaengigkeiten;
    }

    public void setAbhaengigkeiten(List<Integer> abhaengigkeiten) {
        this.abhaengigkeiten = abhaengigkeiten;
    }

    public boolean getWintersemester() {
        return this.wintersemester;
    }

    public boolean getSommersemester() {
        return this.sommersemester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modul modul = (Modul) o;
        return Objects.equals(modulNummer, modul.modulNummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Modul{" +
                "modulNummer=" + modulNummer +
                ", name='" + name + '\'' +
                ", cp=" + cp +
                ", note=" + note +
                ", fachSem=" + fachSem +
                ", bestanden=" + bestanden +
                ", lvs=" + lvs +
                ", abhaengigkeiten=" + abhaengigkeiten +
                '}';
    }
}
