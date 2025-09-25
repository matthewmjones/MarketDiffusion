package uk.ac.ucl.service;

import javafx.scene.canvas.GraphicsContext;
import uk.ac.ucl.geo.GeoJsonData;
import uk.ac.ucl.model.Person;
import uk.ac.ucl.model.RenderingConfig;
import uk.ac.ucl.util.BoundingBox;
import uk.ac.ucl.util.CoordinateTransformation;

import java.util.List;

public class MapRenderingServiceImpl implements MapRenderingService {

    @Override
    public void renderCountryMap(GraphicsContext gc, GeoJsonData geoData, BoundingBox bounds,
                                double canvasWidth, double canvasHeight, RenderingConfig config) {
        if (gc == null || geoData == null || bounds == null) return;

        clearCanvas(gc, canvasWidth, canvasHeight, config);

        gc.setFill(config.getCountryFillColor());
        gc.setStroke(config.getCountryStrokeColor());
        gc.setLineWidth(config.getCountryStrokeWidth());

        for (GeoJsonData.Feature feature : geoData.getFeatures()) {
            if ("MultiPolygon".equals(feature.getGeometry().getType())) {
                renderMultiPolygon(gc, feature.getGeometry().getCoordinates(), bounds,
                                 canvasWidth, canvasHeight, config);
            }
        }
    }

    @Override
    public void renderMultiPolygon(GraphicsContext gc, List<List<List<List<Double>>>> coordinates,
                                  BoundingBox bounds, double canvasWidth, double canvasHeight,
                                  RenderingConfig config) {
        if (gc == null || bounds == null || coordinates == null) return;

        CoordinateTransformation transform = CoordinateTransformation.create(
            bounds, canvasWidth, canvasHeight, config.getMapPadding());

        for (List<List<List<Double>>> polygon : coordinates) {
            for (List<List<Double>> ring : polygon) {
                if (ring.isEmpty()) continue;

                gc.beginPath();
                boolean first = true;

                for (List<Double> point : ring) {
                    double lon = point.get(0);
                    double lat = point.get(1);

                    double x = transform.transformX(lon);
                    double y = transform.transformY(lat);

                    if (first) {
                        gc.moveTo(x, y);
                        first = false;
                    } else {
                        gc.lineTo(x, y);
                    }
                }

                gc.closePath();
                gc.fill();
                gc.stroke();
            }
        }
    }

    @Override
    public void renderPopulation(GraphicsContext gc, List<Person> people, BoundingBox bounds,
                                double canvasWidth, double canvasHeight, RenderingConfig config) {
        if (gc == null || people == null || people.isEmpty() || bounds == null) return;

        CoordinateTransformation transform = CoordinateTransformation.create(
            bounds, canvasWidth, canvasHeight, config.getMapPadding());

        double dotRadius = config.getPersonDotRadius();

        for (Person person : people) {
            double x = transform.transformX(person.getXPos());
            double y = transform.transformY(person.getYPos());

            if (person.hasAdopted()) {
                gc.setFill(config.getAdoptedPersonColor());
            } else {
                gc.setFill(config.getNonAdoptedPersonColor());
            }

            gc.fillOval(x - dotRadius, y - dotRadius, dotRadius * 2, dotRadius * 2);
        }
    }

    @Override
    public void clearCanvas(GraphicsContext gc, double width, double height, RenderingConfig config) {
        if (gc == null || config == null) return;
        gc.setFill(config.getBackgroundColor());
        gc.fillRect(0, 0, width, height);
    }

    public void renderCompleteMap(GraphicsContext gc, GeoJsonData geoData, List<Person> people,
                                 BoundingBox bounds, double canvasWidth, double canvasHeight,
                                 RenderingConfig config) {
        renderCountryMap(gc, geoData, bounds, canvasWidth, canvasHeight, config);
        renderPopulation(gc, people, bounds, canvasWidth, canvasHeight, config);
    }
}