package uk.ac.ucl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.util.BoundingBox;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeographicDataServiceImpl implements GeographicDataService {

    private final Map<String, String> countryFiles = Map.of(
        "UK", "/uk/ac/ucl/geo/uk_geo.json",
        "USA", "/uk/ac/ucl/geo/usa_geo.json",
        "China", "/uk/ac/ucl/geo/china_geo.json",
        "Australia", "/uk/ac/ucl/geo/australia_geo.json"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @Override
    public GeoJsonData loadCountryData(String countryName) throws IOException {
        String filename = countryFiles.get(countryName);
        if (filename == null) {
            throw new IllegalArgumentException("Unknown country: " + countryName);
        }

        try (InputStream inputStream = getClass().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new IOException("Could not find file: " + filename);
            }
            return objectMapper.readValue(inputStream, GeoJsonData.class);
        }
    }

    @Override
    public BoundingBox calculateBoundingBox(GeoJsonData geoData) {
        double minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;

        for (GeoJsonData.Feature feature : geoData.getFeatures()) {
            if ("MultiPolygon".equals(feature.getGeometry().getType())) {
                List<List<List<List<Double>>>> coordinates = feature.getGeometry().getCoordinates();
                for (List<List<List<Double>>> polygon : coordinates) {
                    for (List<List<Double>> ring : polygon) {
                        for (List<Double> point : ring) {
                            double lon = point.get(0);
                            double lat = point.get(1);
                            minLon = Math.min(minLon, lon);
                            maxLon = Math.max(maxLon, lon);
                            minLat = Math.min(minLat, lat);
                            maxLat = Math.max(maxLat, lat);
                        }
                    }
                }
            }
        }

        if (minLon == Double.MAX_VALUE) {
            throw new IllegalArgumentException("No valid coordinates found in GeoJsonData");
        }

        return new BoundingBox(minLon, maxLon, minLat, maxLat);
    }

    @Override
    public List<Person> generateRandomPointsInCountry(GeoJsonData geoData, BoundingBox bounds, int count) {
        List<Person> people = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = count * 10;

        while (people.size() < count && attempts < maxAttempts) {
            double randomLon = bounds.getMinLon() + random.nextDouble() * bounds.getLonRange();
            double randomLat = bounds.getMinLat() + random.nextDouble() * bounds.getLatRange();

            if (isPointInCountry(randomLon, randomLat, geoData)) {
                people.add(new Person(randomLon, randomLat));
            }
            attempts++;
        }

        return people;
    }

    @Override
    public boolean isPointInCountry(double longitude, double latitude, GeoJsonData geoData) {
        if (geoData == null) return false;

        for (GeoJsonData.Feature feature : geoData.getFeatures()) {
            if ("MultiPolygon".equals(feature.getGeometry().getType())) {
                List<List<List<List<Double>>>> coordinates = feature.getGeometry().getCoordinates();
                for (List<List<List<Double>>> polygon : coordinates) {
                    for (List<List<Double>> ring : polygon) {
                        if (isPointInPolygon(longitude, latitude, ring)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPointInPolygon(double x, double y, List<List<Double>> polygon) {
        int n = polygon.size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i).get(0);
            double yi = polygon.get(i).get(1);
            double xj = polygon.get(j).get(0);
            double yj = polygon.get(j).get(1);

            if (((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }
}