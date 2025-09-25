package uk.ac.ucl.service;

import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.SimulationParameters;
import uk.ac.ucl.model.SimulationResult;

import java.util.List;

/**
 * Service interface for Bass diffusion model simulation operations.
 * Handles initialization, step execution, and population management for market diffusion.
 */
public interface DiffusionSimulationService {

    /**
     * Randomly selects k initial adopters from the population.
     *
     * @param population the population to select from
     * @param k number of initial adopters to select
     */
    void initializeAdopters(List<Person> population, int k);

    /**
     * Executes one step of the Bass diffusion model simulation.
     *
     * @param population the current population
     * @param params simulation parameters (p, q, k)
     * @param currentTimeStep the current simulation time step
     * @return result containing adoption metrics and completion status
     */
    SimulationResult performDiffusionStep(List<Person> population, SimulationParameters params, int currentTimeStep);

    /**
     * Selects new adopters based on proximity to existing adopters.
     *
     * @param population the population to select from
     * @param newAdopters number of new adopters to select
     */
    void selectNewAdoptersByDistance(List<Person> population, int newAdopters);

    /**
     * Calculates Euclidean distance between two people.
     *
     * @param p1 first person
     * @param p2 second person
     * @return distance between the two people
     */
    double calculateEuclideanDistance(Person p1, Person p2);

    /**
     * Resets all people in the population to non-adopted state.
     *
     * @param population the population to reset
     */
    void resetAdoptionStates(List<Person> population);

    /**
     * Counts the number of adopters in the population.
     *
     * @param population the population to count
     * @return number of adopters
     */
    int countAdopters(List<Person> population);

    /**
     * Counts the number of non-adopters in the population.
     *
     * @param population the population to count
     * @return number of non-adopters
     */
    int countNonAdopters(List<Person> population);

    /**
     * Determines if the simulation has completed (all people adopted).
     *
     * @param population the population to check
     * @return true if simulation is complete
     */
    boolean isSimulationComplete(List<Person> population);

    /**
     * Sets the random seed for reproducible results.
     *
     * @param seed the random seed value
     */
    void setSeed(long seed);
}