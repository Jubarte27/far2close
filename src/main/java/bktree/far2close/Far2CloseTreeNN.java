package bktree.far2close;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.point.Points;
import bktree.result.Result;

import java.util.ArrayList;
import java.util.List;

public class Far2CloseTreeNN<T extends Point<T>> implements MetricDistanceSearchTree<T> {

    private MetaNode root;
    public static int count = 0;
    public static final int DEGREE = 5;

    @Override
    public String getStrategy() {
        return "Far2CloseTreeNN";
    }

    public void addNode(T point) {
        if (root == null) {
            MetaNode metaNode = new MetaNode(20);
            root = metaNode;

        }
        root.addNode(point);
    }

    @Override
    public void addNodes(ArrayList<T> list) {
        if (root == null) {
            if (list.size() > 0) {
                Points.updateDist(list.get(0), list);
                root = new MetaNode(Points.medianDistance(list.get(0), list));
            }
        }
        for (T o : list) {
            addNode(o);
        }
    }

    /*
    @Override
    public ArrayList<Point> search(Point point, double distance) {
        ArrayList<Point> points = new ArrayList<Point>();
        search(point, distance, points);
        return points;
    }*/

    @Override
    public void search(T point, Result result) {

        count = 0;

        if (root != null) {
            root.search(point, result);
        }

    }

    public class MetaNode {

        double factor;
        ArrayList<Node> children = new ArrayList<>();
        Node remainingNode;

        public MetaNode(double factor) {
            this.factor = factor;

        }

        public void addNode(T point) {

            for (Node node : children) {
                double d = node.point.distance(point);
                if (d < factor) {
                    node.addNode(point);
                    return;
                }
            }

            if (children.size() >= DEGREE) {
                if (remainingNode != null) {
                    remainingNode.addNode(point);
                } else {
                    remainingNode = new Node(point, 2 * factor);
                }
            } else {
                Node node = new Node(point, factor / 2);
                children.add(node);
            }

        }

        private void search(T point, Result result) {

            for (Node node : children) {
                double d = node.point.distance(point);
                //int dd = (int) Math.ceil(d);

                //if (d <= result.maxDist) {
                result.addWord(node.point, d);
                //points.add(node.point);
                //}
                if (d <= factor + result.maxDist) {
                    node.metaNode.search(point, result);
                }
                if (factor - d > result.maxDist) {
                    return;
                }
            }

            if (remainingNode != null) {
                double d = remainingNode.point.distance(point);
                //int dd = (int) Math.ceil(d);

                result.addWord(remainingNode.point, d);
                
                remainingNode.metaNode.search(point, result);
            }

        }

        public class Node {

            T point;
            MetaNode metaNode;

            public Node(T point, double factor) {
                this.point = point;
                metaNode = new MetaNode(factor);
            }

            public void addNode(T point) {
                metaNode.addNode(point);
            }

            private void search(T point_, Result result) {
                metaNode.search(point_, result);
            }
        }
    }

}
