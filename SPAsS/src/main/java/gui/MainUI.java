package gui;

import static app.stundenplan.StundenplanVerwaltung.SPEICHERN;

import app.Settings;
import app.module.Modul;
import app.module.SemesterListe;
import app.stundenplan.Stundenplan;
import app.stundenplan.StundenplanVerwaltung;
import domain.main.ModulListeDomain;
import exceptions.CpExceeds40Exception;
import exceptions.ModulExistiertNichtException;
import exceptions.ModulNichtBelegbarException;
import gui.components.EditModuleBox;
import gui.components.ListHBox;
import gui.components.ModulBox;
import gui.components.SettingsBox;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import main.App;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class MainUI extends Settings implements PropertyChangeListener {

  private Stundenplan stundenplan;

  private Settings settings;

  private StundenplanVerwaltung stundenplanVerwaltung;

  private SettingsBox settingsBox;

  private EditModuleBox editModuleBox;

  @FXML
  private Pane stundenPlanAnsicht, modulAnsicht, topBar, settingsInputPain,
      settingsPane, editPaneBackground, editModulePane, errorPaneBackground,
      errorModulePane;

  @FXML private BorderPane popUpBackground;

  @FXML private Label titel, userStudienplanLabel, errorMsgLabel;

  @FXML
  private Button buttonTest, moduleAddButton, semesterAddButton,
      semesterRemoveButton, settingsButton, settingSaveButton, print,
      editModuleSaveButton, errorOkayButton, durchschnittsNote, saveStudienplan;

  @FXML private ImageView moduleAddIcon, semesterAddIcon, saveIcon;

  @FXML private ListView semesterListView;

  @FXML private ListHBox listHBox;

  @FXML private VBox studViewVBox;

  private boolean newUser;

  /**
   * Der Constructor der MainUI.
   * @param settings Die Global Settings werden per Dependency Injection
   *     übergeben.
   * @param newUser Boolean wert um zu erkennen, ob es sich um einen neuen User
   *     handelt.
   * Hier werden außerdem die propertyChangeListener gesetzt.
   */

  public MainUI(Settings settings, boolean newUser) {
    this.settings = settings;
    this.settings.addPropertyChangeListener(this);
    settings.modulListeDomain.addPropertyChangeListener(this);
    stundenplanVerwaltung = new StundenplanVerwaltung(settings, newUser);
    stundenplanVerwaltung.addPropertyChangeListener(this);
    this.newUser = newUser;
  }

  /**
   * Methode um in der UI ein neues Semester hinzuzufügen.
   * Die einzelnen Semester sind in der UI in einer ListView eingetragen
   *  → In dieser liegt die listHBox
   *  → Die listHBox besteht aus einer Pane die, die Semesterzahl anzeigt und
   * einer ListView, in welcher die Module angezeigt werden.
   * @param listHBox Anzeige der Semester
   */

  public void addSemester(ListHBox listHBox) {

    listHBox.setOnDragDropped(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        String modulNummerStr = dragboard.getString();
        int semesterToAddTo = listHBox.getSemesterZahl();
        try {
          Modul modulToAdd = settings.modulListeDomain.getModulFromList(
              Integer.parseInt(dragboard.getString()));
          try {
            stundenplanVerwaltung.removeModul(modulToAdd);
            stundenplanVerwaltung.addModul(semesterToAddTo, modulToAdd);

          } catch (CpExceeds40Exception cpException) {
            String errStr = cpException.getMessage();
            errorPaneBackground.setVisible(true);
            errorPaneBackground.setDisable(false);
            errorModulePane.setVisible(true);
            errorModulePane.setDisable(false);
            errorMsgLabel.setText(errStr);

          } catch (ModulNichtBelegbarException belegbarException) {
            String errStr = belegbarException.getMessage();
            errorPaneBackground.setVisible(true);
            errorPaneBackground.setDisable(false);
            errorModulePane.setVisible(true);
            errorModulePane.setDisable(false);
            errorMsgLabel.setText(errStr);
          }
        } catch (ModulExistiertNichtException e) {
          throw new RuntimeException(e);
        }
      }
    });

    listHBox.setOnDragOver((dragEvent) -> {
      if (dragEvent.getGestureSource() != listHBox &&
              dragEvent.getDragboard().hasContent(ModulBox.modulBoxFormat) ||
          dragEvent.getDragboard().hasString()) {
        dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
      }
    });

    this.semesterListView.getItems().add(0, listHBox);
  }

  /*
   * JavaFX Initialisierung, hier werden Standardeinstellungen gesetzt und auf
   * ActionEvents gehört.
   */
  @FXML
  public void initialize() {

    saveStudienplan.setOnMouseClicked(event -> {
      try {
        stundenplanVerwaltung.saveStundenplan();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    /*
     * User befehl, den Studienplan als PDF zu drucken.
     */

    print.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {

        stundenPlanAnsicht.setLayoutX(0);
        stundenPlanAnsicht.setLayoutY(0);
        stundenPlanAnsicht.setMinWidth(1750);
        stundenPlanAnsicht.setMinHeight(900);
        studViewVBox.setMinWidth(1750);
        studViewVBox.setMinHeight(900);
        semesterListView.setMinWidth(1750);
        semesterListView.setMinHeight(900);

        /*
         * Hier wird die PDF-Datei erstellt.
         * Wir benutzen hier die pdfbox library, sowie ein Element aus der
         * JavaFX Swing library.
         */

        // Erstellen von einem temp. PNG der stundenPlanAnsicht (Pane)
        WritableImage nodeshot =
            stundenPlanAnsicht.snapshot(new SnapshotParameters(), null);
        File file = new File("Stundenplan.png");

        try {
          ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
        } catch (IOException e) {
        }

        // PDFBOX setzt die PNG auf ein PDF Dokument

        PDDocument doc = new PDDocument();
        // 1300 | 800
        // Hier können die Dimensionen des Inhalts festgelegt werden.
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A3.getHeight(),
                                                 PDRectangle.A3.getWidth()));
        PDImageXObject pdimage;
        PDPageContentStream content;

        try {
          pdimage = PDImageXObject.createFromFile("Stundenplan.png", doc);
          content = new PDPageContentStream(doc, page);
          content.drawImage(pdimage, 0, 0, 1569, 772);
          content.close();
          doc.addPage(page);

          // Damit der ganze Studienplan als PDF gedruckt werden kann, müssen
          // wir das Layout kurz größer machen und danach wieder zurücksetzen.
          stundenPlanAnsicht.setLayoutX(128);
          stundenPlanAnsicht.setLayoutY(162);
          stundenPlanAnsicht.setMinWidth(1569);
          stundenPlanAnsicht.setMinHeight(772);
          studViewVBox.setMinWidth(1549);
          studViewVBox.setMinHeight(742);
          semesterListView.setMinWidth(1549);
          semesterListView.setMinHeight(742);

          FileChooser fileChooser = new FileChooser();
          FileChooser.ExtensionFilter extFilter =
              new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
          fileChooser.getExtensionFilters().add(extFilter);
          File file2 = fileChooser.showSaveDialog(
              ((Node)event.getSource()).getScene().getWindow());

          doc.save(file2);
          doc.close();
          // Die temp. PNG wieder löschen
          file.delete();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    });

    userStudienplanLabel.setText(settings.getSTUD_NAME() + "'s Studienplan");

    semesterListView.getStyleClass().add("semester-list-view-main");

    // Fügt die Semester dem GUI hinzu.
    for (int i = 0; i < stundenplanVerwaltung.getStundenplan().getMaxSemester();
         i++) {
      ListHBox listHBox1 = new ListHBox(i + 1, settings.modulListeDomain);
      addSemester(listHBox1);
    }

    // Öffnet die ModulAnsicht setOnMouseClicked auf das ModulIcon
    moduleAddButton.setOnMouseClicked(event -> {
      if (modulAnsicht.isVisible()) {
        modulAnsicht.setVisible(false);
        studViewVBox.setPrefHeight(studViewVBox.getHeight() + 150);
        stundenPlanAnsicht.setLayoutY(stundenPlanAnsicht.getLayoutY() - 150);
      } else {
        modulAnsicht.setVisible(true);
        studViewVBox.setPrefHeight(studViewVBox.getHeight() - 150);
        stundenPlanAnsicht.setLayoutY(stundenPlanAnsicht.getLayoutY() + 150);
      }
    });

    semesterAddButton.setOnMouseClicked(
        event -> { stundenplanVerwaltung.addSemester(); });

    semesterRemoveButton.setOnMouseClicked(
        event -> { stundenplanVerwaltung.removeSemester(); });

    settingsButton.setOnMouseClicked(event -> {
      if (popUpBackground.isVisible()) {

      } else {

        popUpBackground.setVisible(true);
        popUpBackground.setDisable(false);
        settingsPane.setVisible(true);
        settingsPane.setDisable(false);
      }
    });

    popUpBackground.setOnMouseClicked(event -> {
      settingsPane.setVisible(false);
      settingsPane.setDisable(true);
      popUpBackground.setVisible(false);
      popUpBackground.setDisable(true);
    });

    // Settings Pane
    settingsBox = new SettingsBox(settings.getSTUD_NAME(), getMAX_CP());

    //Öffnet Settings
    settingSaveButton.setOnMouseClicked(event -> {
      settings.setSTUD_NAME(
          ((TextField)settingsBox.getChildren().get(1)).getText());
      settings.setMAX_CP(Integer.parseInt(
          ((TextField)settingsBox.getChildren().get(3)).getText()));
      popUpBackground.setVisible(false);
      popUpBackground.setDisable(true);
      settingsPane.setVisible(false);
      settingsPane.setDisable(true);
    });

    settingsInputPain.getChildren().addAll(settingsBox);

    // Edit Modules Pane
    editModuleSaveButton.setOnMouseClicked(event -> {
      Modul newMod = editModuleBox.getModFromInput();
      settings.modulListeDomain.editModul(newMod.getModulNummer(), newMod);
      editPaneBackground.setVisible(false);
      editPaneBackground.setDisable(true);
    });

    // Error Button
    errorOkayButton.setOnMouseClicked(event -> {
      errorPaneBackground.setVisible(false);
      errorPaneBackground.setDisable(true);
      errorModulePane.setVisible(false);
      errorModulePane.setDisable(true);
    });

    // Hier wird die ModulList.fxml in unsere MainUI geladen.

    try {
      FXMLLoader modulListLoader =
          new FXMLLoader(App.class.getResource("modulList.fxml"));
      ModulListeUI modulListController =
          new ModulListeUI(settings.modulListeDomain);
      modulListLoader.setController(modulListController);
      Pane modulListRoot = modulListLoader.load();
      modulListController.addPropertyChangeListener(this);
      modulAnsicht.getChildren().add(modulListRoot);
    } catch (IOException ioEx) {
      ioEx.printStackTrace();
    }

    Stundenplan stPlan = stundenplanVerwaltung.getStundenplan();
    for (int i = 1; i <= stPlan.getMaxSemester(); i++) {
      SemesterListe sList = stPlan.getSemesterListe(i);
      List<Modul> semMods = sList.getSemesterModule();
      for (Modul m : semMods) {
        int semesterToAddTo = semesterListView.getItems().size() - (i);
        ListHBox semesterListe =
            (ListHBox)semesterListView.getItems().get(semesterToAddTo);
        semesterListe.addModuleToView(m.getModulNummer());
      }
    }
    String durchString =
        String.valueOf(stundenplanVerwaltung.getDurchschnitt());
    durchschnittsNote.setText(durchString);
  }

  /**
   * Hier reagieren wir auf änderungen im Backend in der UI
   * @param event A PropertyChangeEvent object describing the event source
   *          and the property that has changed.
   */

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getPropertyName().equals(Settings.STUD_NAME_CHANGE)) {
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      settings.setSTUD_NAME(event.getNewValue().toString());
      userStudienplanLabel.setText(settings.getSTUD_NAME() + "'s Studienplan");
    } else if (event.getPropertyName().equals(
                   (Stundenplan.ADD_SEMESTER_EVENT))) {
      addSemester(new ListHBox(semesterListView.getItems().size() + 1,
                               settings.modulListeDomain));
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      semesterListView.scrollTo(0);
    } else if (event.getPropertyName().equals(
                   (Stundenplan.REMOVE_SEMESTER_EVENT))) {
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      semesterListView.getItems().remove(0);
      semesterListView.scrollTo(0);
    } else if (event.getPropertyName().equals(
                   (Stundenplan.REMOVE_MODULE_EVENT))) {
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      List<ListHBox> semesterListen = semesterListView.getItems();
      Modul modulToRemove = (Modul)event.getOldValue();
      for (ListHBox semListe : semesterListen) {
        semListe.removeModuleFromView(modulToRemove);
      }
    } else if (event.getPropertyName().equals(Stundenplan.ADD_MODULE_EVENT)) {
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      Modul modulToAdd = (Modul)event.getNewValue();
      int semesterToAddTo =
          semesterListView.getItems().size() - ((int)event.getOldValue());
      ListHBox semesterListe =
          (ListHBox)semesterListView.getItems().get(semesterToAddTo);
      semesterListe.addModuleToView(modulToAdd.getModulNummer());
      String durchString =
          String.valueOf(stundenplanVerwaltung.getDurchschnitt());
      durchschnittsNote.setText(durchString);
    } else if (event.getPropertyName().equals(
                   ModulListeUI.EDIT_MODULE_ACTUATION)) {
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      editModuleBox = new EditModuleBox((int)event.getNewValue(),
                                        settings.modulListeDomain);
      editModulePane.getChildren().add(editModuleBox);
      editPaneBackground.setVisible(true);
      editPaneBackground.setDisable(false);
      editModulePane.setVisible(true);
      editModulePane.setDisable(false);
    } else if (event.getPropertyName().equals(
                   ModulListeDomain.NOTE_CHANGED_EVENT)) {
      saveIcon.setImage(new Image("libs/images/saveIcon.png"));
      String durchString =
          String.valueOf(stundenplanVerwaltung.getDurchschnitt());
      durchschnittsNote.setText(durchString);

    } else if (event.getPropertyName().equals(SPEICHERN)) {
      saveIcon.setImage(new Image("libs/images/saveIconChecked.png"));
    } else {
      throw new IllegalArgumentException("Nicht bekanntes Event " + event);
    }
  }
}
