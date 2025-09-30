package uk.ac.ucl.service;

import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.SimulationParameters;
import uk.ac.ucl.model.SimulationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Implementation of Bass diffusion model simulation with distance-based adoption.
 * Uses spatial proximity to determine which non-adopters are most likely to adopt next.
 */
public class DiffusionSimulationServiceImpl implements DiffusionSimulationService {

    private final Random random = new Random();

    @Override
    public void initializeAdopters(List<Person> population, int k) {
        if (population.isEmpty()) return;

        int actualK = Math.min(k, population.size());
        resetAdoptionStates(population);

        Collections.shuffle(population, random);
        for (int i = 0; i < actualK; i++) {
            population.get(i).setHasAdopted(true);
        }
    }

    @Override
    public SimulationResult performDiffusionStep(List<Person> population, SimulationParameters params, int currentTimeStep) {
        if (population.isEmpty()) {
            return new SimulationResult(currentTimeStep, 0, 0, 0, true);
        }

        int adoptedCount = countAdopters(population);
        int nonAdoptedCount = countNonAdopters(population);
        int totalPopulation = population.size();

        if (nonAdoptedCount == 0) {
            return new SimulationResult(currentTimeStep, 0, adoptedCount, totalPopulation, true);
        }

        // Bass diffusion components:
        // Innovators: p * N(t) - chosen randomly
        // Imitators: q * N(t) * A(t) / M - chosen by proximity
        double innovatorsFormula = params.getP() * nonAdoptedCount;
        double imitatorsFormula = params.getQ() * nonAdoptedCount * adoptedCount / (double) totalPopulation;

        int innovators = (int) Math.ceil(innovatorsFormula);
        int imitators = (int) Math.ceil(imitatorsFormula);

        // Ensure we don't exceed available non-adopters
        innovators = Math.min(innovators, nonAdoptedCount);
        imitators = Math.min(imitators, nonAdoptedCount - innovators);

        int actualNewAdopters = 0;

        // Select innovators randomly
        if (innovators > 0) {
            actualNewAdopters += selectInnovators(population, innovators);
        }

        // Select imitators by proximity to existing adopters
        if (imitators > 0) {
            actualNewAdopters += selectImitatorsByDistance(population, imitators);
        }

        int finalAdoptedCount = countAdopters(population);
        boolean isComplete = finalAdoptedCount >= totalPopulation;

        return new SimulationResult(currentTimeStep, actualNewAdopters, finalAdoptedCount, totalPopulation, isComplete);
    }

    /**
     * Selects innovators randomly from non-adopters (Bass model p component).
     *
     * @param population the population to select from
     * @param numInnovators number of innovators to select
     * @return actual number of innovators selected
     */
    private int selectInnovators(List<Person> population, int numInnovators) {
        List<Person> nonAdopters = new ArrayList<>();
        for (Person person : population) {
            if (!person.hasAdopted()) {
                nonAdopters.add(person);
            }
        }

        if (nonAdopters.isEmpty()) return 0;

        int actualInnovators = Math.min(numInnovators, nonAdopters.size());
        Collections.shuffle(nonAdopters, random);

        for (int i = 0; i < actualInnovators; i++) {
            nonAdopters.get(i).setHasAdopted(true);
        }

        return actualInnovators;
    }

    /**
     * Selects imitators based on proximity to existing adopters (Bass model q component).
     *
     * @param population the population to select from
     * @param numImitators number of imitators to select
     * @return actual number of imitators selected
     */
    private int selectImitatorsByDistance(List<Person> population, int numImitators) {
        List<Person> nonAdopters = new ArrayList<>();
        List<Person> adopters = new ArrayList<>();

        for (Person person : population) {
            if (person.hasAdopted()) {
                adopters.add(person);
            } else {
                nonAdopters.add(person);
            }
        }

        if (nonAdopters.isEmpty() || adopters.isEmpty()) return 0;

        List<PersonDistance> nonAdoptersWithDistance = new ArrayList<>();
        for (Person nonAdopter : nonAdopters) {
            double minDistance = Double.MAX_VALUE;
            for (Person adopter : adopters) {
                double distance = calculateEuclideanDistance(nonAdopter, adopter);
                minDistance = Math.min(minDistance, distance);
            }
            nonAdoptersWithDistance.add(new PersonDistance(nonAdopter, minDistance));
        }

        Collections.sort(nonAdoptersWithDistance, Comparator.comparingDouble(pd -> pd.distance));

        int actualImitators = Math.min(numImitators, nonAdoptersWithDistance.size());
        for (int i = 0; i < actualImitators; i++) {
            nonAdoptersWithDistance.get(i).person.setHasAdopted(true);
        }

        return actualImitators;
    }

    @Override
    public void selectNewAdoptersByDistance(List<Person> population, int newAdopters) {
        // Legacy method - kept for interface compatibility
        // This method now only selects by distance (imitators only)
        selectImitatorsByDistance(population, newAdopters);
    }

    @Override
    public double calculateEuclideanDistance(Person p1, Person p2) {
        double dx = p1.getXPos() - p2.getXPos();
        double dy = p1.getYPos() - p2.getYPos();
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void resetAdoptionStates(List<Person> population) {
        for (Person person : population) {
            person.setHasAdopted(false);
        }
    }

    @Override
    public int countAdopters(List<Person> population) {
        return (int) population.stream().mapToInt(person -> person.hasAdopted() ? 1 : 0).sum();
    }

    @Override
    public int countNonAdopters(List<Person> population) {
        return population.size() - countAdopters(population);
    }

    @Override
    public boolean isSimulationComplete(List<Person> population) {
        return countNonAdopters(population) == 0;
    }

    @Override
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    /**
     * Helper class to associate a person with their distance to the nearest adopter.
     * Used for distance-based adoption selection.
     */
    private static class PersonDistance {
        final Person person;
        final double distance;

        PersonDistance(Person person, double distance) {
            this.person = person;
            this.distance = distance;
        }
    }
}