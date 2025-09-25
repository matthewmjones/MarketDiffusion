package uk.ac.ucl.model;

/**
 * Immutable result of a single diffusion simulation step.
 * Contains metrics about adoption progress and completion status.
 */
public class SimulationResult {
    private final int timeStep;
    private final int newAdopters;
    private final int totalAdopted;
    private final int totalPopulation;
    private final boolean isComplete;

    /**
     * Creates a simulation result with the specified metrics.
     *
     * @param timeStep the current time step of the simulation
     * @param newAdopters number of new adopters in this step
     * @param totalAdopted total number of adopters so far
     * @param totalPopulation total population size
     * @param isComplete whether the simulation has completed
     */
    public SimulationResult(int timeStep, int newAdopters, int totalAdopted, int totalPopulation, boolean isComplete) {
        this.timeStep = timeStep;
        this.newAdopters = newAdopters;
        this.totalAdopted = totalAdopted;
        this.totalPopulation = totalPopulation;
        this.isComplete = isComplete;
    }

    public int getTimeStep() { return timeStep; }
    public int getNewAdopters() { return newAdopters; }
    public int getTotalAdopted() { return totalAdopted; }
    public int getTotalPopulation() { return totalPopulation; }
    public boolean isComplete() { return isComplete; }

    /**
     * Calculates the adoption percentage of the total population.
     *
     * @return adoption percentage (0.0-100.0)
     */
    public double getAdoptionPercentage() {
        if (totalPopulation == 0) return 0.0;
        return (totalAdopted * 100.0) / totalPopulation;
    }

    /**
     * Calculates the number of non-adopters.
     *
     * @return number of people who have not adopted
     */
    public int getNonAdopted() {
        return totalPopulation - totalAdopted;
    }

    @Override
    public String toString() {
        return String.format("SimulationResult[timeStep=%d, newAdopters=%d, totalAdopted=%d/%d (%.1f%%), complete=%s]",
                timeStep, newAdopters, totalAdopted, totalPopulation, getAdoptionPercentage(), isComplete);
    }
}