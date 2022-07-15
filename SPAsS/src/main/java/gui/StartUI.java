package gui;

import app.Settings;
import app.module.Modul;
import app.module.ModulListe;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.App;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class StartUI extends Settings implements PropertyChangeListener {

    private Settings settings = new Settings();

    private File filePath;

    @FXML
    private TextField nameInput;

    @FXML
    private ChoiceBox<String> studChoiceBox;

    @FXML
    ListView planListView;

    @FXML
    Button createPlanBtn, createPlan, createPlanWeiter;

    @FXML
    Pane studAuswaehlen, studErstellen;

    private boolean newUser = true;

    public StartUI() {

    }

    @FXML
    public void initialize() {

        /*
        Falls im data Ordner andere Ordner existieren, dann werden die in unserer ListView beim initialisieren des Programms angezeigt.
        Falls nicht, dann wird der User automatisch aufgefordert, einen neuen Studienplan zu erstellen.
         */

        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            filePath = new File("..\\SPAsS\\src\\main\\java\\data\\");
        } else {
            filePath = new File("src/main/java/data");
        }

        File[] filesList = filePath.listFiles();


        if(filesList != null) {
            for(File file : filesList) {
                if(file.isDirectory()) {
                    planListView.getItems().add(file.getName());
                }
            }
        }

        if(planListView.getItems().size() != 0) {
            studAuswaehlen.setVisible(true);
            studAuswaehlen.setDisable(false);

            studErstellen.setVisible(false);
            studErstellen.setDisable(true);
        } else {
            studAuswaehlen.setVisible(false);
            studAuswaehlen.setDisable(true);

            studErstellen.setVisible(true);
            studErstellen.setDisable(false);
        }

        //planListView set onclick seletected item
        planListView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 1) {
                String selectedItem = (String) planListView.getSelectionModel().getSelectedItem();

                try {
                    ObjectMapper objectMapper = new ObjectMapper();

                    ObjectMapper modulMapper = new ObjectMapper();

                    String jsonString;
                    String modulListString;

                    if(System.getProperty("os.name").toLowerCase().contains("windows")) {
                        jsonString = new String(Files.readAllBytes(Paths.get("..\\SPAsS\\src\\main\\java\\data\\"+ selectedItem +"\\settings.json")));
                        modulListString = new String(Files.readAllBytes(Paths.get("..\\SPAsS\\src\\main\\java\\data\\"+ selectedItem +"\\modulListe.json")));
                    } else {
                        jsonString = new String(Files.readAllBytes(Paths.get("src/main/java/data/"+ selectedItem + "/settings.json")));
                        modulListString = new String(Files.readAllBytes(Paths.get("src/main/java/data/"+ selectedItem + "/modulListe.json")));
                    }

                    List<Modul> listModules = objectMapper.readValue(modulListString, new TypeReference<>() {
                    });

                    settings = objectMapper.readValue(jsonString, Settings.class);
                    objectMapper.readValue(jsonString, Settings.class);

                    settings.modulListeDomain.setModulList(new ModulListe(listModules));

                    newUser = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                changeScene(newUser);
            }
        });

        createPlanBtn.setOnMouseClicked(event -> {
            studAuswaehlen.setVisible(false);
            studAuswaehlen.setDisable(true);
            studErstellen.setVisible(true);
            studErstellen.setDisable(false);
        });


        studChoiceBox.setItems(FXCollections.observableArrayList("Medieninformatik"));
        studChoiceBox.getSelectionModel().selectFirst();


        createPlanWeiter.setOnMouseClicked(event -> {

            settings.setSTUD_NAME(nameInput.getText());

            changeScene(newUser);

        });

    }

    /**
     * Methode um die Szene auf die Main.fxml zu wechseln.
     * @param newUser settings.newUser wird mitgegeben, um zu erkennen, ob es sich um einen neuen oder bestehenden Studienplan handelt.
     */

    public void changeScene(boolean newUser) {
        try {
            FXMLLoader mainLoader = new FXMLLoader(App.class.getResource("main.fxml"));

            MainUI mainController = new MainUI(this.settings, newUser);
            mainLoader.setController(mainController);

            Parent mainRoot = mainLoader.load();
            Scene mainScene = new Scene(mainRoot);

            Stage window = (Stage) studAuswaehlen.getScene().getWindow();
            window.setScene(mainScene);
            window.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // register change on the start ui

    }

}
