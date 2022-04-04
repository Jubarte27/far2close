package bktree.far2close;

import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.point.Points;
import bktree.result.Result;
import util.Util;

import java.util.ArrayList;
import java.util.List;

public class Far2CloseTreeNNSimpleBinary<T extends Point<T>> implements MetricDistanceSearchTree<T> {

    private MetaNode root;
    public static int count = 0;
    public static final int DEGREE = 1;

    public static int max_level = 0;

    public String getStrategy() {
        return "Far2CloseSimple";
    }

    public Far2CloseTreeNNSimpleBinary() {
    }


    public void addNode(T point) {
        if (root == null) {
            MetaNode metaNode = new MetaNode();
            metaNode.factor = Integer.MAX_VALUE / 2.0;
            metaNode.level = 1;
            metaNode.type = 0;
            metaNode.point = point;
            root = metaNode;
            return;
        }
        root.addNode(point);
    }

    public void addNodes(ArrayList<T> list) {
        if (root == null) {
            if (list.size() > 0) {
                List<T> children = list.subList(1, list.size());
                MetaNode metaNode = new MetaNode();
                metaNode.point = list.get(0);
                Points.updateDist(metaNode.point, children);
                metaNode.factor = Points.medianDistance(metaNode.point, children);
                metaNode.level = 1;
                metaNode.type = 0;

                root = metaNode;

                root.addNodes(children);
            }
        } else {
            for (T point : list) {
                addNode(point);
            }
        }
    }

    public void search(T point, Result result) {
        count = 0;
        if (root != null) {
            root.search(point, result);
        }

    }


    public class MetaNode {

        double factor;
        T point;
        MetaNode pivot;
        MetaNode remainingNode;
        MetaNode parentNode;
        public int level = 0;
        int type = 0;

        public MetaNode() {
        }

        private double findLeftElbow(MetaNode node) {

            while (node != null) {
                if (node.type == 1) {
                    return node.parentNode.factor;
                }
                node = node.parentNode;
            }
            return -1;
        }

        private double findRightElbow(MetaNode node) {

            while (node != null) {
                if (node.type == 2) {
                    return node.parentNode.factor;
                }
                node = node.parentNode;
            }
            return -1;
        }

        public void addNode(T p) {

            //System.out.println("Adicionando ponto "+p.toString()+" fator atual "+factor);


            double d = point.distance(p);
            if (d < factor) {
                if (pivot == null) {
                    pivot = new MetaNode();
                    pivot.point = p;

                    pivot.factor = factor / 2;
                    pivot.level = level + 1;
                    if (pivot.level > max_level) {
                        max_level = pivot.level;
                    }
                } else {
                    pivot.addNode(p);
                }
            } else {

                if (remainingNode == null) {
                    remainingNode = new MetaNode();
                    remainingNode.point = p;

                    remainingNode.factor = factor * 2;
                    remainingNode.level = level + 1;
                    if (remainingNode.level > max_level) {
                        max_level = remainingNode.level;
                    }
                } else {
                    remainingNode.addNode(p);
                }
            }

        }

        public void addNodes(List<T> points) {
            ArrayList<T> pivotList = new ArrayList<>();
            ArrayList<T> remainingList = new ArrayList<>();

            for (T point : points) {
                if (this.point.distance(point) < factor) {
                    pivotList.add(point);
                } else {
                    remainingList.add(point);
                }
            }

            if (!pivotList.isEmpty()) {
                var children = pivotList.subList(1, pivotList.size());
                pivot = new MetaNode();
                pivot.point = pivotList.get(0);
                if (children.size() > 0) {
                    Points.updateDist(pivot.point, children);
                    pivot.factor = Points.medianDistance(pivot.point, children);
                } else {
                    pivot.factor = factor / 2;
                }
                pivot.level = level + 1;
                if (pivot.level > max_level) {
                    max_level = pivot.level;
                }
                pivot.addNodes(children);
            }

            if (!remainingList.isEmpty()) {
                var children = remainingList.subList(1, remainingList.size());
                remainingNode = new MetaNode();
                remainingNode.point = remainingList.get(0);
                if (children.size() > 0) {
                    Points.updateDist(remainingNode.point, children);
                    remainingNode.factor = Points.medianDistance(remainingNode.point, children);
                } else {
                    remainingNode.factor = factor * 2;
                }
                remainingNode.level = level + 1;
                if (remainingNode.level > max_level) {
                    max_level = remainingNode.level;
                }
                remainingNode.addNodes(remainingList.subList(1, remainingList.size()));
            }
        }

        protected void search(T p, Result result) {

            double d = point.distance(p);

            result.addWord(point, d);


            if (d + result.maxDist < factor) {
                if (pivot != null) {
                    pivot.search(p, result);
                }
            } else if (d - result.maxDist >= factor) {
                if ((remainingNode != null)) {
                    remainingNode.search(p, result);
                }
            } else {
                if (pivot != null) {
                    pivot.search(p, result);
                }
                if ((remainingNode != null)) {
                    remainingNode.search(p, result);
                }
            }


        }

        private void search_(T point, Result result) {


            if (pivot != null) {

                double d = pivot.point.distance(point);
                //int dd = (int) Math.ceil(d);

                //if (d <= result.maxDist) {
                result.addWord(pivot.point, d);
                //points.add(node.point);
                //}
                if (d <= factor + result.maxDist) {
                    pivot.search(point, result);
                }
                if (factor - d > result.maxDist) {
                    return;
                }
            }

            if (remainingNode != null) {
                double d = remainingNode.point.distance(point);
                //int dd = (int) Math.ceil(d);

                result.addWord(remainingNode.point, d);

                remainingNode.search(point, result);
            }

        }

    }


}
