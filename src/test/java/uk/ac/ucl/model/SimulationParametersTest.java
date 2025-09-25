package uk.ac.ucl.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationParametersTest {

    @Test
    void testValidParameters() {
        assertDoesNotThrow(() -> {
            new SimulationParameters(0.1, 0.05, 5);
        }, "Valid parameters should not throw exception");
    }

    @Test
    void testInvalidP_Negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationParameters(-0.1, 0.05, 5);
        }, "Negative p should throw IllegalArgumentException");
    }

    @Test
    void testInvalidP_GreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationParameters(1.1, 0.05, 5);
        }, "p > 1 should throw IllegalArgumentException");
    }

    @Test
    void testInvalidQ_Negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationParameters(0.1, -0.05, 5);
        }, "Negative q should throw IllegalArgumentException");
    }

    @Test
    void testInvalidQ_GreaterThanOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationParameters(0.1, 1.05, 5);
        }, "q > 1 should throw IllegalArgumentException");
    }

    @Test
    void testInvalidK_Negative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SimulationParameters(0.1, 0.05, -1);
        }, "Negative k should throw IllegalArgumentException");
    }

    @Test
    void testBoundaryValues() {
        assertDoesNotThrow(() -> {
            new SimulationParameters(0.0, 0.0, 0);
        }, "Boundary values (0,0,0) should be valid");

        assertDoesNotThrow(() -> {
            new SimulationParameters(1.0, 1.0, 1000);
        }, "Boundary values (1,1,1000) should be valid");
    }

    @Test
    void testGetters() {
        SimulationParameters params = new SimulationParameters(0.15, 0.25, 10);

        assertEquals(0.15, params.getP(), 0.0001, "P getter should return correct value");
        assertEquals(0.25, params.getQ(), 0.0001, "Q getter should return correct value");
        assertEquals(10, params.getK(), "K getter should return correct value");
    }

    @Test
    void testEquals() {
        SimulationParameters params1 = new SimulationParameters(0.1, 0.05, 5);
        SimulationParameters params2 = new SimulationParameters(0.1, 0.05, 5);
        SimulationParameters params3 = new SimulationParameters(0.2, 0.05, 5);

        assertEquals(params1, params2, "Equal parameters should be equal");
        assertNotEquals(params1, params3, "Different parameters should not be equal");
        assertNotEquals(params1, null, "Parameters should not equal null");
        assertNotEquals(params1, "string", "Parameters should not equal different type");
    }

    @Test
    void testHashCode() {
        SimulationParameters params1 = new SimulationParameters(0.1, 0.05, 5);
        SimulationParameters params2 = new SimulationParameters(0.1, 0.05, 5);

        assertEquals(params1.hashCode(), params2.hashCode(),
                    "Equal objects should have equal hash codes");
    }

    @Test
    void testToString() {
        SimulationParameters params = new SimulationParameters(0.123, 0.456, 789);
        String result = params.toString();

        assertTrue(result.contains("0.123"), "ToString should contain p value");
        assertTrue(result.contains("0.456"), "ToString should contain q value");
        assertTrue(result.contains("789"), "ToString should contain k value");
        assertTrue(result.contains("SimulationParameters"), "ToString should contain class name");
    }
}