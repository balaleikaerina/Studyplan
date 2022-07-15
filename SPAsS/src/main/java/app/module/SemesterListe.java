package app.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import exceptions.CpExceeds40Exception;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonDeserialize
public class SemesterListe implements Iterable<Modul>{

    int semZahl;
    int cpSum = 0;

    private List<Modul> semesterModule = new ArrayList<>();

    /**
     * Contructor einer SemesterListe
     * @param semZahl
     */
    public SemesterListe(int semZahl) {
        this.semZahl = semZahl;
    }

    /**
     * Contructor einer SemesterListe
     * @param semZahl
     * @param semesterModule
     */
    @JsonCreator
    public SemesterListe(@JsonProperty("semZahl") int semZahl, @JsonProperty("semesterModule") List<Modul> semesterModule) {
        this.semZahl = semZahl;
        this.semesterModule = semesterModule;
    }


    public List<Modul> getSemesterModule() {
        return semesterModule;
    }

    /**
     * fügt Modul den SemesterModulen hinzu, falls dann nicht mehr als 40 CP in der Semesterliste sind
     * @param modul
     * @throws CpExceeds40Exception
     */
    public void addModul(Modul modul) throws CpExceeds40Exception {
        if(cpSum + modul.getCp() < 40) {
            semesterModule.add(modul);
            cpSum += modul.getCp();
        } else {
            throw new CpExceeds40Exception("Maximal 40 Cp pro Semester: momentane Cp " + cpSum);
        }
    }

    /**
     * Entfernt modul aus der SemesterListe
     * @param modul
     * @return true falls modul entfernt wurde, false falls nicht
     */
    public boolean removeModul(Modul modul) {
        cpSum -= modul.getCp();
        return semesterModule.remove(modul);
    }

    public int getSemZahl() {
        return semZahl;
    }

    /**
     * Errechnet den Notendurchschnitt im aktuellen Semester
     * @return Durchschnittsnote
     */
    @JsonIgnore
    public double getDurchschnitt() {
        int bestMods = 0;
        double noteKummuliert = 0;
        for(Modul m: semesterModule) {
            if(m.bestanden && m.note != 0) {
                bestMods += m.getCp();
                noteKummuliert += m.note * (double) m.getCp();
            }
        }
        if(bestMods == 0) {
            return 0;
        }
        return noteKummuliert / (double) bestMods;
    }

    /**
     * Prüft, ob das eingegebene Modul in der Semesterliste vorhanden ist
     * @param m
     * @return true falls Modul vorhanden, false falls nicht vorhanden
     */
    public boolean hasModul(Modul m) {
        return semesterModule.contains(m);
    }

    @JsonIgnore
    public int getBestMods() {
        int i = 0;
        for(Modul m: semesterModule) {
            if(m.isBestanden() && m.getNote() > 0 && m.getNote() <= 4) {
                i++;
            }
        }
        return i;
    }

    @Override
    public Iterator<Modul> iterator() {
        return semesterModule.iterator();
    }
}
