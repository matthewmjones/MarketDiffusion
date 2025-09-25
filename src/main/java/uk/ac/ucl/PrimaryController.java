package uk.ac.ucl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.util.Duration;

import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.RenderingConfig;
import uk.ac.ucl.model.SimulationParameters;
import uk.ac.ucl.model.SimulationResult;
import uk.ac.ucl.service.DiffusionSimulationService;
import uk.ac.ucl.service.DiffusionSimulationServiceImpl;
import uk.ac.ucl.service.GeographicDataService;
import uk.ac.ucl.service.GeographicDataServiceImpl;
import uk.ac.ucl.service.MapRenderingService;
import uk.ac.ucl.service.MapRenderingServiceImpl;
import uk.ac.ucl.util.BoundingBox;

/**
 * Primary controller for the MarketDiffusion JavaFX application.
 * Manages the user interface, coordinates diffusion simulation services,
 * and handles map rendering with real-time visualization.
 */
public class PrimaryController implements Initializable {

    @FXML
    private ChoiceBox<String> countryChoice;

    @FXML
    private Slider pSlider;

    @FXML
    private Slider qSlider;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button resetButton;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private Slider numPoints;

    @FXML
    private Slider kSlider;

    @FXML
    private Slider speedSlider;

    private final GeographicDataService geographicDataService = new GeographicDataServiceImpl();
    private final DiffusionSimulationService diffusionSimulationService = new DiffusionSimulationServiceImpl();
    private final MapRenderingService mapRenderingService = new MapRenderingServiceImpl();
    private final RenderingConfig renderingConfig = RenderingConfig.getDefault();

    private GeoJsonData currentGeoData;
    private List<Person> people = new ArrayList<>();
    private Random random = new Random();
    private Timeline diffusionAnimation;
    private int currentTimeStep = 0;
    private boolean animationRunning = false;
    private BoundingBox currentBounds;
    private static final double MAP_PADDING = 20.0;

    /**
     * Initializes the controller, setting up UI components and their event handlers.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryChoice.getItems().addAll("UK", "Australia", "USA", "China");
        countryChoice.setValue("UK");

        countryChoice.setOnAction(e -> drawSelectedCountry());

        pSlider.setMin(0);
        pSlider.setMax(0.2);
        pSlider.setMajorTickUnit(0.05);
        pSlider.setMinorTickCount(9);
        pSlider.setSnapToTicks(true);
        pSlider.setShowTickMarks(true);
        pSlider.setShowTickLabels(true);
        pSlider.setValue(0.1);

        qSlider.setMin(0);
        qSlider.setMax(0.2);
        qSlider.setMajorTickUnit(0.05);
        qSlider.setMinorTickCount(9);
        qSlider.setSnapToTicks(true);
        qSlider.setShowTickMarks(true);
        qSlider.setShowTickLabels(true);
        qSlider.setValue(0.01);

        numPoints.setMin(1000);
        numPoints.setMax(10000);
        numPoints.setMajorTickUnit(1000);
        numPoints.setMinorTickCount(1);
        numPoints.setSnapToTicks(true);
        numPoints.setShowTickMarks(true);
        numPoints.setShowTickLabels(true);
        numPoints.setValue(4000);
        numPoints.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && !animationRunning) {
                generateRandomPeople();
            }
        });

        kSlider.setMin(1);
        kSlider.setMax(50);
        kSlider.setMajorTickUnit(10);
        kSlider.setMinorTickCount(9);
        kSlider.setSnapToTicks(true);
        kSlider.setShowTickMarks(true);
        kSlider.setShowTickLabels(true);
        kSlider.setValue(5);

        speedSlider.setMin(100);
        speedSlider.setMax(2000);
        speedSlider.setMajorTickUnit(500);
        speedSlider.setMinorTickCount(4);
        speedSlider.setSnapToTicks(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setValue(500);

        startButton.setOnAction(e -> startDiffusionAnimation());
        stopButton.setOnAction(e -> stopDiffusionAnimation());
        resetButton.setOnAction(e -> resetDiffusion());

        countryChoice.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (animationRunning) {
                stopDiffusionAnimation();
            }
            drawSelectedCountry();
        });

        mapCanvas.widthProperty().bind(mapCanvas.getParent().layoutBoundsProperty().map(bounds -> bounds.getWidth()));
        mapCanvas.heightProperty().bind(mapCanvas.getParent().layoutBoundsProperty().map(bounds -> bounds.getHeight()));

        mapCanvas.widthProperty().addListener((obs, oldVal, newVal) -> redrawCurrentCountry());
        mapCanvas.heightProperty().addListener((obs, oldVal, newVal) -> redrawCurrentCountry());

        drawSelectedCountry();
    }

    /**
     * Starts the diffusion animation with current parameter settings.
     * Initializes adopters and begins the Timeline-based animation loop.
     */
    private void startDiffusionAnimation() {
        if (animationRunning || people.isEmpty()) return;

        animationRunning = true;
        startButton.setDisable(true);
        stopButton.setDisable(false);
        resetButton.setDisable(true);
        currentTimeStep = 0;

        resetAdoptionStates();
        initializeAdopters();

        double animationDelay = speedSlider.getValue();
        diffusionAnimation = new Timeline(new KeyFrame(
            Duration.millis(animationDelay),
            e -> performDiffusionStep()
        ));
        diffusionAnimation.setCycleCount(Timeline.INDEFINITE);
        diffusionAnimation.play();
    }

    /**
     * Stops the currently running diffusion animation and updates UI state.
     */
    private void stopDiffusionAnimation() {
        if (!animationRunning) return;

        animationRunning = false;
        if (diffusionAnimation != null) {
            diffusionAnimation.stop();
        }
        startButton.setDisable(false);
        stopButton.setDisable(true);
        resetButton.setDisable(false);
    }

    /**
     * Resets the diffusion simulation to its initial state.
     */
    private void resetDiffusion() {
        stopDiffusionAnimation();
        resetAdoptionStates();
        currentTimeStep = 0;
        if (currentGeoData != null) {
            renderCurrentMap();
        }
        System.out.println("Diffusion reset - Time step: 0");
    }

    private void resetAdoptionStates() {
        diffusionSimulationService.resetAdoptionStates(people);
    }

    /**
     * Randomly selects initial adopters based on the k parameter and renders the map.
     */
    private void initializeAdopters() {
        if (people.isEmpty()) return;

        int k = (int) kSlider.getValue();
        diffusionSimulationService.initializeAdopters(people, k);

        if (currentGeoData != null) {
            renderCurrentMap();
        }

        int adoptedCount = diffusionSimulationService.countAdopters(people);
        double adoptionPercentage = (adoptedCount * 100.0) / people.size();
        System.out.printf("Initialization complete - Time step: %d, Adopted: %d/%d (%.1f%%)%n",
                currentTimeStep, adoptedCount, people.size(), adoptionPercentage);
    }

    /**
     * Executes a single diffusion step using the Bass model and updates the visualization.
     * Stops animation when diffusion is complete.
     */
    private void performDiffusionStep() {
        currentTimeStep++;

        double p = pSlider.getValue();
        double q = qSlider.getValue();
        int k = (int) kSlider.getValue();
        SimulationParameters params = new SimulationParameters(p, q, k);

        SimulationResult result = diffusionSimulationService.performDiffusionStep(people, params, currentTimeStep);

        if (currentGeoData != null) {
            renderCurrentMap();
        }

        System.out.printf("Time step: %d, New adopters: %d, Total adopted: %d/%d (%.1f%%)%n",
                result.getTimeStep(), result.getNewAdopters(), result.getTotalAdopted(),
                result.getTotalPopulation(), result.getAdoptionPercentage());

        if (result.isComplete()) {
            System.out.println("Diffusion complete - All people have adopted!");
            stopDiffusionAnimation();
        }
    }


    /**
     * Loads and renders the selected country's geographic data,
     * generating a new population of people within the country boundaries.
     */
    private void drawSelectedCountry() {
        String selectedCountry = countryChoice.getValue();
        if (selectedCountry == null) return;

        try {
            currentGeoData = geographicDataService.loadCountryData(selectedCountry);
            currentBounds = geographicDataService.calculateBoundingBox(currentGeoData);
            generateRandomPeople();
            renderCurrentMap();

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading country data: " + e.getMessage());
        }
    }


    private void redrawCurrentCountry() {
        renderCurrentMap();
    }

    private void renderCurrentMap() {
        if (currentGeoData != null && currentBounds != null) {
            double canvasWidth = mapCanvas.getWidth();
            double canvasHeight = mapCanvas.getHeight();

            mapRenderingService.renderCountryMap(
                mapCanvas.getGraphicsContext2D(),
                currentGeoData,
                currentBounds,
                canvasWidth,
                canvasHeight,
                renderingConfig
            );

            mapRenderingService.renderPopulation(
                mapCanvas.getGraphicsContext2D(),
                people,
                currentBounds,
                canvasWidth,
                canvasHeight,
                renderingConfig
            );
        }
    }

    /**
     * Generates random population points within the current country boundaries
     * and resets their adoption states.
     */
    private void generateRandomPeople() {
        if (currentGeoData == null || currentBounds == null) return;

        int numPeople = (int) numPoints.getValue();
        people = geographicDataService.generateRandomPointsInCountry(currentGeoData, currentBounds, numPeople);

        System.out.println("Generated " + people.size() + " people");
        resetAdoptionStates();

        if (currentGeoData != null) {
            renderCurrentMap();
        }
    }

}
