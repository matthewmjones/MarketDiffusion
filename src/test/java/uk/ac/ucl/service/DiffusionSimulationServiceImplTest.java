package uk.ac.ucl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.SimulationParameters;
import uk.ac.ucl.model.SimulationResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiffusionSimulationServiceImplTest {

    private DiffusionSimulationServiceImpl service;
    private List<Person> testPopulation;

    @BeforeEach
    void setUp() {
        service = new DiffusionSimulationServiceImpl();
        service.setSeed(12345L);

        testPopulation = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testPopulation.add(new Person(i * 1.0, i * 1.0));
        }
    }

    @Test
    void testInitializeAdopters_ValidInput() {
        service.initializeAdopters(testPopulation, 3);

        int adoptedCount = service.countAdopters(testPopulation);
        assertEquals(3, adoptedCount, "Should initialize exactly 3 adopters");
    }

    @Test
    void testInitializeAdopters_KLargerThanPopulation() {
        service.initializeAdopters(testPopulation, 15);

        int adoptedCount = service.countAdopters(testPopulation);
        assertEquals(10, adoptedCount, "Should initialize all people when k > population size");
    }

    @Test
    void testInitializeAdopters_ZeroK() {
        service.initializeAdopters(testPopulation, 0);

        int adoptedCount = service.countAdopters(testPopulation);
        assertEquals(0, adoptedCount, "Should initialize zero adopters when k=0");
    }

    @Test
    void testInitializeAdopters_EmptyPopulation() {
        List<Person> emptyPopulation = new ArrayList<>();
        service.initializeAdopters(emptyPopulation, 3);

        assertEquals(0, emptyPopulation.size(), "Empty population should remain empty");
    }

    @Test
    void testPerformDiffusionStep_ValidParameters() {
        SimulationParameters params = new SimulationParameters(0.1, 0.05, 2);
        service.initializeAdopters(testPopulation, 2);

        SimulationResult result = service.performDiffusionStep(testPopulation, params, 1);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.getTimeStep(), "Time step should match input");
        assertTrue(result.getTotalAdopted() >= 2, "Should maintain at least initial adopters");
        assertEquals(10, result.getTotalPopulation(), "Population size should be preserved");
        assertFalse(result.isComplete(), "Should not be complete with partial adoption");
    }

    @Test
    void testPerformDiffusionStep_EmptyPopulation() {
        List<Person> emptyPopulation = new ArrayList<>();
        SimulationParameters params = new SimulationParameters(0.1, 0.05, 1);

        SimulationResult result = service.performDiffusionStep(emptyPopulation, params, 1);

        assertNotNull(result, "Result should not be null");
        assertEquals(0, result.getTotalPopulation(), "Population should be 0");
        assertEquals(0, result.getTotalAdopted(), "Adopted should be 0");
        assertTrue(result.isComplete(), "Empty population should be complete");
    }

    @Test
    void testPerformDiffusionStep_FullyAdopted() {
        SimulationParameters params = new SimulationParameters(0.1, 0.05, 10);
        service.initializeAdopters(testPopulation, 10);

        SimulationResult result = service.performDiffusionStep(testPopulation, params, 1);

        assertTrue(result.isComplete(), "Fully adopted population should be complete");
        assertEquals(10, result.getTotalAdopted(), "All should be adopted");
        assertEquals(0, result.getNewAdopters(), "No new adopters possible");
    }

    @Test
    void testSelectNewAdoptersByDistance() {
        testPopulation.get(0).setHasAdopted(true);
        testPopulation.get(9).setHasAdopted(true);

        service.selectNewAdoptersByDistance(testPopulation, 2);

        int adoptedCount = service.countAdopters(testPopulation);
        assertTrue(adoptedCount >= 2, "Should have at least initial adopters");
        assertTrue(adoptedCount <= 4, "Should not exceed initial + requested");
    }

    @Test
    void testSelectNewAdoptersByDistance_NoAdopters() {
        service.selectNewAdoptersByDistance(testPopulation, 2);

        int adoptedCount = service.countAdopters(testPopulation);
        assertEquals(0, adoptedCount, "Should not add adopters when none exist");
    }

    @Test
    void testSelectNewAdoptersByDistance_NoNonAdopters() {
        for (Person person : testPopulation) {
            person.setHasAdopted(true);
        }

        service.selectNewAdoptersByDistance(testPopulation, 2);

        int adoptedCount = service.countAdopters(testPopulation);
        assertEquals(10, adoptedCount, "All should remain adopted");
    }

    @Test
    void testCalculateEuclideanDistance() {
        Person p1 = new Person(0.0, 0.0);
        Person p2 = new Person(3.0, 4.0);

        double distance = service.calculateEuclideanDistance(p1, p2);

        assertEquals(5.0, distance, 0.0001, "3-4-5 triangle should have distance 5");
    }

    @Test
    void testCalculateEuclideanDistance_SamePoint() {
        Person p1 = new Person(1.0, 1.0);
        Person p2 = new Person(1.0, 1.0);

        double distance = service.calculateEuclideanDistance(p1, p2);

        assertEquals(0.0, distance, 0.0001, "Same point should have distance 0");
    }

    @Test
    void testResetAdoptionStates() {
        for (Person person : testPopulation) {
            person.setHasAdopted(true);
        }

        service.resetAdoptionStates(testPopulation);

        int adoptedCount = service.countAdopters(testPopulation);
        assertEquals(0, adoptedCount, "All adoption states should be reset to false");
    }

    @Test
    void testCountAdopters() {
        testPopulation.get(0).setHasAdopted(true);
        testPopulation.get(5).setHasAdopted(true);

        int adoptedCount = service.countAdopters(testPopulation);

        assertEquals(2, adoptedCount, "Should count exactly 2 adopters");
    }

    @Test
    void testCountNonAdopters() {
        testPopulation.get(0).setHasAdopted(true);
        testPopulation.get(5).setHasAdopted(true);

        int nonAdoptedCount = service.countNonAdopters(testPopulation);

        assertEquals(8, nonAdoptedCount, "Should count exactly 8 non-adopters");
    }

    @Test
    void testIsSimulationComplete_True() {
        for (Person person : testPopulation) {
            person.setHasAdopted(true);
        }

        assertTrue(service.isSimulationComplete(testPopulation),
                  "Simulation should be complete when all adopted");
    }

    @Test
    void testIsSimulationComplete_False() {
        testPopulation.get(0).setHasAdopted(true);

        assertFalse(service.isSimulationComplete(testPopulation),
                   "Simulation should not be complete with partial adoption");
    }

    @Test
    void testSeedConsistency() {
        List<Person> population1 = createTestPopulation();
        List<Person> population2 = createTestPopulation();

        service.setSeed(54321L);
        service.initializeAdopters(population1, 3);

        service.setSeed(54321L);
        service.initializeAdopters(population2, 3);

        for (int i = 0; i < population1.size(); i++) {
            assertEquals(population1.get(i).hasAdopted(), population2.get(i).hasAdopted(),
                        "Same seed should produce identical adoption patterns");
        }
    }

    @Test
    void testSimulationParameters_ValidationInPerformStep() {
        SimulationParameters params = new SimulationParameters(0.1, 0.05, 1);
        service.initializeAdopters(testPopulation, 1);

        assertDoesNotThrow(() -> {
            service.performDiffusionStep(testPopulation, params, 1);
        }, "Valid parameters should not throw exception");
    }

    @Test
    void testBassModelFormula() {
        SimulationParameters params = new SimulationParameters(0.1, 0.2, 1);
        service.initializeAdopters(testPopulation, 1);

        SimulationResult result = service.performDiffusionStep(testPopulation, params, 1);

        assertTrue(result.getNewAdopters() >= 0, "New adopters should be non-negative");
        assertTrue(result.getTotalAdopted() <= result.getTotalPopulation(),
                  "Adopters cannot exceed population");
    }

    @Test
    void testDistanceBasedSelection() {
        testPopulation.clear();
        testPopulation.add(new Person(0.0, 0.0));
        testPopulation.add(new Person(1.0, 0.0));
        testPopulation.add(new Person(10.0, 0.0));
        testPopulation.add(new Person(100.0, 0.0));

        testPopulation.get(0).setHasAdopted(true);

        service.selectNewAdoptersByDistance(testPopulation, 1);

        assertTrue(testPopulation.get(1).hasAdopted(),
                  "Closest person should be selected first");
        assertFalse(testPopulation.get(3).hasAdopted(),
                   "Farthest person should not be selected first");
    }

    private List<Person> createTestPopulation() {
        List<Person> population = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            population.add(new Person(i * 1.0, i * 1.0));
        }
        return population;
    }
}