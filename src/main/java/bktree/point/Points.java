package bktree.point;

import util.Util;

import java.util.ArrayList;
import java.util.List;

public final class Points {
    public static <T extends Point<T>> void updateDist(T point, List<T> points) {
        for (T p : points) {
            p.setDist(point.distance(p));
        }
    }

    public static <T extends Point<T>> double medianDistance(T point, List<T> points) {
        return Util.middleElement(points, Comparable::compareTo).distance(point);
    }

    public static <T extends Point<T>> double medianDistance(T point, T[] points) {
        for (T p : points) {
            p.setDist(point.optimizedDistance(p));
        }

        return Util.middleElement(points, Comparable::compareTo).distance(point);
    }

    public static <T extends Point<T>> double medianDistanceSquared(T point, List<T> points) {
        List<Double> distancesSquared = new ArrayList<>();
        for (T p : points) {
            distancesSquared.add(point.optimizedDistance(p));
        }
        return Util.medianDouble(distancesSquared);
    }
}
