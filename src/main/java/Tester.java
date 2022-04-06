import bktree.MetricDistanceSearchTree;
import bktree.linearSearch.LinearSearch;
import bktree.point.Point;
import bktree.result.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        ArrayList<LinearSearch<T>> linearSearches = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            linearSearches.add(new LinearSearch<>());
        }
        averageInsertionAndPrint(linearSearches, pointList);
        treesList.forEach(trees -> averageInsertionAndPrint(trees, pointList));

        System.out.println();
        System.out.println();


        ArrayList<Result> resultsLinearSearch = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            resultsLinearSearch.add(resultSupplier.get());
        }
        averageSearchAndPrint(linearSearches, pointsToSearchFor, resultsLinearSearch);

        for (ArrayList<MetricDistanceSearchTree<T>> trees : treesList) {
            ArrayList<Result> results = new ArrayList<>();
            for (int i = 0; i < pointList.size(); i++) {
                results.add(resultSupplier.get());
            }
            averageSearchAndPrint(trees, pointsToSearchFor, results);
            testForSameResult(results, resultsLinearSearch, pointsToSearchFor);
        }
    }

    private void testForSameResult(ArrayList<Result> tested, ArrayList<Result> linearSearchResults, ArrayList<T> pointsTested) {
        for (int i = 0; i < tested.size(); i++) {
            var testedResultPoints = new HashSet<>(tested.get(i).toArray());
            var linearResultPoints = new HashSet<>(linearSearchResults.get(i).toArray());
            if (!testedResultPoints.equals(linearResultPoints)) {
                System.out.println("\tFor point \"" + pointsTested.get(i).toString() + "\" the results are different:");
                System.out.println("\t\tTested: " + testedResultPoints.stream().sorted().toList());
                System.out.println("\t\tLinear Search: " + linearResultPoints.stream().sorted().toList());
            }
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

    private <U extends MetricDistanceSearchTree<T>> void averageInsertionAndPrint(ArrayList<U> trees, ArrayList<ArrayList<T>> pointsList) {
        double average = averageInsertion(trees, pointsList);
        System.out.println("Average insertion " + trees.get(0).getStrategy() + ": " + Main.df.format(average));
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

    private <U extends MetricDistanceSearchTree<T>> void averageSearchAndPrint(ArrayList<U> trees, ArrayList<T> points, ArrayList<Result> results) {
        double average = averageSearch(trees, points, results);
        System.out.println("Average search " + trees.get(0).getStrategy() + ": " + Main.df.format(average));
    }

}
