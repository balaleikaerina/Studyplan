package app;

import app.module.Modul;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.main.ModulListeDomain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Settings Klasse über die auf die ModulListeDomain zugegriffen werden kann und die den Benutzernamen sowie, die maximal belegbaren CP speichert
 */
public class Settings {

     String STUD_NAME;
     int MAX_CP = 40;

     @JsonIgnore
     public ModulListeDomain modulListeDomain;

     /**
      * Constructor für die Settings
      */
     public Settings() {
          try {
               modulListeDomain = new ModulListeDomain();
          } catch (IOException ioException) {
               ioException.printStackTrace();
          }
     }

     /**
      * Initialisiert die settings aus einem vorhandenen JSON-File. Abhängig davon ob windows oder ein anderes OS verwendet wird, ließt die Funktion die Pfade unterschiedlich ein.
      * @param name
      * Die JSON wird mit dem Jackson-Framework geladen.
      */
     public void initSettingsFromFile(String name) {

          try {
               ObjectMapper objectMapper = new ObjectMapper();

               String jsonString;

               if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                    jsonString = new String(Files.readAllBytes(Paths.get("..\\SPAsS\\src\\main\\java\\data\\"+ name +"\\settings.json")));
               } else {
                    jsonString = new String(Files.readAllBytes(Paths.get("SPAsS/src/main/java/data/"+ name + "/settings.json")));
               }

               /*
               setSTUD_NAME(objectMapper.readValue(jsonString, new TypeReference<>() {
               }).toString().split(",")[0]);
                 */
               objectMapper.readValue(jsonString, Settings.class);

          } catch (IOException e) {
               e.printStackTrace();
          }

     }

     public static final String STUD_NAME_CHANGE = "STUD_NAME.change";

     private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

     public void addPropertyChangeListener(PropertyChangeListener listener) {
          this.pcs.addPropertyChangeListener(listener);
     }

     public void removePropertyChangeListener(PropertyChangeListener listener) {
          this.pcs.removePropertyChangeListener(listener);
     }

     public void setSTUD_NAME(String STUD_NAME) {
          String oldStudName = this.STUD_NAME;
          this.STUD_NAME = STUD_NAME;
          this.pcs.firePropertyChange("STUD_NAME.change", oldStudName, STUD_NAME);
     }

     public ArrayList<String> getSettings() {
          ArrayList<String> settingsJSON = new ArrayList<String>();
          settingsJSON.add(STUD_NAME);
          settingsJSON.add(Integer.toString(MAX_CP));
          return settingsJSON;
     }


     public String getSTUD_NAME() {
          return STUD_NAME;
     }

     public int getMAX_CP() {
          return MAX_CP;
     }

     public void setMAX_CP(int MAX_CP) {
          this.MAX_CP = MAX_CP;
     }


}
