package uk.ac.ucl.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for logging diffusion simulation data to CSV files.
 * Creates timestamped CSV files with real-time simulation data.
 */
public class CsvLoggingService {

    private static final String LOGS_DIRECTORY = "logs";
    private static final String CSV_HEADER = "TimeStep,NewAdopters,TotalAdopted,TotalPopulation,AdoptionPercentage,SimulationTime";
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private FileWriter csvWriter;
    private String currentFilePath;
    private boolean isLogging = false;

    /**
     * Starts logging for a new simulation run.
     *
     * @param simulationName The name/identifier for this simulation (e.g., country name)
     * @throws IOException if file creation or writing fails
     */
    public void startLogging(String simulationName) throws IOException {
        if (isLogging) {
            stopLogging();
        }

        // Create logs directory if it doesn't exist
        Path logsDir = Paths.get(LOGS_DIRECTORY);
        if (!Files.exists(logsDir)) {
            Files.createDirectories(logsDir);
        }

        // Generate timestamped filename
        String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
        String filename = String.format("diffusion_%s_%s.csv", sanitizeFilename(simulationName), timestamp);
        currentFilePath = Paths.get(LOGS_DIRECTORY, filename).toString();

        try {
            csvWriter = new FileWriter(currentFilePath);
            csvWriter.write(CSV_HEADER + System.lineSeparator());
            csvWriter.flush(); // Ensure header is written immediately
            isLogging = true;

            System.out.println("CSV logging started: " + currentFilePath);
        } catch (IOException e) {
            System.err.println("Error starting CSV logging: " + e.getMessage());
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException closeException) {
                    System.err.println("Error closing CSV writer: " + closeException.getMessage());
                }
            }
            throw e;
        }
    }

    /**
     * Logs a single diffusion step to the CSV file.
     *
     * @param timeStep The current time step
     * @param newAdopters Number of new adopters in this step
     * @param totalAdopted Total number of adopters
     * @param totalPopulation Total population size
     * @param adoptionPercentage Percentage of population that has adopted
     */
    public void logDiffusionStep(int timeStep, int newAdopters, int totalAdopted,
                                int totalPopulation, double adoptionPercentage) {
        if (!isLogging || csvWriter == null) {
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String csvLine = String.format("%d,%d,%d,%d,%.1f,%s",
                    timeStep, newAdopters, totalAdopted, totalPopulation, adoptionPercentage, timestamp);

            csvWriter.write(csvLine + System.lineSeparator());
            csvWriter.flush(); // Ensure data is written immediately for real-time updates

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
            // Continue simulation even if logging fails
        }
    }

    /**
     * Stops the current logging session and closes the CSV file.
     * Displays the path of the saved file.
     */
    public void stopLogging() {
        if (!isLogging || csvWriter == null) {
            return;
        }

        try {
            csvWriter.close();
            System.out.println("CSV logging stopped. Data saved to: " + currentFilePath);
        } catch (IOException e) {
            System.err.println("Error closing CSV file: " + e.getMessage());
        } finally {
            isLogging = false;
            csvWriter = null;
            currentFilePath = null;
        }
    }

    /**
     * Checks if logging is currently active.
     *
     * @return true if logging is active, false otherwise
     */
    public boolean isLogging() {
        return isLogging;
    }

    /**
     * Gets the current CSV file path if logging is active.
     *
     * @return the current file path, or null if not logging
     */
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    /**
     * Sanitizes a filename by removing or replacing invalid characters.
     *
     * @param filename the original filename
     * @return sanitized filename safe for file system
     */
    private String sanitizeFilename(String filename) {
        if (filename == null) {
            return "unknown";
        }
        // Replace invalid filename characters with underscores
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_").toLowerCase();
    }
}