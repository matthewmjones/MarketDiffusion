package uk.ac.ucl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.util.BoundingBox;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeographicDataServiceImplTest {

    private GeographicDataServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GeographicDataServiceImpl();
        service.setSeed(12345L);
    }

    @Test
    void testLoadCountryData_ValidCountry() throws IOException {
        GeoJsonData result = service.loadCountryData("UK");

        assertNotNull(result, "GeoJsonData should not be null");
        assertNotNull(result.getFeatures(), "Features should not be null");
        assertFalse(result.getFeatures().isEmpty(), "Features should not be empty");
    }

    @Test
    void testLoadCountryData_InvalidCountry() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.loadCountryData("NonExistentCountry");
        }, "Should throw IllegalArgumentException for invalid country");
    }

    @Test
    void testLoadCountryData_AllSupportedCountries() throws IOException {
        String[] supportedCountries = {"UK", "USA", "China", "Australia"};

        for (String country : supportedCountries) {
            GeoJsonData result = service.loadCountryData(country);
            assertNotNull(result, "Should load data for " + country);
            assertNotNull(result.getFeatures(), "Features should exist for " + country);
        }
    }

    @Test
    void testCalculateBoundingBox() throws IOException {
        GeoJsonData geoData = service.loadCountryData("UK");
        BoundingBox bounds = service.calculateBoundingBox(geoData);

        assertNotNull(bounds, "BoundingBox should not be null");
        assertTrue(bounds.getMinLon() < bounds.getMaxLon(), "MinLon should be less than MaxLon");
        assertTrue(bounds.getMinLat() < bounds.getMaxLat(), "MinLat should be less than MaxLat");
        assertTrue(bounds.getLonRange() > 0, "Longitude range should be positive");
        assertTrue(bounds.getLatRange() > 0, "Latitude range should be positive");
    }

    @Test
    void testGenerateRandomPointsInCountry() throws IOException {
        GeoJsonData geoData = service.loadCountryData("UK");
        BoundingBox bounds = service.calculateBoundingBox(geoData);
        int requestedCount = 100;

        List<Person> people = service.generateRandomPointsInCountry(geoData, bounds, requestedCount);

        assertNotNull(people, "Generated people list should not be null");
        assertTrue(people.size() > 0, "Should generate at least some people");
        assertTrue(people.size() <= requestedCount, "Should not generate more than requested");

        for (Person person : people) {
            assertNotNull(person, "Each person should not be null");
            assertTrue(person.getXPos() >= bounds.getMinLon() && person.getXPos() <= bounds.getMaxLon(),
                "Person longitude should be within bounds");
            assertTrue(person.getYPos() >= bounds.getMinLat() && person.getYPos() <= bounds.getMaxLat(),
                "Person latitude should be within bounds");
            assertTrue(service.isPointInCountry(person.getXPos(), person.getYPos(), geoData),
                "Generated point should be inside country");
        }
    }

    @Test
    void testIsPointInPolygon_SimpleSquare() {
        List<List<Double>> square = List.of(
            List.of(0.0, 0.0),
            List.of(1.0, 0.0),
            List.of(1.0, 1.0),
            List.of(0.0, 1.0),
            List.of(0.0, 0.0)
        );

        assertTrue(service.isPointInPolygon(0.5, 0.5, square), "Center point should be inside");
        assertFalse(service.isPointInPolygon(1.5, 0.5, square), "Outside point should be outside");
        assertFalse(service.isPointInPolygon(-0.5, 0.5, square), "Negative point should be outside");
    }

    @Test
    void testIsPointInPolygon_EdgeCases() {
        List<List<Double>> triangle = List.of(
            List.of(0.0, 0.0),
            List.of(2.0, 0.0),
            List.of(1.0, 2.0),
            List.of(0.0, 0.0)
        );

        assertTrue(service.isPointInPolygon(1.0, 0.5, triangle), "Point inside triangle should be inside");
        assertFalse(service.isPointInPolygon(1.5, 1.5, triangle), "Point outside triangle should be outside");
        assertFalse(service.isPointInPolygon(-1.0, -1.0, triangle), "Point far outside should be outside");
    }

    @Test
    void testIsPointInCountry_WithRealData() throws IOException {
        GeoJsonData geoData = service.loadCountryData("UK");
        BoundingBox bounds = service.calculateBoundingBox(geoData);

        double centerLon = bounds.getCenterLon();
        double centerLat = bounds.getCenterLat();

        boolean centerResult = service.isPointInCountry(centerLon, centerLat, geoData);

        double farOutsideLon = bounds.getMinLon() - bounds.getLonRange();
        double farOutsideLat = bounds.getMinLat() - bounds.getLatRange();
        boolean outsideResult = service.isPointInCountry(farOutsideLon, farOutsideLat, geoData);

        assertFalse(outsideResult, "Point far outside bounding box should not be in country");
    }

    @Test
    void testIsPointInCountry_NullGeoData() {
        assertFalse(service.isPointInCountry(0.0, 0.0, null),
            "Null GeoData should return false");
    }

    @Test
    void testCalculateBoundingBox_EmptyGeoData() {
        GeoJsonData emptyGeoData = new GeoJsonData();
        emptyGeoData.setFeatures(List.of());

        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateBoundingBox(emptyGeoData);
        }, "Empty GeoData should throw IllegalArgumentException");
    }

    @Test
    void testGenerateRandomPointsInCountry_ZeroCount() throws IOException {
        GeoJsonData geoData = service.loadCountryData("UK");
        BoundingBox bounds = service.calculateBoundingBox(geoData);

        List<Person> people = service.generateRandomPointsInCountry(geoData, bounds, 0);

        assertNotNull(people, "Result should not be null");
        assertTrue(people.isEmpty(), "Should generate zero people when count is zero");
    }

    @Test
    void testSeedConsistency() throws IOException {
        GeoJsonData geoData = service.loadCountryData("UK");
        BoundingBox bounds = service.calculateBoundingBox(geoData);

        service.setSeed(12345L);
        List<Person> people1 = service.generateRandomPointsInCountry(geoData, bounds, 10);

        service.setSeed(12345L);
        List<Person> people2 = service.generateRandomPointsInCountry(geoData, bounds, 10);

        assertEquals(people1.size(), people2.size(), "Same seed should produce same number of people");

        for (int i = 0; i < people1.size(); i++) {
            assertEquals(people1.get(i).getXPos(), people2.get(i).getXPos(), 0.000001,
                "Same seed should produce identical X coordinates");
            assertEquals(people1.get(i).getYPos(), people2.get(i).getYPos(), 0.000001,
                "Same seed should produce identical Y coordinates");
        }
    }
}