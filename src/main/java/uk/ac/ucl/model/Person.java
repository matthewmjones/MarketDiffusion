package uk.ac.ucl.model;

/**
 * Represents an individual in the diffusion simulation with position and adoption status.
 */
public class Person {
    private double xPos;
    private double yPos;
    private boolean hasAdopted;

    /**
     * Creates a person at origin (0,0) with no adoption.
     */
    public Person() {
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.hasAdopted = false;
    }

    /**
     * Creates a person at the specified position with no adoption.
     *
     * @param xPos the x-coordinate position
     * @param yPos the y-coordinate position
     */
    public Person(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.hasAdopted = false;
    }

    /**
     * Creates a person at the specified position with the given adoption status.
     *
     * @param xPos the x-coordinate position
     * @param yPos the y-coordinate position
     * @param hasAdopted whether this person has adopted the innovation
     */
    public Person(double xPos, double yPos, boolean hasAdopted) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.hasAdopted = hasAdopted;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }

    public boolean hasAdopted() {
        return hasAdopted;
    }

    public void setHasAdopted(boolean hasAdopted) {
        this.hasAdopted = hasAdopted;
    }
}