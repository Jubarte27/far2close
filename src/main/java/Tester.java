import bktree.MetricDistanceSearchTree;
import bktree.point.Point;
import bktree.result.Result;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class Tester<T extends Point<T>> {
    private final Function<Integer, ArrayList<T>> pointsGenerator;
    private final ArrayList<Supplier<MetricDistanceSearchTree<T>>> treeSuppliers = new ArrayList<>();

    public Tester(Function<Integer, ArrayList<T>> pointsGenerator) {
        this.pointsGenerator = pointsGenerator;
    }

    public void addTreeSupplier(Supplier<MetricDistanceSearchTree<T>> treeSupplier) {
        treeSuppliers.add(treeSupplier);
    }

    public void test(int testingIterations, int vCount, Supplier<Result> resultSupplier) {
        ArrayList<T> pointToSearchFor = pointsGenerator.apply(testingIterations);
        ArrayList<ArrayList<T>> points = new ArrayList<>();
        for (int i = 0; i < testingIterations; i++) {
            points.add(pointsGenerator.apply(vCount));
        }
        test(points, pointToSearchFor, resultSupplier);
    }

    private void test(ArrayList<ArrayList<T>> pointList, ArrayList<T> pointsToSearchFor, Supplier<Result> resultSupplier) {

        ArrayList<ArrayList<MetricDistanceSearchTree<T>>> treesList = new ArrayList<>();
        for (Supplier<MetricDistanceSearchTree<T>> treeSupplier : treeSuppliers) {
            ArrayList<MetricDistanceSearchTree<T>> trees = new ArrayList<>();
            for (int i = 0; i < pointList.size(); i++) {
                MetricDistanceSearchTree<T> tree = treeSupplier.get();
                trees.add(tree);
            }
            treesList.add(trees);
        }

        for (ArrayList<MetricDistanceSearchTree<T>> trees : treesList) {
            double average = averageInsertion(trees, pointList);
            System.out.println("Average insertion " + trees.get(0).getStrategy() + ": " + Main.df.format(average));
        }

        System.out.println();
        System.out.println();

        for (ArrayList<MetricDistanceSearchTree<T>> trees : treesList) {
            ArrayList<Result> results = new ArrayList<>();
            for (int i = 0; i < pointList.size(); i++) {
                results.add(resultSupplier.get());
            }
            double average = averageSearch(trees, pointsToSearchFor, results);
            System.out.println("Average search " + trees.get(0).getStrategy() + ": " + Main.df.format(average));
        }
    }

    private double timeInsertion(MetricDistanceSearchTree<T> tree, ArrayList<T> points) {
        double start = Main.getSec();
        tree.addNodes(points);
        return Main.getSec() - start;
    }

    private <U extends MetricDistanceSearchTree<T>> double averageInsertion(ArrayList<U> trees, ArrayList<ArrayList<T>> pointsList) {
        double totalTime = 0;
        for (int i = 0; i < trees.size(); i++) {
            U tree = trees.get(i);
            ArrayList<T> points = pointsList.get(i);
            totalTime += timeInsertion(tree, points);
        }
        return totalTime / trees.size();
    }

    private double timeSearch(MetricDistanceSearchTree<T> tree, T point, Result result) {
        double start = Main.getSec();
        tree.search(point, result);
        return Main.getSec() - start;
    }

    private <U extends MetricDistanceSearchTree<T>> double averageSearch(ArrayList<U> trees, ArrayList<T> points, ArrayList<Result> results) {
        double totalTime = 0;
        for (int i = 0; i < trees.size(); i++) {
            U tree = trees.get(i);
            T point = points.get(i);
            Result result = results.get(i);
            totalTime += timeSearch(tree, point, result);
        }
        return totalTime / trees.size();
    }
}
