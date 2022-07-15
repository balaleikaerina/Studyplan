package gui;


import app.module.Modul;
import domain.main.ModulListeDomain;
import gui.components.ModulBox;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Die ModulListeUI wird in der MainUI included.
 */

public class ModulListeUI implements PropertyChangeListener {

    private ModulListeDomain modulListeDomain;

    private ModulBox modulBox;

    private List<Modul> shownModule = new ArrayList<Modul>();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String EDIT_MODULE_ACTUATION = "modulbox.edit";

    @FXML
    FlowPane flowPane;

    @FXML
    HBox filterHBox;

    @FXML
    MenuButton filterTyp;

    @FXML
    CheckBox filterInformatik, filterGestaltung, filterMathematik, filterFachuebergreifend;

    @FXML
    MenuButton filterFS;
    @FXML
    CheckBox filterFS1, filterFS2, filterFS3, filterFS4, filterFS5, filterFS6, filterFS7;


    TextField searchField;

    /**
     * Constructor für die ModulListeUI, ModulListeDomain wird übergeben.
     * @param modulListeDomain ModulListeDomain
     * @throws IOException IOException
     */

    public ModulListeUI(ModulListeDomain modulListeDomain) throws IOException {
        this.modulListeDomain = modulListeDomain;
        modulListeDomain.addPropertyChangeListener(this);

    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    /**
     * Initialisierung der ModulListeUI
     */

    @FXML
    public void initialize() {

        searchField = new TextField();
        filterHBox.getChildren().addAll(searchField);
        searchField.getStyleClass().add("searchField");


        filterInformatik.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterGestaltung.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterMathematik.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFachuebergreifend.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS1.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS2.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS3.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS4.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS5.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS6.setOnAction(event -> {
            updateModulListeFilter();
        });
        filterFS7.setOnAction(event -> {
            updateModulListeFilter();
        });



        /*
        * Hier kann über eine Eingabe die ModulListe gefiltert werden.
        */

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateModulListeFilter();
            if (newValue.isEmpty()) {
                flowPane.getChildren().clear();
                shownModule.forEach(m -> {
                   displayModule(m);

                });
            } else {
                flowPane.getChildren().clear();
                for (Modul modul : shownModule) {
                    if (modul.getName().toLowerCase().contains(newValue.toLowerCase())) {
                        displayModule(modul);
                    }
                }
            }
        });


        /*
        * Für jedes bestehende Modul gibt es ein onDragDetected Event
        */

        modulListeDomain.forEach(m -> {
            shownModule.add(m);
            ModulBox modulBox1 = new ModulBox(m.getName(), m.getCp(), m.getModulNummer(), m.getAbhaengigkeiten(), m.isBestanden(), m);
            modulBox1.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Dragboard dragboard = modulBox1.startDragAndDrop(TransferMode.COPY);
                    dragboard.setDragView(modulBox1.snapshot(null, null));
                    ClipboardContent content = new ClipboardContent();
                    content.putString(Integer.toString(modulBox1.getModulNummer()));
                    dragboard.setContent(content);
                    event.consume();
                }
            });

            modulBox1.setOnMouseClicked(event -> {
                if(event.getButton() == MouseButton.SECONDARY) {
                    this.pcs.firePropertyChange(EDIT_MODULE_ACTUATION, null, modulBox1.getModulNummer());
                }
            });

            flowPane.getChildren().add(modulBox1);

        });

        }

    public ModulListeDomain getModulListeDomain() {
        return modulListeDomain;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    //Filtert je nachdem, was in der UI ausgewählt worden ist.
    public void updateModulListeFilter(){

        shownModule.clear();
        if(!filterInformatik.isSelected() && !filterGestaltung.isSelected() && !filterMathematik.isSelected() && !filterFachuebergreifend.isSelected()){
            for (Modul modul : modulListeDomain){
                shownModule.add(modul);
            }
        }else{
            shownModule.clear();
            for (Modul modul : modulListeDomain){
                char modulTyp = Integer.toString(modul.getModulNummer()).charAt(1);
                if (modulTyp == '1' && filterInformatik.isSelected()){
                    shownModule.add(modul);
                }
                if (modulTyp == '2' && filterGestaltung.isSelected()){
                    shownModule.add(modul);
                }
                if (modulTyp == '3' && filterMathematik.isSelected()){
                    shownModule.add(modul);
                }
                if (modulTyp == '4' && filterFachuebergreifend.isSelected()){
                    shownModule.add(modul);
                }
            }

        }
        if(filterFS1.isSelected() || filterFS2.isSelected() || filterFS3.isSelected() || filterFS4.isSelected() || filterFS5.isSelected() || filterFS6.isSelected() || filterFS7.isSelected()){
           ArrayList<Modul> tempModulListe = new ArrayList<>();
            for(Modul modul : shownModule){
                char fachSemester = Integer.toString(modul.getModulNummer()).charAt(0);
                if(filterFS1.isSelected() && fachSemester == '1'){
                    tempModulListe.add(modul);
                }
                if(filterFS2.isSelected() && fachSemester == '2'){
                    tempModulListe.add(modul);
                }
                if(filterFS3.isSelected() && fachSemester == '3'){
                    tempModulListe.add(modul);
                }
                if(filterFS4.isSelected() && fachSemester == '4'){
                    tempModulListe.add(modul);
                }
                if(filterFS5.isSelected() && fachSemester == '5'){
                    tempModulListe.add(modul);
                }
                if(filterFS6.isSelected() && fachSemester == '6'){
                    tempModulListe.add(modul);
                }
                if(filterFS7.isSelected() && fachSemester == '7'){
                    tempModulListe.add(modul);
                }

            }
            shownModule = tempModulListe;
        }

        flowPane.getChildren().clear();
        for (Modul modul: shownModule){
            displayModule(modul);
        }

    }

    //display module in UI and add Drag/Drop functionality
    public void displayModule(Modul modul){
            ModulBox modulBox1;
            flowPane.getChildren().add(modulBox1 = new ModulBox(modul.getName(), modul.getCp(), modul.getModulNummer(), modul.getAbhaengigkeiten(), modul.isBestanden(), modul));
            modulBox1.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Dragboard dragboard = modulBox1.startDragAndDrop(TransferMode.COPY);
                    dragboard.setDragView(modulBox1.snapshot(null, null));
                    ClipboardContent content = new ClipboardContent();
                    content.putString(Integer.toString(modulBox1.getModulNummer()));
                    dragboard.setContent(content);
                    event.consume();
                }
            });


    }
}
