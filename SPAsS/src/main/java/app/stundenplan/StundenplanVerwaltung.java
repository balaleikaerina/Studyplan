package app.stundenplan;

import app.Settings;
import app.module.Modul;
import app.module.SemesterListe;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.CpExceeds40Exception;
import exceptions.ModulExistiertNichtException;
import exceptions.ModulNichtBelegbarException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class StundenplanVerwaltung implements StundenplanInterface, PropertyChangeListener {


    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public static final String SPEICHERN = "studienplan.save";
    private Settings settings;

    private Stundenplan stundenplan;

    /**
     * In der StundenplanVerwaltung wird ein Stundenplan erstellt und die Settings werden mitgegeben.
     *
     * @param settings Settings werden per dependency injection geladen
     *
     * @param newUser boolean wert wird aus der StartUI mitgegeben, um zu prüfen, ob es sich um einen neuen oder bestehenden User hält.
     *                Sollte es sich um einen neuen User handeln, so wird ein createEmptyPlan() erstellt.
     *                Sollte es sich um einen bestehenden User handeln, so wird ein initStudienplanFromFile(settings.STUD_NAME) aufgerufen.
     *
     *
     */

    public StundenplanVerwaltung(Settings settings, boolean newUser) {
        this.settings = settings;
        stundenplan = new Stundenplan(settings);

        if(newUser) {
            stundenplan.createEmptyPlan();
        } else {
            stundenplan.initStudienplanFromFile(settings.getSTUD_NAME());
        }


        // Einen Listener auf die Module im Stundenplan setzen.
        List<SemesterListe> semListen = stundenplan.getSemesterListe();
        for(SemesterListe semList: semListen) {
            for(Modul modul: semList.getSemesterModule()) {
                modul.addPropertyChangeListener(this);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * Fügt ein Modul zu dem Stundenplan hinzu.
     * @param semester In welches Semester es gespeichert werden soll.
     * @param modul Um welches Modul es sich handelt.
     * @throws CpExceeds40Exception Hat der User über 40CP erreicht?
     * @throws ModulNichtBelegbarException Hat der User die benötigten Abhängigkeiten schon bestanden?
     */

    public void addModul(int semester, Modul modul) throws CpExceeds40Exception, ModulNichtBelegbarException {
        if(stundenplan.getSemesterListe(semester).getSemZahl() <= semester) {
            try {
                checkIfAddingIsPossible(semester, modul);
                modul.addPropertyChangeListener(this);
                stundenplan.setSemesterModule(semester, modul);
                this.pcs.firePropertyChange(Stundenplan.ADD_MODULE_EVENT, semester, modul);
            } catch(ModulNichtBelegbarException e) {
                throw e;
            }
        }
    }


    /**
     * Speichert den Studienplan (Settings, modulListe und Stundenplan) in mehrere JSON Dateien.
     * Hintergrund: Bei einem leeren Stundenplan wird die ModulListe "leer". also unbearbeitet aus einer JSON Datei eingelesen.
     *              Wenn der User jetzt ein Modul hinzufügt, bearbeitet oder seine Note einträgt, dann wird diese Datei neu geschrieben.
     *              Und wenn der User dann seinen Studienplan öffnet, sind sowohl Stundenplan als auch die ModulListe (Welche alle Module anzeigt) auf dem selben Stand.
     *
     *              Die Dateien lassen sich auch getrennt einfacher serialisieren und deserialisieren, da wir eine ModulListe z.b aus einer ModulListe.json direkt weitergeben können.
     * @throws IOException
     */

    public void saveStundenplan() throws IOException {
        ObjectMapper stundenplanMapper = new ObjectMapper();
        ObjectMapper settingsMapper = new ObjectMapper();
        ObjectMapper modulMapper = new ObjectMapper();

        // Leider gibt es zwischen Windows und Linux unterschiede beim Einlesen von Dateien.
        // Deshalb gucken wir bei jeder Operation, bei der eine Datei eingelesen oder gespeichert wird, ob es sich bei dem OS um Windows oder Linux handelt.

        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            new File("..\\SPAsS\\src\\main\\java\\data\\" + settings.getSTUD_NAME()).mkdirs();

            stundenplanMapper.writeValue(new File("..\\SPAsS\\src\\main\\java\\data\\" + settings.getSTUD_NAME() + "\\stundenplan.json"), stundenplan.getSemesterListe());
            modulMapper.writeValue(new File("..\\SPAsS\\src\\main\\java\\data\\" + settings.getSTUD_NAME() + "\\modulListe.json"), settings.modulListeDomain);
            settingsMapper.writeValue(new File("..\\SPAsS\\src\\main\\java\\data\\" + settings.getSTUD_NAME() + "\\settings.json"), settings);
        } else {
            new File("src/main/java/data/" + settings.getSTUD_NAME()).mkdirs();

            stundenplanMapper.writeValue(new File("src/main/java/data/" + settings.getSTUD_NAME() + "/stundenplan.json"), stundenplan.getSemesterListe());
            modulMapper.writeValue(new File("src/main/java/data/" + settings.getSTUD_NAME() + "/modulListe.json"), settings.modulListeDomain.getModulList().getModulListe());
            settingsMapper.writeValue(new File("src/main/java/data/" + settings.getSTUD_NAME() + "/settings.json"), settings);
        }

        this.pcs.firePropertyChange(SPEICHERN, null, null);
    }



    public void removeModul(int semester, Modul modul) {
        stundenplan.removeStundenplanModule(semester, modul);
    }


    /**
     * Entfernt ein Modul aus dem Stundenplan (Methode aus Stundenplan) und feuert ein Event
     * @param modul Zu löschendes Modul
     */

    public void removeModul(Modul modul) {
        if(stundenplan.removeModuleFromStundenplan(modul)) {
            this.pcs.firePropertyChange(Stundenplan.REMOVE_MODULE_EVENT, modul, null);
        }
    }

    public void editSemZahl(int semesterOld, int semesterNew, Modul modul) throws CpExceeds40Exception{
        if(stundenplan.getSemesterListe(semesterNew).getSemZahl() <= stundenplan.getSemesterListe().size()) {
            stundenplan.setSemesterModule(semesterNew, modul);
            stundenplan.removeStundenplanModule(semesterOld, modul);
        }
    }

    public double getDurchschnitt() {
        return stundenplan.getDurchschnitt();
    }

    public void addSemester() {
        stundenplan.addSemester();
        int maxSemester = stundenplan.getMaxSemester();
        this.pcs.firePropertyChange(Stundenplan.ADD_SEMESTER_EVENT, maxSemester - 1, maxSemester);
    }

    public void removeSemester() {
        int maxSemester = stundenplan.getMaxSemester();
        stundenplan.removeSemester();
        this.pcs.firePropertyChange(Stundenplan.REMOVE_SEMESTER_EVENT, maxSemester, maxSemester - 1);
    }

    /**
     * Hier werden die User Befehle validiert und überprüft.
     * @param semesterToAddTo (Das Semester wo das Modul hinzugefügt werden soll)
     * @param moduleToAdd (Das Modul welches hinzugefügt werden soll)
     * @return true, wenn die validierung erfolgreich ist, false wenn nicht.
     * @throws ModulNichtBelegbarException Exception die zurückgegeben wird, falls die Usereingabe nicht erfolgreich ist.
     */
    private boolean checkIfAddingIsPossible(int semesterToAddTo, Modul moduleToAdd) throws ModulNichtBelegbarException {
        String msg = "";
        //Check 1
        msg = msg.concat(alleSemesterBisBestanden(semesterToAddTo, moduleToAdd));
        //Check 2
        if(stundenplan.containsModule(moduleToAdd)) {
            msg += "Der Stundenplan enthaelt das Modul schon";
        }
        //Check 3
        msg = msg.concat(wirdInSemesterAngeboten(semesterToAddTo, moduleToAdd));
        //Check 4
        msg = msg.concat(maxZweiSemDifferenz(semesterToAddTo, moduleToAdd));
        //Check 5
        msg = msg.concat(alleAbhaengigkeitenBestanden(moduleToAdd));
        if(msg.equals("")) {
            return true;
        } else {
            throw new ModulNichtBelegbarException(msg);
        }
    }

    /**
     *  Hat der User bereits alle Module bestanden, um in diesem Semester Module hinzufügen zu können?
     * @param semesterToAddTo (Das Semester wo das Modul hinzugefügt werden soll)
     * @return boolean
     */

    private String alleSemesterBisBestanden(int semesterToAddTo, Modul moduleToAdd) {
        if(semesterToAddTo <= 3 || moduleToAdd.getFachSem() < semesterToAddTo) {
            return "";
        }
        for(Modul modul: settings.modulListeDomain) {
            if(modul.getFachSem() <= (semesterToAddTo - 3) && !modul.isBestanden()) {
                 return "Es wurden noch nicht alle Module bis einschliesslich Fachsemester " + (semesterToAddTo - 3) + " bestanden!\n";
            }
        }
        return "";
    }

    /**
     * Wird das Modul in diesem Semester angeboten?
     * @param semester (Das Semester in dem das Modul hinzugefügt werden soll)
     * @param modul (Das Modul welches hinzugefügt werden soll)
     * @return boolean
     */

    private String wirdInSemesterAngeboten(int semester, Modul modul) {
        //gerade semester sind sommer, ungerade sind winter
        if(semester%2 == 0) {
            if(!modul.getSommersemester()) {
                String msg = "Das Modul " + modul.getName() + " kann nicht im Sommersemester belegt werden!\n";
                return  msg;
            }
        } else {
            if(!modul.getWintersemester()) {
                String msg = "Das Modul " + modul.getName() + " kann nicht im Wintersemester belegt werden!\n";
                return msg;
            }
        }
        return "";
    }

    /**
     *  Hier wird geprüft, wie weit die Differenz zwischen den Semestern und dem Modul ist.
     * @param semesterToAddTo (Das Semester wo das Modul hinzugefügt werden soll)
     * @param modul (Das Modul welches hinzugefügt werden soll)
     * @return boolean
     */

    private String maxZweiSemDifferenz(int semesterToAddTo, Modul modul) {
        if(semesterToAddTo - modul.getFachSem() < -2) {
            return "Module dürfen maximal 2 Semester vor ihrer planmaessigen Belegung belegt werden!\n";
        }
        return "";
    }

    /**
     * Hier wird geprüft, ob alle Abhängigkeiten bestanden sind.
     * @param moduleToAdd (Das Modul welches hinzugefügt werden soll)
     * @return boolean
     */

    private String alleAbhaengigkeitenBestanden(Modul moduleToAdd) {
        String msg = "";
        for(Modul modul: settings.modulListeDomain) {
            for(Integer dependency: moduleToAdd.getAbhaengigkeiten()) {
                if(dependency == modul.getModulNummer()) {
                    if(!modul.isBestanden()) {
                        msg += "Modul " + modul.getName() + " muss bestanden sein um Modul " + moduleToAdd.getName() + " belegen zu koennen!\n";
                    }
                }
            }
        }
        return msg;
    }

    public Stundenplan getStundenplan() {
        return stundenplan;
    }

    /*
        Abfangen der PropertyChangeEvents
     */

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(Modul.REMOVE_CHANGE_EVENT)) {
            try {
                Modul modulToRemove = settings.modulListeDomain.getModulFromList((int) event.getNewValue());
                removeModul(modulToRemove);
            } catch (ModulExistiertNichtException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
