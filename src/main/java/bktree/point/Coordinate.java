package bktree.point;

import osmreader.OsmReader;

import java.util.*;

public class Coordinate implements MultiPoint<Double, Coordinate> {
    public final double[] axes;
    private double dist = 0;

    public Coordinate(double[] axes) {
        this.axes = axes.clone();
    }

    @Override
    public Double at(int i) {
        return axes[i];
    }

    @Override
    public int dimensions() {
        return axes.length;
    }

    public double distanceInAxis(Coordinate p, int axis) {
        return this.axes[axis] - p.axes[axis];
    }

    @Override
    public void setDist(Double d) {
        dist = d;
    }

    @Override
    public Double getDist() {
        return dist;
    }

    @Override
    public int compareTo(Coordinate o) {
        return Double.compare(dist, o.dist);
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "axes=" + Arrays.toString(axes) +
                '}';
    }

    public static Coordinate randomCoordinate(int dimensions) {
        double[] axes = new double[dimensions];
        Random r = new Random();
        for (int i = 0; i < dimensions; i++) {
            axes[i] = r.nextDouble(-100, 100);
        }
        return new Coordinate(axes);
    }

    public static ArrayList<Coordinate> randomCoordinates(int vCount, int dimensions) {
        ArrayList<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < vCount; i++) {
            points.add(randomCoordinate(dimensions));
        }
        return points;
    }

    public static ArrayList<Coordinate> schoolCoordinates() {
        return allCoordinatesFromResource("/schools.osm.pbf");
    }

    public static ArrayList<Coordinate> universityCoordinates() {
        return allCoordinatesFromResource("/universities.osm.pbf");
    }

    public static ArrayList<Coordinate> restaurantCoordinates() {
        return allCoordinatesFromResource("/restaurants.osm.pbf");
    }

    public static ArrayList<Coordinate> maxCoordinates() {
        return allCoordinatesFromResource("/south-america-latest.osm.pbf");
    }

    private static ArrayList<Coordinate> allCoordinatesFromResource(String resource) {
        OsmReader reader = new OsmReader(resource);
        ArrayList<Coordinate> allCoordinates = reader.filterAndConvert(Map.of(), osmNode -> new Coordinate(new double[] {osmNode.getLatitude(), osmNode.getLongitude()}), 1 << 19);
        reader.close();
        return allCoordinates;
    }
}
