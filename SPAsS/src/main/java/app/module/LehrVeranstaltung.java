package app.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * In dieser Klasse werden die LehrVeranstaltungen instanziiert
 */
public class LehrVeranstaltung {

    protected String name;
    protected int lvNr;
    protected double note;
    protected String dozent;
    protected int cp;

    /**
     * Constructor für die LehrVeranstaltung
     * @param name | Name
     * @param lvNr | LehrVeranstallungs(Modul) Nummer
     * @param note | Eingetragene Note
     * @param dozent
     * @param cp | Credit Points für diese LV erreichbar
     */

    public LehrVeranstaltung(String name, int lvNr, double note, String dozent, int cp) {
        this.name = name;
        this.lvNr = lvNr;
        this.note = note;
        this.dozent = dozent;
        this.cp = cp;
    }

    /**
     * JSON Jackson Constructor für das einlesen der LehrVeranstaltungen aus einem gespeichertem Studienplan
     * @param name
     * @param lvNr
     * @param dozent
     * @param cp
     */

    @JsonCreator
    public LehrVeranstaltung(@JsonProperty("name") String name, @JsonProperty("lvNr") int lvNr, @JsonProperty("dozent") String dozent, @JsonProperty("cp") int cp) {
        this(name, lvNr, 0, dozent, cp);
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public int getLvlNr() {
        return lvNr;
    }

    public void setLvlNr(int lvlNr) {
        this.lvNr = lvlNr;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public String getDozent() {
        return dozent;
    }

    public void setDozent(String dozent) {
        this.dozent = dozent;
    }

    
}
