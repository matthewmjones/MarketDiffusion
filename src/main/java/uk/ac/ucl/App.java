package uk.ac.ucl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the MarketDiffusion JavaFX application.
 * Initializes and starts the primary user interface for market diffusion simulation.
 */
public class App extends Application {

    private static Scene scene;

    /**
     * Starts the JavaFX application by loading the primary FXML and displaying the stage.
     *
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the root of the current scene to load a different FXML file.
     *
     * @param fxml the name of the FXML file (without .fxml extension)
     * @throws IOException if the FXML file cannot be loaded
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file from the resources directory.
     *
     * @param fxml the name of the FXML file (without .fxml extension)
     * @return the loaded Parent node from the FXML file
     * @throws IOException if the FXML file cannot be found or loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}