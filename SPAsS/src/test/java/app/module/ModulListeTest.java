package app.module;

import exceptions.ModulExistiertNichtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ModulListeTest {

    Modul modul = null;

    ModulListe modulListe = null;

    @BeforeEach
    public void init() {
        modulListe = new ModulListe();
        modul = new Modul(1000, "SCRUM Kurs", 5, null, null, true, true, 10, 0.0, false);

    }

    @Test
    @DisplayName("Existiert eine gefüllte ModulListe?")
    public void test_modulListeExistiert() {
        assertNotNull(modulListe);

    }

    @Test
    @DisplayName("Modul hinzufügen und ändern. Überprüft ob die änderung in der ModulListe durchgeht.")
    public void addModul() throws ModulExistiertNichtException {
        modulListe.add(modul);
        assertNotNull(modulListe.getModulFromList(1000));
        modulListe.editModul(
                modul,
                "Teams",
                modul.getNote(),
                modul.getFachSem(),
                modul.getLvs(),
                modul.getAbhaengigkeiten());
        assertEquals("Teams", modulListe.getModulFromList(1000).getName());
    }

    @Test
    @DisplayName("Modul entfernen, erwartet Exception")
    public void removeModul()  {
        modulListe.add(modul);
        modulListe.getModulListe().remove(modul);


        Exception exception = assertThrows(ModulExistiertNichtException.class, () -> {
            modulListe.getModulFromList(1000);
        });

        String expected = "Modul existiert nicht";
        String actual = exception.getMessage();

        assertTrue(expected.contains(actual));
    }

    @Test
    @DisplayName("GET from ModulListe by modulNummer")
    public void getModulFromList() {
        try {
            modulListe.getModulFromList(1110);
        } catch (ModulExistiertNichtException e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(() -> {});

    }


}
