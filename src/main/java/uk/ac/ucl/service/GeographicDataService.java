package uk.ac.ucl.service;

import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.util.BoundingBox;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for geographic data operations including country boundary
 * loading, point generation, and geometric calculations.
 */
public interface GeographicDataService {

    /**
     * Loads geographic boundary data for a specified country.
     *
     * @param countryName the name of the country to load
     * @return geographic data containing country boundaries
     * @throws IOException if the country data cannot be loaded
     */
    GeoJsonData loadCountryData(String countryName) throws IOException;

    /**
     * Calculates the bounding box for the given geographic data.
     *
     * @param geoData the geographic data to analyze
     * @return bounding box containing min/max coordinates
     */
    BoundingBox calculateBoundingBox(GeoJsonData geoData);

    /**
     * Generates random population points within country boundaries.
     *
     * @param geoData the country's geographic data
     * @param bounds the bounding box for the country
     * @param count number of points to generate
     * @return list of people positioned within the country
     */
    List<Person> generateRandomPointsInCountry(GeoJsonData geoData, BoundingBox bounds, int count);

    /**
     * Tests whether a coordinate point lies within the country boundaries.
     *
     * @param longitude the longitude coordinate
     * @param latitude the latitude coordinate
     * @param geoData the country's geographic data
     * @return true if the point is within the country boundaries
     */
    boolean isPointInCountry(double longitude, double latitude, GeoJsonData geoData);

    /**
     * Tests whether a point lies within a polygon using ray casting algorithm.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param polygon the polygon vertices
     * @return true if the point is inside the polygon
     */
    boolean isPointInPolygon(double x, double y, List<List<Double>> polygon);
}