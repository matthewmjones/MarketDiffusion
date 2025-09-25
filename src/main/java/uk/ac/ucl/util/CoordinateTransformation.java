package uk.ac.ucl.util;

/**
 * Utility class for transforming geographic coordinates to canvas pixel coordinates.
 * Maintains aspect ratio and applies padding for optimal map visualization.
 */
public class CoordinateTransformation {
    private final double scale;
    private final double offsetX;
    private final double offsetY;
    private final BoundingBox bounds;

    private CoordinateTransformation(double scale, double offsetX, double offsetY, BoundingBox bounds) {
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.bounds = bounds;
    }

    /**
     * Creates a coordinate transformation for the given bounds and canvas dimensions.
     * Calculates optimal scale and offset to fit the map within the canvas with padding.
     *
     * @param bounds the geographic bounding box
     * @param canvasWidth canvas width in pixels
     * @param canvasHeight canvas height in pixels
     * @param padding padding around the map edges
     * @return configured coordinate transformation
     */
    public static CoordinateTransformation create(BoundingBox bounds, double canvasWidth, double canvasHeight, double padding) {
        double lonRange = bounds.getLonRange();
        double latRange = bounds.getLatRange();

        double availableWidth = canvasWidth - 2 * padding;
        double availableHeight = canvasHeight - 2 * padding;

        double scaleX = availableWidth / lonRange;
        double scaleY = availableHeight / latRange;
        // Use minimum scale to maintain aspect ratio
        double scale = Math.min(scaleX, scaleY);

        double mapWidth = lonRange * scale;
        double mapHeight = latRange * scale;

        // Center the map within the available space
        double offsetX = padding + (availableWidth - mapWidth) / 2;
        double offsetY = padding + (availableHeight - mapHeight) / 2;

        return new CoordinateTransformation(scale, offsetX, offsetY, bounds);
    }

    /**
     * Transforms longitude to canvas x-coordinate.
     *
     * @param longitude the longitude value
     * @return canvas x-coordinate
     */
    public double transformX(double longitude) {
        return offsetX + (longitude - bounds.getMinLon()) * scale;
    }

    /**
     * Transforms latitude to canvas y-coordinate.
     * Note: Y-axis is flipped for canvas coordinate system.
     *
     * @param latitude the latitude value
     * @return canvas y-coordinate
     */
    public double transformY(double latitude) {
        return offsetY + (bounds.getMaxLat() - latitude) * scale;
    }

    public double getScale() { return scale; }
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
}