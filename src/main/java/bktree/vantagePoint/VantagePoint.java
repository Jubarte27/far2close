package bktree.vantagePoint;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.point.Points;
import bktree.result.Result;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VantagePoint<T extends Point<T>> implements MetricDistanceSearchTree<T> {
    Node root;

    public VantagePoint(ArrayList<T> points) {
        addNodes(points);
    }

    public VantagePoint() {
    }

    private void insertPoints(Node subTree, Point[] points) {
        if (points.length == 0 || subTree == null) {
            return;
        }

        subTree.radius = Points.medianDistance((Point) subTree.info, points);

        Point[] insidePoints = new Point[points.length];
        Point[] outsidePoints = new Point[points.length];
        int insideCount = 0;
        int outsideCount = 0;

        for (Point point : points) {
            if (subTree.info.distance((T) point) > subTree.radius) {
                outsidePoints[outsideCount] = point;
                outsideCount++;
            } else {
                insidePoints[insideCount] = point;
                insideCount++;
            }
        }

        if (outsideCount > 0) {
            subTree.outside = new Node((T) outsidePoints[0]);
            insertPoints(subTree.outside, Arrays.copyOfRange(outsidePoints, 1, outsideCount));
        }

        if (insideCount > 0) {
            subTree.inside = new Node((T) insidePoints[0]);
            insertPoints(subTree.inside, Arrays.copyOfRange(insidePoints, 1, insideCount));
        }
    }

    private void insertPoints(Node subTree, List<T> points) {
        if (points.isEmpty() || subTree == null) {
            return;
        }

        Points.updateDist(subTree.info, points);
        subTree.radius = Points.medianDistance(subTree.info, points);

        ArrayList<T> insidePoints = new ArrayList<>();
        ArrayList<T> outsidePoints = new ArrayList<>();

        for (T point : points) {
            if (point.getDist() > subTree.radius) {
                outsidePoints.add(point);
            } else {
                insidePoints.add(point);
            }
        }

        if (outsidePoints.size() > 0) {
            subTree.outside = new Node(outsidePoints.get(0));
            insertPoints(subTree.outside, outsidePoints.subList(1, outsidePoints.size()));
        }

        if (insidePoints.size() > 0) {
            subTree.inside = new Node(insidePoints.get(0));
            insertPoints(subTree.inside, insidePoints.subList(1, insidePoints.size()));
        }
    }

    private void search(Node node, T point, Result result) {
        if (node == null) {
            return;
        }
        double distance = node.info.distance(point);
        result.addWord(node.info, distance);

        if (distance > node.radius) {
            search(node.outside, point, result);
            if (node.radius + result.maxDist >= distance) {
                search(node.inside, point, result);
            }
        } else {
            search(node.inside, point, result);
            if (node.radius < distance + result.maxDist) {
                search(node.outside, point, result);
            }
        }
    }

    @Override
    public void addNodes(ArrayList<T> list) {
        if (list.size() > 0) {
            this.root = new Node(list.get(0));
            insertPoints(this.root, list.subList(1, list.size()));
//            Point[] points = new Point[list.size() - 1];
//            list.subList(1, list.size()).toArray(points);
//            insertPoints(this.root, points);
        }
    }

    @Override
    public void addNode(T point) {
    }

    @Override
    public void search(T point, Result result) {
        search(root, point, result);
    }

    @Override
    public String getStrategy() {
        return "VantagePoint";
    }

    public class Node {
        T info;
        double radius;
        Node inside;
        Node outside;

        public Node(T info) {
            this.info = info;
            this.outside = null;
            this.inside = null;
            this.radius = 0;
        }
    }
}
