package uk.ac.ucl.geo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Jackson-annotated classes for parsing GeoJSON country boundary data.
 * Represents a complete GeoJSON document with features and geometries.
 */
public class GeoJsonData {
    @JsonProperty("type")
    private String type;

    @JsonProperty("features")
    private List<Feature> features;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Feature> getFeatures() { return features; }
    public void setFeatures(List<Feature> features) { this.features = features; }

    /**
     * Represents a GeoJSON feature containing geometry and properties.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        @JsonProperty("type")
        private String type;

        @JsonProperty("geometry")
        private Geometry geometry;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Geometry getGeometry() { return geometry; }
        public void setGeometry(Geometry geometry) { this.geometry = geometry; }
    }

    /**
     * Represents GeoJSON geometry containing coordinate data for MultiPolygon features.
     */
    public static class Geometry {
        @JsonProperty("type")
        private String type;

        @JsonProperty("coordinates")
        private List<List<List<List<Double>>>> coordinates;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public List<List<List<List<Double>>>> getCoordinates() { return coordinates; }
        public void setCoordinates(List<List<List<List<Double>>>> coordinates) { this.coordinates = coordinates; }
    }
}