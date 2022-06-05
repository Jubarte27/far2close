package bktree.point;

import osmreader.OsmReader;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.sqrt;

public class Coordinate implements Point<Coordinate> {
    public final double[] axes;
    private double dist = 0;

    public Coordinate(double[] axes) {
        this.axes = axes.clone();
    }

    public void print() {
        for (double axis : axes) {
            System.out.println(axis + " ");
        }
    }

    public double distanceInAxis(Coordinate p, int axis) {
        return this.axes[axis] - p.axes[axis];
    }

    public double distanceInAxisSquared(Coordinate p, int axis) {
        double distanceInAxis = distanceInAxis(p, axis);
        return distanceInAxis * distanceInAxis;
    }

    public double distanceSquared(Coordinate p) {
        double distanceSquared = 0;
        for (int i = 0; i < axes.length; i++) {
            distanceSquared += distanceInAxisSquared(p, i);
        }
        return distanceSquared;
    }

    @Override
    public double distance(Coordinate p) {
        return sqrt(distanceSquared(p));
    }

    @Override
    public double optimizedDistance(Coordinate p) {
        return distanceSquared(p);
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
            axes[i] = r.nextDouble(-100000, 100000);
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
