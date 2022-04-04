package bktree.vantagePoint;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.result.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * @author 
 * Similar to the original
 * 
 */

public final class MultiVpTree_a<T extends Point<T>> implements MetricDistanceSearchTree<T> {

           // The following condition must held:
    // MAX_LEAF_SIZE >= VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT
    private int VANTAGE_POINT_CANDIDATES = 3;
    private int TEST_POINT_COUNT = 5;
    private int MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;
    
    private static final int MAX_DEPTH = 20;
    
    //public static Hashtable<String, Double> queryDic = new Hashtable<String, Double>();
    
    public static int search_count;
    public static int search_count2;
    
    MultiVpTreeNode root;
    
    //Hashtable<Word, Double> map = new Hashtable<Word, Double>();

 public MultiVpTree_a(int vantagePoints, int testPoints){
        VANTAGE_POINT_CANDIDATES = vantagePoints;
        TEST_POINT_COUNT = testPoints;
        MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;
    }
    
    public void addNodes(ArrayList<T> points) {
        root = new MultiVpTreeNode();

        root.points.addAll(points);
        root.level = 0;
        root.buildTreeNode(null, 0);
        
    }

    public void addNode(T point) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void search(T point, Result result) {
        
        if (root!=null)
            root.findNearbyPoints(point,  -1, result);
    }

    public String getStrategy() {
        return "MultiVpTree A";
    }



public class MultiVpTreeNode {

    

    private MultiVpTreeNode left = null;
    private MultiVpTreeNode right = null;
    private MultiVpTreeNode parent = null;
    private T vantagePoint = null;
    public double distance2Parent = 0;
    private double leftRadius = 0;
    
    int level = 0;
    
    
    private List<T> points = new ArrayList<>();
    
    
    //public Hashtable<String, Double> buildDic = new Hashtable<String, Double>();
    
    
    public void findNearbyPoints(T point,  Result result) {
        //queryDic.clear();
        findNearbyPoints(point, -1, result);
    }
    
    public void findNearbyPoints(T point,  double parentDistance, Result result) {
        
        if (points.size()>0){
            for (T curPoint : points) {
                //                double curPointDist = map.get(curPoint);
//                if (maxDistance<Math.abs(parentDistance-curPointDist))
//                    continue;
                /*
                MultiVpTreeNode aux = this;
                boolean ignore = false;
                while (aux!=null && aux.vantagePoint!=null){
                    String key = aux.vantagePoint.toString()+"***"+point.toString();
                    Double queryDist = queryDic.get(key);
                    
                    String key1 = aux.vantagePoint.toString()+"***"+curPoint.toString();
                    Double parentDist = buildDic.get(key1);
                    
                    if (parentDist-maxDistance>queryDist){
                        ignore = true;
                        break;
                    }
                    
                    if (parentDist+maxDistance<queryDist){
                        ignore = true;
                        break;
                    }
                    aux = aux.parent;
                }
                
                if (ignore) {
                    System.out.println("eitaaa");
                    continue;}
                */

                double dist = point.distance(curPoint);
                result.addWord(curPoint, dist);

            }
        }
        if (left!=null && right!=null){
            /*
            String key = vantagePoint.word+"***"+point.word;
            Double dist = queryDic.get(key);
            if (dist==null){
                dist = distanceLevLight(point.word, vantagePoint.word);
                queryDic.put(key, dist);
            }*/
            
            double dist = point.distance(vantagePoint);
            //else System.out.println("found .........................");
            
//            if (dist<=maxDistance) 
//                if (!result.contains(vantagePoint))
//                    result.add(vantagePoint);
            
            if (dist + result.maxDist <leftRadius)
                left.findNearbyPoints(point,dist, result);
            else if (dist - result.maxDist >=leftRadius)
                right.findNearbyPoints(point,dist, result);
            else {
                left.findNearbyPoints(point,dist, result);
                right.findNearbyPoints(point,dist, result);
                
            }
        }
        
        

    }

    
    private  void buildTreeNode(ArrayList<T> vantagePoints, int depth) {
        
        T basePoint = null;
        
        int new_level = -1;
        
        if (points.size() < MAX_LEAF_SIZE) {
            return;
        }
        if (level==0){
            new_level = 1;
            vantagePoints = new ArrayList<>();
            
            //VpTreePointString firstVantagePoint = chooseRandomVantagePoint(points);
            T firstVantagePoint = chooseNewVantagePoint(points);
            //points.remove(firstVantagePoint);
            vantagePoints.add(firstVantagePoint);
            T secondVantagePoint = chooseSecondVantagePoint(firstVantagePoint, points);
            //points.remove(secondVantagePoint);
            vantagePoints.add(secondVantagePoint);
            
            T thirdVantagePoint = chooseThirdVantagePoint(vantagePoints, points);
            //points.remove(thirdVantagePoint);
            vantagePoints.add(thirdVantagePoint);
            
            
            basePoint = vantagePoints.get(level);
        }
        else { 
            new_level = level + 1;
            basePoint = vantagePoints.get(level);
            if (new_level>2)
                new_level = 0;
        }


        for (T p : points) {
            double distance = basePoint.distance(p);
            p.setDist(distance);
            //map.put(p, distance);
        }

        Collections.sort(points);
        
        T medianPoint = points.get(points.size() / 2);
        //double medianDistance = map.get(medianPoint);
        double medianDistance = medianPoint.getDist();

        left = new MultiVpTreeNode();
        right = new MultiVpTreeNode();
        
        
        for (int i = points.size()-1;i>=0;i--) {
            T point = points.get(i);
            //double dist = point.dist;
            double dist = basePoint.distance(points.get(i));
            if (dist < medianDistance) {
                left.points.add(point);
            } else {
                right.points.add(point);
            }
//            if (level<MAX_DEPTH){
//                String key = basePoint.toString()+"***"+point.toString();
//                buildDic.put(key, dist);
//            }
            
            //point.distances.add(dist);
            points.remove(i);
        }

        

        vantagePoint = basePoint;
        leftRadius = medianDistance;

        left.level = new_level;
        left.parent = this;
        right.level = new_level;
        right.parent = this;
        
        if (left.points.size()==0)
            return;
        
        
        left.buildTreeNode(vantagePoints, depth+1);
        right.buildTreeNode(vantagePoints, depth+1);

    }

    private  void buildTreeNodeOld(T randomVP) {
        
        T basePoint = null;
        int new_level = -1;
        
        if (points.size() < MAX_LEAF_SIZE) {
            return;
        }
        if (level==0){
            new_level = 1;
            basePoint = chooseRandomVantagePoint(points);
        }
        else { 
            new_level = level + 1;
            if (new_level>4)
                new_level = 0;
            basePoint = randomVP;
        }


        for (T p : points) {
            double distance = basePoint.distance(p);
            p.setDist(distance);
            //map.put(p, distance);
        }

        Collections.sort(points);
        T medianPoint = points.get(points.size() / 2);
        //double medianDistance = map.get(medianPoint);
        double medianDistance = medianPoint.getDist();

        left = new MultiVpTreeNode();
        right = new MultiVpTreeNode();
        
        
        for (int i = points.size()-1;i>=0;i--) {
            T point = points.get(i);
            //double dist = point.dist;
            double dist = basePoint.distance(points.get(i));
            if (dist < medianDistance) {
                left.points.add(point);
            } else {
                right.points.add(point);
            }
//            String key = basePoint.toString()+"***"+point.toString();
//            buildDic.put(key, dist);
            
            //point.distances.add(dist);
            points.remove(i);
        }

        

        vantagePoint = basePoint;
        leftRadius = medianDistance;

        left.level = new_level;
        left.parent = this;
        right.level = new_level;
        right.parent = this;
        
        
        
        randomVP = chooseFarthestVantagePoint(basePoint, right.points);
        
       
        left.buildTreeNodeOld(randomVP);
        right.buildTreeNodeOld(randomVP);

    }
    
    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseSecondVantagePoint(T firstVantagePoint, List<T> points) {
        
        T farthestPoint = null;
        double farthestDistance = -1;
        for (T point : points) {
            double d = firstVantagePoint.distance(point);
            if (d > farthestDistance) {
                farthestDistance = d;
                farthestPoint = point;
            }
        }
       return farthestPoint;
    }
    
    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseThirdVantagePoint(List<T> vantagePoints, List<T> points) {
        
        T closestPoint = null;
        double closestDistance = 999999999;
        for (T point : points) {
            int summedDistance = 0;
            for (T vantagePoint : vantagePoints) {
                summedDistance += point.distance(vantagePoint);

                if (summedDistance < closestDistance) {
                    closestDistance = summedDistance;
                    closestPoint = point;
                }
            }
        }
       return closestPoint;
    }
    
    
    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseRandomVantagePoint(List<T> points) {
        
        int basePointIndex =  (int) (Math.random() * (points.size()));
        return points.get(basePointIndex);
    }
    
    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseFarthestVantagePoint(T pivot, List<T> points) {

        T farthestPoint = null;
        double farthestDistance = -1;

        for (T point : points) {
            double d = point.distance(pivot);
            if (d > farthestDistance) {
                farthestDistance = d;
                farthestPoint = point;
            }
        }
                
            

        return farthestPoint;
    }

    /** Trying to choose a new vantage point with highest distance deviation to other nodes. */
    private T chooseNewVantagePoint(List<T> points) {
        ArrayList<T> candidates = new ArrayList<T>(VANTAGE_POINT_CANDIDATES);

        for (int i = 0; i < VANTAGE_POINT_CANDIDATES; ++i) {
            int basePointIndex = i + (int) (Math.random() * (points.size() - i));
            T candidate = points.get(basePointIndex);
            points.set(basePointIndex, points.get(i));
            points.set(i, candidate);
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