package bktree.vantagePoint;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.result.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Anatoly Borisov
 */
public class VpTree_a<T extends Point<T>> implements MetricDistanceSearchTree<T> {

    // The following condition must held:
    // MAX_LEAF_SIZE >= VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT
    private int VANTAGE_POINT_CANDIDATES = 3;
    private int TEST_POINT_COUNT = 5;
    private int MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;

    VpTreeNode1 root;
    
    Hashtable<T, Double> map = new Hashtable<>();

    public static int search_count;
    public static int search_count2;
    public static double balance = 0;
    public static int balance_count = 0;
    
    public VpTree_a(int vantagePoints, int testPoints){
        VANTAGE_POINT_CANDIDATES = vantagePoints;
        TEST_POINT_COUNT = testPoints;
        MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;
    }
    
    public void addNodes(ArrayList<T> points) {
        
        root = new VpTreeNode1();
        root.points = new ArrayList<>();

        root.points.addAll(points);
        root.buildTreeNode();
        
    }

    public void addNode(T point) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void search(T point, Result result) {
        
        
        
        if (root!=null)
            root.findNearbyPoints(point,  null,-1, result);
        
    }

    @Override
    public String getStrategy() {
        return "vp tree a";
    }

    
    public class VpTreeNode1{
    
    private VpTreeNode1 left = null;
    private VpTreeNode1 right = null;
    private T vantagePoint = null;
    public double distance2Parent = 0;
    private double leftRadius = 0;
    
    public List<T> points;
    
    public VpTreeNode1() {
        
    }
    
    
    
    public void findNearbyPoints(T point,T parentPoint, double dist2Parent, Result result) {
        if (left == null) {
            
//            if (dist2Parent==-1){
//                dist2Parent = distanceLevLight(point.word, parentPoint.word);
//            }
            for (T p : points) {
                double pDist = map.get(p);
                if (result.maxDist>=Math.abs(pDist-dist2Parent))
                {
                double d = point.distance(p);
                result.addWord(p, d);
                }
                //else System.out.println("filtrei");
            }
            return ;
        }

        
//        if (dist2Parent>maxDistance && (leftRadius < (dist2Parent - maxDistance)/2)){
//            return right.findNearbyPoints(point,vantagePoint,-1, maxDistance);
//        }
//        else 
        {
            double distanceToLeftCenter = vantagePoint.distance(point);
            if (distanceToLeftCenter + result.maxDist < leftRadius) {
            left.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, result);
            return ;
        } else if (distanceToLeftCenter - result.maxDist >= leftRadius) {
            right.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, result);
            return;
        } else {
            right.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, result);
            left.findNearbyPoints(point,vantagePoint,distanceToLeftCenter, result);
            return ;
        }}
    }

    

    /** List must not be modified after node creation! */
    private void buildTreeNode() {
        
        if (points.size() < MAX_LEAF_SIZE) {
            return;
        }

        T basePoint = chooseNewVantagePoint(points);
        double[] distances = new double[points.size()];
        double[] sortedDistances = new double[points.size()];

        for (int i = 0; i < points.size(); ++i) {
            T p = points.get(i);
            distances[i] = basePoint.distance(p);
            p.setDist(distances[i]);
            map.put(p, distances[i]);
            sortedDistances[i] = distances[i];
        }

        Arrays.sort(sortedDistances);
        final double medianDistance = sortedDistances[sortedDistances.length / 2];
        
        left = new VpTreeNode1();
        left.points = new ArrayList<>();
        right = new VpTreeNode1();
        right.points = new ArrayList<>();
        
        for (int i = distances.length-1;i>=0; i--) {
            if (distances[i] < medianDistance) {
                left.points.add(points.get(i));
            } else {
                right.points.add(points.get(i));
            }
            points.remove(i);
        }

        vantagePoint = basePoint;
        leftRadius = medianDistance;

        balance += Math.abs(0.5 - right.points.size() / (right.points.size() + left.points.size()));
        balance_count++;
        
        left.buildTreeNode();
        if (left.vantagePoint!=null){
            left.distance2Parent = left.vantagePoint.distance(basePoint);
        }

        right.buildTreeNode();
        if (right.vantagePoint!=null){
            right.distance2Parent = right.vantagePoint.distance(basePoint);
        }

        
        
    }
    
    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseNewVantagePoint(List<T> points) {
        ArrayList<T> candidates = new ArrayList<>(VANTAGE_POINT_CANDIDATES);
        ArrayList<T> testPoints = new ArrayList<>(TEST_POINT_COUNT);

        for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
            int basePointIndex = (int) (Math.random() * points.size());
            T candidate = points.get(basePointIndex);
            candidates.add(candidate);
        }



        T bestBasePoint = points.get(0);
        double bestBasePointSigma = 0;

        for (T basePoint : candidates) {
            double[] distances = new double[points.size()];
            for (int i = 0; i < distances.length; ++i) {
                distances[i] = basePoint.distance(points.get(i));
            }
            double sigma = sigmaSquare(distances);
            if (sigma > bestBasePointSigma) {
                bestBasePointSigma = sigma;
                bestBasePoint = basePoint;
            }
        }

        return bestBasePoint;
    }

    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseNewVantagePoint2(List<T> points) {
        ArrayList<T> candidates = new ArrayList<>(VANTAGE_POINT_CANDIDATES);
        ArrayList<T> testPoints = new ArrayList<>(TEST_POINT_COUNT);

        for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
            int basePointIndex = i + (int) (Math.random() * (points.size() - i));
            T candidate = points.get(basePointIndex);
            points.set(basePointIndex, points.get(i));
            points.set(i, candidate);
            candidates.add(candidate);
        }

        for (int i = VANTAGE_POINT_CANDIDATES; i < VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT; ++i) {
            int testPointIndex = i + (int) (Math.random() * (points.size() - i));
            T testPoint = points.get(testPointIndex);
            points.set(testPointIndex, points.get(i));
            points.set(i, testPoint);
            testPoints.add(testPoint);
        }

        T bestBasePoint = points.get(0);
        double bestBasePointSigma = 0;

        for (T basePoint : candidates) {
            double[] distances = new double[TEST_POINT_COUNT];
            for (int i = 0; i < TEST_POINT_COUNT; ++i) {
                distances[i] = basePoint.distance(testPoints.get(i));
            }
            double sigma = sigmaSquare(distances);
            if (sigma > bestBasePointSigma) {
                bestBasePointSigma = sigma;
                bestBasePoint = basePoint;
            }
        }

        return bestBasePoint;
    }

    private double sigmaSquare(double[] values) {
        double sum = 0;

        for (double value : values) {
            sum += value;
        }

        double avg = sum / values.length;
        double sigmaSq = 0;

        for (double value : values) {
            double dev = value - avg;
            sigmaSq += dev * dev;
        }

        return sigmaSq;
    }
    }
}