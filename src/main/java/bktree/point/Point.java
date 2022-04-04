package bktree.point;


/**
 * An base interface for point in VP Tree.
 * @author Anatoly Borisov
 */
public interface Point<T extends Point<T>> extends Comparable<T>{
    /**
     * Calculates distance to another point.
     * The metric must hold the following condition for points A, B, C:
     * A.distance(C) <= A.distance(B) + B.distance(C)
     */
    double distance(T p);

    /**
     * * For any points A, B, C
     * * (A.distance(B) > A.distance(C) == (A.optimizedDistance(B) > A.optimizedDistance(C))
     * * (A.distance(B) < A.distance(C) == (A.optimizedDistance(B) < A.optimizedDistance(C))
     * * (A.distance(B) == A.distance(C) == (A.optimizedDistance(B) == A.optimizedDistance(C))
     * * */
    double optimizedDistance(T p);
    
    void setDist(Double d);
    
    Double getDist();
}
