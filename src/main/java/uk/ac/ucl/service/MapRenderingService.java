package uk.ac.ucl.service;

import javafx.scene.canvas.GraphicsContext;
import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.RenderingConfig;
import uk.ac.ucl.util.BoundingBox;

import java.util.List;

/**
 * Service interface for rendering geographic maps and population data on JavaFX canvas.
 * Handles coordinate transformation, polygon rendering, and population visualization.
 */
public interface MapRenderingService {

    /**
     * Renders a country map with boundaries onto the canvas.
     *
     * @param gc graphics context for drawing
     * @param geoData country geographic data
     * @param bounds bounding box for coordinate transformation
     * @param canvasWidth width of the canvas
     * @param canvasHeight height of the canvas
     * @param config rendering configuration
     */
    void renderCountryMap(GraphicsContext gc, GeoJsonData geoData, BoundingBox bounds,
                         double canvasWidth, double canvasHeight, RenderingConfig config);

    /**
     * Renders a multi-polygon geometry onto the canvas.
     *
     * @param gc graphics context for drawing
     * @param coordinates nested list of polygon coordinates
     * @param bounds bounding box for coordinate transformation
     * @param canvasWidth width of the canvas
     * @param canvasHeight height of the canvas
     * @param config rendering configuration
     */
    void renderMultiPolygon(GraphicsContext gc, List<List<List<List<Double>>>> coordinates,
                           BoundingBox bounds, double canvasWidth, double canvasHeight, RenderingConfig config);

    /**
     * Renders population points on the canvas with adoption status visualization.
     *
     * @param gc graphics context for drawing
     * @param people list of people to render
     * @param bounds bounding box for coordinate transformation
     * @param canvasWidth width of the canvas
     * @param canvasHeight height of the canvas
     * @param config rendering configuration
     */
    void renderPopulation(GraphicsContext gc, List<Person> people, BoundingBox bounds,
                         double canvasWidth, double canvasHeight, RenderingConfig config);

    /**
     * Clears the canvas and applies background color.
     *
     * @param gc graphics context for drawing
     * @param width canvas width
     * @param height canvas height
     * @param config rendering configuration
     */
    void clearCanvas(GraphicsContext gc, double width, double height, RenderingConfig config);
}