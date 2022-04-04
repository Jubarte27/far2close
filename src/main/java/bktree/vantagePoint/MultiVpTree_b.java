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

public final class MultiVpTree_b<T extends Point<T>> implements MetricDistanceSearchTree<T> {

           // The following condition must held:
    // MAX_LEAF_SIZE >= VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT
    
    private int VANTAGE_POINT_CANDIDATES = 3;
    private int TEST_POINT_COUNT = 5;
    private int MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;
    
    private static final int CHILDREN = 5;
    
    private static final int MAX_DEPTH = 20;
    
    //public static Hashtable<String, Double> queryDic = new Hashtable<String, Double>();
    
    public static int search_count;
    public static int search_count2;
    
    MultiVpTreeNode root;
    
    //Hashtable<Word, Double> map = new Hashtable<Word, Double>();

 public MultiVpTree_b(int vantagePoints, int testPoints){
        VANTAGE_POINT_CANDIDATES = vantagePoints;
        TEST_POINT_COUNT = testPoints;
        MAX_LEAF_SIZE = VANTAGE_POINT_CANDIDATES + TEST_POINT_COUNT;
    }
    
    public void addNodes(ArrayList<T> points) {
        root = new MultiVpTreeNode();

        root.points.addAll(points);
        root.buildTreeNode(1, new ArrayList<>());
        
    }

    public void addNode(T point) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void search(T point, Result result) {
        
       
        if (root!=null)
            root.findNearbyPoints(point,  -1, result);
        
    }

    public String getStrategy() {
        return "MultiVpTree B";
    }



public class MultiVpTreeNode {

    private ArrayList<Double> values = null;
    private ArrayList<MultiVpTreeNode> nodes = null;
    
    private MultiVpTreeNode parent = null;
    private T vantagePoint = null;
    public double distance2Parent = 0;
    
    
    
    
    
    private List<T> points = new ArrayList<>();
    
    
    //public Hashtable<String, Double> buildDic = new Hashtable<String, Double>();
    
    
    public void findNearbyPoints(T point,  Result result) {
        //queryDic.clear();
        findNearbyPoints(point, -1,  result);
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
        {
            /*
            String key = vantagePoint.word+"***"+point.word;
            Double dist = queryDic.get(key);
            if (dist==null){
                dist = distanceLevLight(point.word, vantagePoint.word);
                queryDic.put(key, dist);
            }*/
            if (vantagePoint==null)
                return;
            
            double dist = point.distance(vantagePoint);
            //else System.out.println("found .........................");
            
//            if (dist<=maxDistance) 
//                if (!result.contains(vantagePoint))
//                    result.add(vantagePoint);
            boolean ok = false;
            for(int x=0;x<nodes.size()-1;x++){
            Double radius = values.get(x);
            
            if (dist + result.maxDist <=radius){
                nodes.get(x).findNearbyPoints(point,dist, result);
                ok = true;
                break;
            }
            if (dist - result.maxDist <=radius){
                nodes.get(x).findNearbyPoints(point,dist, result);
            }
            }
            if (!ok)
                nodes.get(nodes.size()-1).findNearbyPoints(point,dist, result);    
            
        }
        
        

    }

    
    private  void buildTreeNode(int level, ArrayList<T> vps) {
        
        T basePoint = null;
        
        //level = 1;//comment this line to allow different pivot selection strategies
        if (level>3 || level<1){
            level = 1;
            vps = new ArrayList<>();
        }
        if (points.size() < MAX_LEAF_SIZE) {
            return;
        }
        
        
        
        nodes = new ArrayList<>();
        values = new ArrayList<>();
        
        
        if (level==1){
            //VpTreePointString firstVantagePoint = chooseRandomVantagePoint(points);
            T firstVantagePoint = chooseNewVantagePoint(points);
            //points.remove(firstVantagePoint);
            vantagePoint = firstVantagePoint;
            vps.add(firstVantagePoint);
        }
        else if (level==2){
            
            T secondVantagePoint = chooseSecondVantagePoint(vps.get(0), points);
            //points.remove(secondVantagePoint);
            vantagePoint = secondVantagePoint;
            vps.add(secondVantagePoint);
        }
        else{
            T thirdVantagePoint = chooseThirdVantagePoint(vps, points);
            //points.remove(thirdVantagePoint);
            vantagePoint = thirdVantagePoint;
            vps.add(thirdVantagePoint);
        }
        basePoint = vantagePoint;


        for (T p : points) {
            double distance = basePoint.distance(p);
            p.setDist(distance);
            //map.put(p, distance);
        }

        Collections.sort(points);
        
        
        for(int x=0;x<CHILDREN;x++){
            MultiVpTreeNode vpNode = new MultiVpTreeNode();
            vpNode.parent = this;
            nodes.add(vpNode);
        }
        
        int range = points.size() / CHILDREN;
        int index = 0;
        for (int x=0;x<CHILDREN-1;x++){
            index+=range;
            T medianPoint = points.get(index);
            //double medianDistance = map.get(medianPoint);
            double medianDistance = medianPoint.getDist();
            values.add(medianDistance);
        }
        //System.out.println("vantage point "+values.get(0)+" "+values.get(1)+" level "+level + "points "+points.size());
        
        
        
        for (int i = points.size()-1;i>=0;i--) {
            T point = points.get(i);
            //double dist = point.dist;
            double dist = point.getDist();//  distanceLevLight(basePoint.word, (points.get(i).word));
            boolean ok = false;
            for (int x=0;x<values.size();x++){
            if (dist <= values.get(x)) {
                nodes.get(x).points.add(point);
                ok = true;
                break;
            }
            }
            if (!ok)
                nodes.get(nodes.size()-1).points.add(point);
            
        
            points.remove(i);
        }
        
    
        if (values.get(0).equals(values.get(1)))
            return;

        for (MultiVpTreeNode node : nodes) {
            node.buildTreeNode(level + 1, vps);
        }

        

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
        ArrayList<T> candidates = new ArrayList<>(VANTAGE_POINT_CANDIDATES);

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