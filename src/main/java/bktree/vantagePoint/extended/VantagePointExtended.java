package bktree.vantagePoint.extended;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.point.Points;
import bktree.result.Result;

import java.util.ArrayList;
import java.util.List;

public class VantagePointExtended<T extends Point<T>> implements MetricDistanceSearchTree<T> {
    Node root;
    public float threshold;

    public VantagePointExtended(float threshold) {
        this.threshold = threshold;
    }

    public VantagePointExtended() {
        threshold = 0.1f;
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
        result.distanceFunctionCalls++;
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
            if (root == null) {
                this.root = new Node(list.get(0));
                List<T> remaining = list.subList(1, list.size());
                if (remaining.size() > 0) {
                    if (remaining.size() > 2) {
                        int middle = Math.round(remaining.size() * threshold);
                        insertPoints(this.root, remaining.subList(0, middle));
                        remaining.subList(middle, remaining.size()).forEach(this::addNode);
                    } else {
                        insertPoints(this.root, remaining);
                    }
                }
            } else {
                list.forEach(this::addNode);
            }
        }
    }

    @Override
    public void addNode(T point) {
        Node currentNode = root;
        while (currentNode != null) {
            double distance = point.distance(currentNode.info);
            if (currentNode.radius < distance) {
                if (currentNode.outside == null) {
                    currentNode.outside = new Node(point);
                    currentNode.outside.radius = currentNode.radius * 2;
                    break;
                } else {
                    currentNode = currentNode.outside;
                }
            } else {
                if (currentNode.inside == null) {
                    currentNode.inside = new Node(point);
                    currentNode.inside.radius = currentNode.radius / 2;
                    break;
                } else {
                    currentNode = currentNode.inside;
                }
            }
        }
    }

    @Override
    public void search(T point, Result result) {
        search(root, point, result);
    }

    @Override
    public String getStrategy() {
        return "VantagePointExtended - threshold: " + threshold;
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
