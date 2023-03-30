package bktree.point;

import static java.lang.Math.sqrt;

public interface MultiPoint<T extends Comparable<T>, P extends MultiPoint<T, P>> extends Point<P> {
    int dimensions();
    T at(int i);

    default int compareAt(P other, int dimension) {
        return at(dimension).compareTo(other.at(dimension));
    }

    double distanceInAxis(P p, int axis);

    default double distanceInAxisSquared(P p, int axis) {
        double distanceInAxis = distanceInAxis(p, axis);
        return distanceInAxis * distanceInAxis;
    }

    default double distanceSquared(P p) {
        double distanceSquared = 0;
        for (int i = 0; i < dimensions(); i++) {
            distanceSquared += distanceInAxisSquared(p, i);
        }
        return distanceSquared;
    }

    default double distance(P p) {
        return sqrt(distanceSquared(p));
    }

    default double optimizedDistance(P p) {
        return distanceSquared(p);
    }
}
