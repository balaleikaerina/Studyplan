package app.module;

import app.module.Modul;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class ModulTest {

    Modul modul = null;

    @BeforeEach
    public void init() {
        LehrVeranstaltung lehrVeranstaltungV = new LehrVeranstaltung("Einführung in die Medieninformatik", 1111, 0.0, "Prof. Dr. Philipp Schaible", 3);
        LehrVeranstaltung lehrVeranstaltungP = new LehrVeranstaltung("Einführung in die Medieninformatik (Praktikum)", 1112, 0.0, "Prof. Dr. Philipp Schaible", 2);
        List<LehrVeranstaltung> lehrVeranstaltungList = new ArrayList<>();
        lehrVeranstaltungList.add(lehrVeranstaltungV);
        lehrVeranstaltungList.add(lehrVeranstaltungP);
        List<Integer> abhaengigkeiten = new ArrayList<>();

        modul = new Modul(1110, "Einführung in die Medieninformatik", 1, lehrVeranstaltungList, abhaengigkeiten, true, true, 5, 0.0, false);
    }
    
    @Test
    @DisplayName("Create Modul")
    public void test_createModule() {
        assertEquals("Einführung in die Medieninformatik", modul.getName());
        assertEquals(1, modul.getFachSem());
        assertEquals(5, modul.getCp());
        assertFalse(modul.isBestanden());
        assertTrue(modul.getWintersemester());
        assertTrue(modul.getSommersemester());
    }

    @Test
    @DisplayName("Set-/Get-Modulattributes")
    public void test_getSet() {
        modul.setBestanden(true);
        modul.setName("Mathe");
        modul.setNote(1.0);
        modul.setCp(7);
        modul.setFachSem(3);
        assertTrue(modul.isBestanden());
        assertEquals("Mathe", modul.getName());
        assertEquals(1110, modul.getModulNummer());
        assertEquals(1.0, modul.getNote());
        assertEquals(7, modul.getCp());
        assertEquals(3, modul.getFachSem());
    }
}
