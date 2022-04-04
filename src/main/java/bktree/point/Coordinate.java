package bktree.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
}
