package app.stundenplan;

import app.Settings;
import app.module.Modul;
import app.module.SemesterListe;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.CpExceeds40Exception;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Stundenplan {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String ADD_SEMESTER_EVENT = "semester.add";
    public static final String REMOVE_SEMESTER_EVENT = "semester.remove";
    public static final String ADD_MODULE_EVENT = "modul.add";
    public static final String REMOVE_MODULE_EVENT = "modul.remove";


    private List<SemesterListe> semesterListe = new ArrayList<>();
    private Settings settings;

    /**
     * Constructor für einen Stundenplan
     * @param settings Settings werden per dependency injection geladen
     */
    public Stundenplan(Settings settings) {
        this.settings = settings;
    }

    /**
     *
     */
    public void createEmptyPlan() {
        for(int i = 1; i <= 7; i++) {
            semesterListe.add(new SemesterListe(i));
        }
    }

    /**
     * Initialisiert einen Stundenplan aus einer JSON Datei, in der stundenplan.json ist die semesterListe inklusive der Module gespeichert.
     * @param name | Der übergebene Dateiname ist der Username des Users, der den Stundenplan erstellt hat. (Alle Dateien sind in einem User-Ordner gespeichert und werden von dort aus geladen).
     * Die JSON wird mit dem Jackson-Framework geladen.
     */

    public void initStudienplanFromFile(String name) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString;

            if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                jsonString = new String(Files.readAllBytes(Paths.get("..\\SPAsS\\src\\main\\java\\data\\" + name + "\\stundenplan.json")));
            } else {
                jsonString = new String(Files.readAllBytes(Paths.get("src/main/java/data/" + name + "/stundenplan.json")));

            }
            List<SemesterListe> tempSemList = objectMapper.readValue(jsonString, new TypeReference<>() {});
            int semesterIndex = 1;
            for(SemesterListe semListe: tempSemList) {
                SemesterListe semListToAdd = new SemesterListe(semesterIndex);
                for(Modul modulToCopy: semListe) {
                    Modul modulToAdd = settings.modulListeDomain.getModulFromList(modulToCopy.getModulNummer());
                    semListToAdd.addModul(modulToAdd);
                }
                semesterListe.add(semListToAdd);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }




    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public List<SemesterListe> getSemesterListe() {
        return semesterListe;
    }

    public SemesterListe getSemesterListe(int semester) {
        return semesterListe.get(semester-1);
    }

    /**
     * Fügt ein Semester hinzu. dem Stundenplan hinzu
     * @param semester (In welches Semester)
     * @param modul (Das Modul, das hinzugefügt werden soll)
     * @throws CpExceeds40Exception Wird geworfen, wenn die CP des Moduls über 40 liegt.
     */

    public void setSemesterModule(int semester, Modul modul) throws CpExceeds40Exception {
        semesterListe.get(semester-1).addModul(modul);
    }

    /**
     * Entfernt ein Modul aus einem Semester
     * @param semester (In welches Semester)
     * @param modul (Das Modul, das entfernt werden soll)
     */

    public void removeStundenplanModule(int semester, Modul modul) {
        semesterListe.get(semester-1).removeModul(modul);
    }

    /**
     * Boolean gibt zurück, ob ein Modul vom Stundenplan entfernt wurde. Danach wird ein REMOVE_MODUL_EVENT angestoßen.
     * @param modul Modul Object
     * @return boolean wert
     */

    public boolean removeModuleFromStundenplan(Modul modul) {
        for(SemesterListe liste: semesterListe) {
            if(liste.hasModul(modul)) {
                return liste.removeModul(modul);
            }
        }
        return false;
    }

    /**
     * Füge ein weiteres Semester zum Stundenplan hinzu.
     */

    public void addSemester() {
        SemesterListe semesterListe = new SemesterListe(this.semesterListe.size() + 1);
        this.semesterListe.add(semesterListe);
    }

    /*
    * Entfernt ein Semester aus dem Stundenplan.
    */

    public void removeSemester() {
        semesterListe.remove(semesterListe.size() - 1);
    }

    public int getMaxSemester() {
        return semesterListe.size();
    }

    /**
     * Errechnet Durchschnittsnote aller bestandenen Module.
     * @return Durchschnittsnote
     */
    public double getDurchschnitt() {
        int bestMods = 0;
        double noteKummuliert = 0;
        for(SemesterListe liste: semesterListe) {
            for(Modul m: liste) {
                if(m.isBestanden() && m.getNote() != 0) {
                    bestMods += m.getCp();
                    noteKummuliert += m.getNote() * (double) m.getCp();
                }
            }

        }
        if(bestMods == 0) {
            return 0;
        }
        return noteKummuliert / (double) bestMods;
    }

    /**
     * Diese Methode prüft, ob das Modul bereits im Stundenplan existiert.
     * @param modul (Modul Object)
     * @return boolean wert
     */

    public boolean containsModule(Modul modul) {
        for(SemesterListe semesterListe: semesterListe) {
            if(semesterListe.hasModul(modul)) {
                return true;
            }
        }
        return false;
    }
}
