package uk.ac.ucl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.RenderingConfig;
import uk.ac.ucl.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapRenderingServiceImplTest {

    private MapRenderingServiceImpl service;
    private RenderingConfig config;
    private BoundingBox bounds;
    private List<Person> testPeople;

    @BeforeEach
    void setUp() {
        service = new MapRenderingServiceImpl();
        config = RenderingConfig.getDefault();
        bounds = new BoundingBox(-1.0, 1.0, -1.0, 1.0);

        testPeople = new ArrayList<>();
        testPeople.add(new Person(0.0, 0.0, false));
        testPeople.add(new Person(0.5, 0.5, true));
    }

    @Test
    void testRenderingConfigDefaults() {
        assertNotNull(config.getBackgroundColor(), "Background color should not be null");
        assertNotNull(config.getCountryFillColor(), "Country fill color should not be null");
        assertNotNull(config.getCountryStrokeColor(), "Country stroke color should not be null");
        assertNotNull(config.getAdoptedPersonColor(), "Adopted person color should not be null");
        assertNotNull(config.getNonAdoptedPersonColor(), "Non-adopted person color should not be null");

        assertTrue(config.getCountryStrokeWidth() > 0, "Stroke width should be positive");
        assertTrue(config.getPersonDotRadius() > 0, "Dot radius should be positive");
        assertTrue(config.getMapPadding() > 0, "Map padding should be positive");
    }

    @Test
    void testRenderCountryMap_WithNullGeoData() {
        // Test that null parameters are handled gracefully
        assertDoesNotThrow(() -> {
            service.renderCountryMap(null, null, bounds, 800, 600, config);
        }, "Should handle null GeoData gracefully");
    }

    @Test
    void testRenderCountryMap_WithNullBounds() {
        GeoJsonData geoData = createSimpleGeoData();

        assertDoesNotThrow(() -> {
            service.renderCountryMap(null, geoData, null, 800, 600, config);
        }, "Should handle null bounds gracefully");
    }

    @Test
    void testRenderMultiPolygon_WithNullParameters() {
        assertDoesNotThrow(() -> {
            service.renderMultiPolygon(null, null, null, 800, 600, config);
        }, "Should handle null parameters gracefully");

        assertDoesNotThrow(() -> {
            service.renderMultiPolygon(null, createSimpleCoordinates(), null, 800, 600, config);
        }, "Should handle null bounds gracefully");
    }

    @Test
    void testRenderPopulation_WithEmptyPeople() {
        List<Person> emptyPeople = new ArrayList<>();

        assertDoesNotThrow(() -> {
            service.renderPopulation(null, emptyPeople, bounds, 800, 600, config);
        }, "Should handle empty people list gracefully");
    }

    @Test
    void testRenderPopulation_WithNullBounds() {
        assertDoesNotThrow(() -> {
            service.renderPopulation(null, testPeople, null, 800, 600, config);
        }, "Should handle null bounds gracefully");
    }

    @Test
    void testClearCanvas() {
        // This test verifies the method can be called without throwing exceptions
        assertDoesNotThrow(() -> {
            service.clearCanvas(null, 800, 600, config);
        }, "Clear canvas should not throw exceptions");
    }

    @Test
    void testRenderCompleteMap() {
        GeoJsonData geoData = createSimpleGeoData();

        assertDoesNotThrow(() -> {
            service.renderCompleteMap(null, geoData, testPeople, bounds, 800, 600, config);
        }, "Complete map rendering should not throw exceptions");
    }

    @Test
    void testRenderCompleteMap_WithNullParameters() {
        assertDoesNotThrow(() -> {
            service.renderCompleteMap(null, null, null, null, 800, 600, config);
        }, "Should handle null parameters in complete map rendering");

        assertDoesNotThrow(() -> {
            service.renderCompleteMap(null, null, testPeople, bounds, 800, 600, config);
        }, "Should handle partial null parameters");
    }

    @Test
    void testParameterValidation_CanvasSize() {
        GeoJsonData geoData = createSimpleGeoData();

        // Test with various canvas sizes
        assertDoesNotThrow(() -> {
            service.renderCountryMap(null, geoData, bounds, 0, 0, config);
        }, "Should handle zero canvas size");

        assertDoesNotThrow(() -> {
            service.renderCountryMap(null, geoData, bounds, 1920, 1080, config);
        }, "Should handle large canvas size");

        assertDoesNotThrow(() -> {
            service.renderCountryMap(null, geoData, bounds, -100, -100, config);
        }, "Should handle negative canvas size");
    }

    @Test
    void testBoundingBoxInteraction() {
        BoundingBox largeBounds = new BoundingBox(-180.0, 180.0, -90.0, 90.0);
        BoundingBox smallBounds = new BoundingBox(0.0, 0.1, 0.0, 0.1);

        // Test with different bounding box sizes
        assertDoesNotThrow(() -> {
            service.renderPopulation(null, testPeople, largeBounds, 800, 600, config);
        }, "Should handle large bounding box");

        assertDoesNotThrow(() -> {
            service.renderPopulation(null, testPeople, smallBounds, 800, 600, config);
        }, "Should handle small bounding box");
    }

    @Test
    void testPeopleWithVariousStates() {
        List<Person> mixedPeople = new ArrayList<>();
        mixedPeople.add(new Person(-0.5, -0.5, false));
        mixedPeople.add(new Person(0.0, 0.0, true));
        mixedPeople.add(new Person(0.5, 0.5, false));
        mixedPeople.add(new Person(0.8, -0.8, true));

        assertDoesNotThrow(() -> {
            service.renderPopulation(null, mixedPeople, bounds, 800, 600, config);
        }, "Should handle mixed adoption states");
    }

    @Test
    void testExtremePeoplePositions() {
        List<Person> extremePeople = new ArrayList<>();
        extremePeople.add(new Person(bounds.getMinLon(), bounds.getMinLat(), false));
        extremePeople.add(new Person(bounds.getMaxLon(), bounds.getMaxLat(), true));
        extremePeople.add(new Person(bounds.getCenterLon(), bounds.getCenterLat(), false));

        assertDoesNotThrow(() -> {
            service.renderPopulation(null, extremePeople, bounds, 800, 600, config);
        }, "Should handle extreme positions within bounds");
    }

    @Test
    void testPeopleOutsideBounds() {
        List<Person> outsidePeople = new ArrayList<>();
        outsidePeople.add(new Person(-5.0, -5.0, false));
        outsidePeople.add(new Person(5.0, 5.0, true));

        assertDoesNotThrow(() -> {
            service.renderPopulation(null, outsidePeople, bounds, 800, 600, config);
        }, "Should handle people outside bounds gracefully");
    }

    // Helper methods to create test data
    private GeoJsonData createSimpleGeoData() {
        GeoJsonData geoData = new GeoJsonData();
        geoData.setType("FeatureCollection");

        GeoJsonData.Feature feature = new GeoJsonData.Feature();
        feature.setType("Feature");

        GeoJsonData.Geometry geometry = new GeoJsonData.Geometry();
        geometry.setType("MultiPolygon");
        geometry.setCoordinates(createSimpleCoordinates());
        feature.setGeometry(geometry);

        geoData.setFeatures(Arrays.asList(feature));
        return geoData;
    }

    private List<List<List<List<Double>>>> createSimpleCoordinates() {
        // Create a simple square polygon
        List<Double> point1 = Arrays.asList(-0.5, -0.5);
        List<Double> point2 = Arrays.asList(0.5, -0.5);
        List<Double> point3 = Arrays.asList(0.5, 0.5);
        List<Double> point4 = Arrays.asList(-0.5, 0.5);
        List<Double> point5 = Arrays.asList(-0.5, -0.5); // Close the polygon

        List<List<Double>> ring = Arrays.asList(point1, point2, point3, point4, point5);
        List<List<List<Double>>> polygon = Arrays.asList(ring);
        List<List<List<List<Double>>>> multiPolygon = Arrays.asList(polygon);

        return multiPolygon;
    }
}