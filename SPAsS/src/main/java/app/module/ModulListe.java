package app.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ModulExistiertNichtException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModulListe implements ModulInterface, Iterable<Modul>, PropertyChangeListener {

    List<Modul> modulListe = new ArrayList<>();
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String DURCHSCHNITT_CHANGE_EVENT = "modulliste.change";

    /**
     * Contructor für eine Modulliste
     */
    public ModulListe() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //check if Windows or Linux
            String jsonString;

            if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                jsonString = new String(Files.readAllBytes(Paths.get("..\\SPAsS\\src\\main\\java\\data\\file.json")));
            } else {
                jsonString = new String(Files.readAllBytes(Paths.get("src/main/java/data/file.json")));
            }
            // C:\Users\Felix\IdeaProjects\Studienplaner\SPAsS\src\main\java\data\file.json
            //String jsonString = new String(Files.readAllBytes(Paths.get("..\\Studienplaner\\SPAsS\\src\\main\\java\\data\\file.json")));
            List<Modul> listModules = objectMapper.readValue(jsonString, new TypeReference<>() {
            });
            modulListe.addAll(listModules);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Modul m: modulListe) {
            m.addPropertyChangeListener(this);
        }
    }

    /**
     * Contructor für eine Modulliste, die eine vorhandene ModulListe beinhaltet
     * @param modulListe
     */
    public ModulListe(List<Modul> modulListe) {
        this.modulListe = modulListe;
    }

    public List<Modul> getModulListe() {
        return modulListe;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Gibt zu einer eingegebenen Modulnummer das passende Modul
     * @param modulNummer
     * @return Modul
     * @throws ModulExistiertNichtException
     */
    public Modul getModulFromList(int modulNummer) throws ModulExistiertNichtException {
        for(Modul m: modulListe) {
            if(m.getModulNummer() == modulNummer) {
                return m;
            }
        }
        throw new ModulExistiertNichtException("Modul existiert nicht");
    }

    public boolean add(Modul modul) {
        return modulListe.add(modul);
    }

    /**
     * Erstellt ein Modul und fügt es der modulListe hinzu
     * @param modulNummer
     * @param name
     * @param fachSem
     * @param lvs
     * @param abhaengigkeiten
     * @param wintersemester
     * @param sommersemester
     * @param cp
     */
    @Override
    public void createModul(int modulNummer, String name, int fachSem, List<LehrVeranstaltung> lvs, List<Integer> abhaengigkeiten, boolean wintersemester, boolean sommersemester, int cp) {
        modulListe.add(new Modul(modulNummer, name, fachSem, lvs, abhaengigkeiten, wintersemester, sommersemester, cp, 0.0, false));
        
    }

    /**
     * Hoffentlich sinnlose Klasse, ich traue mich aber nicht sie zu löschen
     * @param m
     * @param name
     * @param note
     * @param fachSem
     * @param lvs
     * @param abhaengigkeiten
     */
    @Override
    public void editModul(Modul m, String name, double note, int fachSem, List<LehrVeranstaltung> lvs, List<Integer> abhaengigkeiten) {
        m.setName(name);
        m.setNote(note);
        m.setFachSem(fachSem);
        m.setLvs(lvs);
        m.setAbhaengigkeiten(abhaengigkeiten);
    }

    public void setModulListe(List<Modul> modulListe) {
        this.modulListe = modulListe;
    }

    @Override
    public Iterator<Modul> iterator() {
        return modulListe.iterator();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(Modul.BESTANDEN_CHANGE_EVENT)) {
            this.pcs.firePropertyChange(DURCHSCHNITT_CHANGE_EVENT, null, evt.getNewValue());
        }
        
    }
}
