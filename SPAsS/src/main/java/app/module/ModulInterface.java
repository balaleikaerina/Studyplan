package app.module;

import java.util.List;

//TODO deleteModul hinzufügen? ja/nein?

/**
 * Interface, wird von der ModulListe implementiert.
 * CREATE | EDIT der Module.
 */
public interface ModulInterface {
    void createModul(int modulNummer, String name, int fachSem, List<LehrVeranstaltung> lvs, List<Integer> abhaengigkeiten, boolean wintersemester, boolean sommersemester, int cp);
    void editModul(Modul m, String name, double note, int fachSem, List<LehrVeranstaltung> lvs, List<Integer> abhaengigkeiten);

}
    
