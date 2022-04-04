package bktree;

import bktree.point.Point;
import bktree.result.Result;

import java.util.ArrayList;

    public interface MetricDistanceSearchTree<T extends Point<T>> {
    
    public void addNodes(ArrayList<T> list);
    public void addNode(T point);
    //public ArrayList<T> search(T point, double distance);
    public void search(T point, Result result);
    public String getStrategy();
   

}
