# MarketDiffusion

A JavaFX application that simulates market diffusion processes using the Bass diffusion model with geographic visualization.

## Features

- **Bass Diffusion Model**: Simulates market adoption using innovation (p) and imitation (q) coefficients
- **Geographic Visualization**: Interactive map rendering with country selection (UK, USA, China, Australia)
- **Real-time Animation**: Watch diffusion spread across populations with configurable speed
- **Distance-based Adoption**: New adopters selected based on proximity to existing adopters
- **Interactive Controls**: Parameter sliders, start/stop/reset buttons, and progress tracking

## Requirements

- Java 11 or higher
- Maven 3.6+

## Building and Running

### Compile the project:
```bash
mvn clean compile
```

### Run the application:
```bash
mvn clean javafx:run
```

### Package as JAR:
```bash
mvn clean package
```

## Usage

1. Launch the application
2. Select a country from the dropdown menu
3. Adjust simulation parameters:
   - **p**: Innovation coefficient (0.0-1.0)
   - **q**: Imitation coefficient (0.0-1.0)
   - **k**: Number of initial adopters
   - **Speed**: Animation delay in milliseconds
   - **Points**: Population size
4. Click "Start Simulation" to begin the diffusion animation
5. Use "Stop" to pause and "Reset" to restart

## Architecture

The application follows JavaFX MVC pattern with service-based architecture:

- **Model**: `Person`, `SimulationParameters`, `SimulationResult`
- **Services**: Diffusion simulation, geographic data, map rendering
- **Controller**: `PrimaryController` manages UI and coordinates services
- **View**: FXML-based interface with canvas visualization

## Dependencies

- JavaFX 21 (Controls, FXML)
- Jackson 2.17.2 (JSON processing)
- JUnit 5 (Testing)