package uk.ac.ucl.model;

/**
 * Immutable configuration parameters for Bass diffusion model simulation.
 * Contains innovation coefficient (p), imitation coefficient (q), and initial adopters (k).
 */
public class SimulationParameters {
    private final double p;
    private final double q;
    private final int k;

    /**
     * Creates simulation parameters with validation.
     *
     * @param p innovation coefficient (0.0-1.0)
     * @param q imitation coefficient (0.0-1.0)
     * @param k number of initial adopters (non-negative)
     * @throws IllegalArgumentException if parameters are outside valid ranges
     */
    public SimulationParameters(double p, double q, int k) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Innovation coefficient (p) must be between 0 and 1");
        }
        if (q < 0 || q > 1) {
            throw new IllegalArgumentException("Imitation coefficient (q) must be between 0 and 1");
        }
        if (k < 0) {
            throw new IllegalArgumentException("Initial adopters (k) must be non-negative");
        }

        this.p = p;
        this.q = q;
        this.k = k;
    }

    public double getP() { return p; }
    public double getQ() { return q; }
    public int getK() { return k; }

    @Override
    public String toString() {
        return String.format("SimulationParameters[p=%.3f, q=%.3f, k=%d]", p, q, k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulationParameters that = (SimulationParameters) o;
        return Double.compare(that.p, p) == 0 &&
               Double.compare(that.q, q) == 0 &&
               k == that.k;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(p, q, k);
    }
}