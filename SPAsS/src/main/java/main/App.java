package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            //Group root = new Group();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("start.fxml")));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("../libs/css/application.css")).toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                try {
                    logout();
                } catch (Exception e) {
                    System.out.println("Exception occurred when closing down: " + e);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout() throws Exception {
        this.stop();
        Platform.exit();
        System.exit(0);
    }

}


