package app.stundenplan;

import app.module.Modul;
import exceptions.CpExceeds40Exception;
import exceptions.ModulNichtBelegbarException;

/**
 * Interface zum hinzufügen und entfernen von Modulen.
 */

public interface StundenplanInterface {
     void addModul(int semester, Modul m) throws CpExceeds40Exception, ModulNichtBelegbarException;
     void removeModul(int semester, Modul m);

    
}
