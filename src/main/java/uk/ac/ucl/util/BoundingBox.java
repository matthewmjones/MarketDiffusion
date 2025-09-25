package uk.ac.ucl.util;

/**
 * Immutable bounding box for geographic coordinate ranges.
 * Contains longitude and latitude bounds with utility methods for calculations.
 */
public class BoundingBox {
    private final double minLon;
    private final double maxLon;
    private final double minLat;
    private final double maxLat;

    /**
     * Creates a bounding box with the specified coordinate bounds.
     *
     * @param minLon minimum longitude
     * @param maxLon maximum longitude
     * @param minLat minimum latitude
     * @param maxLat maximum latitude
     */
    public BoundingBox(double minLon, double maxLon, double minLat, double maxLat) {
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.minLat = minLat;
        this.maxLat = maxLat;
    }

    public double getMinLon() { return minLon; }
    public double getMaxLon() { return maxLon; }
    public double getMinLat() { return minLat; }
    public double getMaxLat() { return maxLat; }

    /** @return longitude range (max - min) */
    public double getLonRange() { return maxLon - minLon; }

    /** @return latitude range (max - min) */
    public double getLatRange() { return maxLat - minLat; }

    /** @return center longitude coordinate */
    public double getCenterLon() { return (minLon + maxLon) / 2.0; }

    /** @return center latitude coordinate */
    public double getCenterLat() { return (minLat + maxLat) / 2.0; }

    @Override
    public String toString() {
        return String.format("BoundingBox[lon: %.4f to %.4f, lat: %.4f to %.4f]",
                           minLon, maxLon, minLat, maxLat);
    }
}