package app.module;

import app.Settings;
import app.stundenplan.StundenplanVerwaltung;
import domain.main.ModulListeDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ModulListDomainTest implements PropertyChangeListener {

    ModulListeDomain modulListeDomain = null;
    Settings settings = null;
    int counter = 0;
    double zahl = 0;
    double temp = 0;
    String[] namen = {"Mathe", "Deutsch", "Sport", "Englisch", "Biologie", "Chemie", "Geschichte", "Physik", "Informatik"};
    int[] modulNummern;
    int[] fachSemester;
    int[] cps;
    double[] note;

    @BeforeEach
    public void init() throws IOException {
        modulListeDomain = new ModulListeDomain();
        modulListeDomain.addPropertyChangeListener(this);
        settings = new Settings();
        settings.modulListeDomain.setModulList(modulListeDomain.getModulList());
        counter = 0;
        zahl = 0;
        temp = 0;
        modulNummern = new int[namen.length];
        fachSemester = new int[namen.length];
        cps = new int[namen.length];
        note = new double[namen.length];
        for(int i = 0; i < namen.length; i++) {
            modulNummern[i] = (int) (Math.random() * 8999 + 1000);
            fachSemester[i] = (int) (Math.random() * 7 + 1);
            cps[i] = (int) (Math.random() * 20 + 3);
            note[i] = (Math.random() * 3 + 1);
        }
    }

    @Test
    @DisplayName("ModulList creation")
    public void test_modulListCreate() {
        ModulListe modList = new ModulListe();
        int size = modList.getModulListe().size();
        assertTrue(size > 0);
    }

    @Test
    @DisplayName("Test creating ModulListeDomain")
    public void test_creation() {
        for(Modul m: modulListeDomain) {
            counter++;
        }
        assertEquals(29, counter);
    }

    @Test
    @DisplayName("Events get fired test")
    public void test_fireEvents() {
        for(int i = 0; i < namen.length; i++) {
            Modul m = new Modul(modulNummern[i], namen[i], fachSemester[i], cps[i], 4);
            m.setBestanden(true);
            modulListeDomain.add(m);
            double temp2 = temp;
            modulListeDomain.editModul(m.getModulNummer(), new Modul(modulNummern[i], namen[i], fachSemester[i], cps[i], note[i]));
            assertTrue(temp2 != temp);
        }


    }

    @Test
    @DisplayName("Computing mean and edit modules")
    public void compEdit() {
        for(int i = 0; i < namen.length; i++) {
            Modul m = new Modul(modulNummern[i], namen[i], fachSemester[i], cps[i], 4);
            m.setBestanden(true);
            modulListeDomain.add(m);
            modulListeDomain.editModul(m.getModulNummer(), new Modul(modulNummern[i], namen[i], fachSemester[i], cps[i], note[i]));
        }
        double x = 0;
        double y = 0;
        for(int i = 0; i < namen.length; i++) {
            x += cps[i];
            y += note[i] * (double) cps[i];
        }
        assertEquals((y/(double)x), temp);
    }

    @Test
    @DisplayName("Stundenplanverwaltung create")
    public void test_studCreate() {
        StundenplanVerwaltung stundenplanVerwaltung = new StundenplanVerwaltung(settings, true);
        stundenplanVerwaltung.addPropertyChangeListener(this);
        ModulListe modulListe = settings.modulListeDomain.getModulList();
        for(Modul m: modulListe) {
            if(m.getFachSem() == 1) {
                try {
                    stundenplanVerwaltung.addModul(1, m);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        assertEquals(5, counter);
        for(Modul m: modulListe) {
            if(m.getFachSem() == 1) {
                m.setBestanden(true);
            }
        }
        SemesterListe semList = stundenplanVerwaltung.getStundenplan().getSemesterListe(1);
        for(Modul m: semList) {
            assertTrue(m.isBestanden());
        }
        for(Modul m: modulListe) {
            stundenplanVerwaltung.removeModul(m);
        }
        assertEquals(0, counter);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("modul.add")) {
            counter++;
        } else if(evt.getPropertyName().equals("durchschnitt.changed")) {
            double cpSave = 0;
            double noteKum = 0;
            for(Modul m: modulListeDomain) {
                if(m.getNote() > 0 && m.getNote() <= 4 && m.isBestanden()) {
                    cpSave += m.getCp();
                    noteKum += m.getNote() * (double) m.getCp();
                }
            }
            temp = noteKum / (double) cpSave;
        } else if(evt.getPropertyName().equals("modul.remove")) {
            counter--;
        }
    }

}
