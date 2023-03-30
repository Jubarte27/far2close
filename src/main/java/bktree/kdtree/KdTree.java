package bktree.kdtree;

import bktree.MetricDistanceSearchTree;
import bktree.point.MultiPoint;
import bktree.result.Result;
import util.Util;

import java.util.ArrayList;

public class KdTree<T extends Comparable<T>, P extends MultiPoint<T, P>> implements MetricDistanceSearchTree<P> {
    int dimensions = 0;
    private Node root = null;
    private int dimensions() {
        return dimensions;
    }
    @Override
    public void addNodes(ArrayList<P> list) {
        list.stream().findAny().ifPresent(p -> dimensions = p.dimensions());
        root = insert(list, 0);
    }

    @Override
    public void addNode(P point) {
        if (root == null) {
            dimensions = point.dimensions();
            root = new Node(point);
        } else {
            root.insert(point, 0);
        }
    }

    private Node insert(ArrayList<P> points, int dimension) {
        if (points.isEmpty()) {
            return null;
        }

        P midPoint = Util.middleElement(points, (a, b) -> a.compareAt(b, dimension));
        Node midNode = new Node(midPoint);

        ArrayList<P> less = new ArrayList<>();
        ArrayList<P> moreEqual = new ArrayList<>();

        for (P point : points) {
            int result = midPoint.compareAt(point, dimension);
            if (midPoint == point) {
                continue;
            }
            if (result > 0) {
                less.add(point);
            } else {
                moreEqual.add(point);
            }
        }
        midNode.moreEqual = insert(moreEqual, nextDimension(dimension));
        midNode.less = insert(less, nextDimension(dimension));
        return midNode;
    }

    @Override
    public void search(P point, Result result) {
        search(root, point, result, 0);
    }

    private void search(Node node, P point, Result result, int dimension) {
        if (node == null) {
            return;
        }
        double d = node.info.distanceSquared(point);
        result.addWord(node.info, d);
        result.distanceFunctionCalls++;
        double dx = node.info.distanceInAxis(point, dimension);
        dimension = nextDimension(dimension);
        search(dx > 0 ? node.less : node.moreEqual, point, result, dimension);
        if (dx * dx <= result.maxDist) {
            search(dx > 0 ? node.moreEqual : node.less, point, result, dimension);
        }
    }
    @Override
    public String getStrategy() {
        return "KD-Tree";
    }

    int nextDimension(int dimension) {
        return (dimension + 1) % dimensions();
    }

    private class Node {
        private final P info;
        private Node less = null;
        private Node moreEqual = null;

        public Node(P info) {
            this.info = info;
        }

        void insert(P other, int dimension) {
            int result = info.compareAt(other, dimension);
            if (result > 0) {
                if (less == null) {
                    less = new Node(other);
                } else {
                    less.insert(other, nextDimension(dimension));
                }
            } else {
                if (moreEqual == null) {
                    moreEqual = new Node(other);
                } else {
                    moreEqual.insert(other, nextDimension(dimension));
                }
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "info=" + info +
                    '}';
        }
    }
}
