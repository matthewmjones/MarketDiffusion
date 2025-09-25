package uk.ac.ucl.model;

import javafx.scene.paint.Color;

/**
 * Immutable configuration for map and population visualization rendering.
 * Contains colors, sizes, and styling properties for the canvas display.
 */
public class RenderingConfig {
    private final Color backgroundColor;
    private final Color countryFillColor;
    private final Color countryStrokeColor;
    private final double countryStrokeWidth;
    private final Color adoptedPersonColor;
    private final Color nonAdoptedPersonColor;
    private final double personDotRadius;
    private final double mapPadding;

    /**
     * Creates a rendering configuration with specified visual properties.
     *
     * @param backgroundColor canvas background color
     * @param countryFillColor fill color for country boundaries
     * @param countryStrokeColor stroke color for country boundaries
     * @param countryStrokeWidth width of country boundary strokes
     * @param adoptedPersonColor color for adopted individuals
     * @param nonAdoptedPersonColor color for non-adopted individuals
     * @param personDotRadius radius of person visualization dots
     * @param mapPadding padding around the map boundaries
     */
    public RenderingConfig(Color backgroundColor, Color countryFillColor, Color countryStrokeColor,
                          double countryStrokeWidth, Color adoptedPersonColor, Color nonAdoptedPersonColor,
                          double personDotRadius, double mapPadding) {
        this.backgroundColor = backgroundColor;
        this.countryFillColor = countryFillColor;
        this.countryStrokeColor = countryStrokeColor;
        this.countryStrokeWidth = countryStrokeWidth;
        this.adoptedPersonColor = adoptedPersonColor;
        this.nonAdoptedPersonColor = nonAdoptedPersonColor;
        this.personDotRadius = personDotRadius;
        this.mapPadding = mapPadding;
    }

    /**
     * Creates a default rendering configuration with standard colors and sizes.
     *
     * @return default rendering configuration
     */
    public static RenderingConfig getDefault() {
        return new RenderingConfig(
            Color.WHITE,
            Color.WHITESMOKE,
            Color.DARKBLUE,
            1.0,
            Color.web("#EA7600"),
            Color.web("#8c8279"),
            3.0,
            20.0
        );
    }

    public Color getBackgroundColor() { return backgroundColor; }
    public Color getCountryFillColor() { return countryFillColor; }
    public Color getCountryStrokeColor() { return countryStrokeColor; }
    public double getCountryStrokeWidth() { return countryStrokeWidth; }
    public Color getAdoptedPersonColor() { return adoptedPersonColor; }
    public Color getNonAdoptedPersonColor() { return nonAdoptedPersonColor; }
    public double getPersonDotRadius() { return personDotRadius; }
    public double getMapPadding() { return mapPadding; }
}