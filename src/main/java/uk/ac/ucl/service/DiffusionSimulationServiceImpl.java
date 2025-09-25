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

        // Bass diffusion formula: p*N(t) + q*N(t)*A(t)/M where N(t)=non-adopters, A(t)=adopters, M=total
        double newAdoptersFormula = params.getP() * nonAdoptedCount +
                                   params.getQ() * nonAdoptedCount * adoptedCount / (double) totalPopulation;
        int newAdopters = (int) Math.round(newAdoptersFormula);
        newAdopters = Math.min(newAdopters, nonAdoptedCount);

        if (newAdopters > 0) {
            selectNewAdoptersByDistance(population, newAdopters);
        }

        int finalAdoptedCount = countAdopters(population);
        boolean isComplete = finalAdoptedCount >= totalPopulation;

        return new SimulationResult(currentTimeStep, newAdopters, finalAdoptedCount, totalPopulation, isComplete);
    }

    @Override
    public void selectNewAdoptersByDistance(List<Person> population, int newAdopters) {
        List<Person> nonAdopters = new ArrayList<>();
        List<Person> adopters = new ArrayList<>();

        for (Person person : population) {
            if (person.hasAdopted()) {
                adopters.add(person);
            } else {
                nonAdopters.add(person);
            }
        }

        if (nonAdopters.isEmpty() || adopters.isEmpty()) return;

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

        int actualNewAdopters = Math.min(newAdopters, nonAdoptersWithDistance.size());
        for (int i = 0; i < actualNewAdopters; i++) {
            nonAdoptersWithDistance.get(i).person.setHasAdopted(true);
        }
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