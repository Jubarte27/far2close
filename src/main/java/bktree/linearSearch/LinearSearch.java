package bktree.linearSearch;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.result.Result;

import java.util.ArrayList;


public class LinearSearch<T extends Point<T>> implements MetricDistanceSearchTree<T> {

    ArrayList<T> list = new ArrayList<>();

    public void addNode(T point) {
        list.add(point);
    }

    public void addNodes(ArrayList<T> list) {
        this.list.addAll(list);
    }

    public void search(T point, Result result) {
        for (T obj : list) {
            double curDist = obj.distance(point);
            result.addWord(obj, curDist);
        }

    }

    @Override
    public String getStrategy() {
        return "Linear Search";
    }

}

