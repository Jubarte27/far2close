package tester;

import bktree.MetricDistanceSearchTree;
import bktree.linearSearch.LinearSearch;
import bktree.point.Point;
import bktree.result.Result;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonConfig;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tester<T extends Point<T>> {
    private final Function<Integer, ArrayList<T>> pointsGenerator;
    private final ArrayList<Supplier<MetricDistanceSearchTree<T>>> treeSuppliers = new ArrayList<>();

    private final ArrayList<SearchResults> searchResults = new ArrayList<>();
    private final ArrayList<InsertionResults> insertionResults = new ArrayList<>();

    public Tester(Function<Integer, ArrayList<T>> pointsGenerator) {
        this.pointsGenerator = pointsGenerator;
    }

    private String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public void scatter() {
        List<Integer> x = searchResults
                .stream()
                .map(s -> s.result().distanceFunctionCalls)
                .collect(Collectors.toList());

        Plot plt = Plot.create(PythonConfig.pythonBinPathConfig("/home/itto/anaconda3/bin/python"));
        plt.hist().add(x).label("VantagePointExtended");
        plt.legend().loc("upper right");
        plt.title("VantagePointExtended");
        try {
            plt.show();
        } catch (IOException | PythonExecutionException e) {
            e.printStackTrace();
        }
    }

    public void dumpResults(String filename) {
        File csvOutputFileSearch = new File(filename + "search.csv");
        File csvOutputFileInsertion = new File(filename + "insert.csv");
        try (PrintWriter pwSearch = new PrintWriter(csvOutputFileSearch); PrintWriter pwInsertion = new PrintWriter(csvOutputFileInsertion)) {
            searchResults.stream()
                    .map(r -> new String[]{r.tree().getStrategy(), Main.df.format(r.searchTime()), Integer.toString(r.result().distanceFunctionCalls)})
                    .map(this::convertToCSV)
                    .forEach(pwSearch::println);
            insertionResults.stream()
                    .map(r -> new String[]{r.tree().getStrategy(), Main.df.format(r.insertionTime())})
                    .map(this::convertToCSV)
                    .forEach(pwInsertion::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public void testUniqueTree(ArrayList<T> points, ArrayList<T> pointsToSearchFor, Supplier<Result> resultSupplier) {
        ArrayList<MetricDistanceSearchTree<T>> trees = new ArrayList<>();
        for (Supplier<MetricDistanceSearchTree<T>> treeSupplier : treeSuppliers) {
            MetricDistanceSearchTree<T> tree = treeSupplier.get();
            trees.add(tree);
        }

        LinearSearch<T> linearSearch = new LinearSearch<>();
        timeInsertionAndPrint(linearSearch, points);
        trees.forEach(tree -> timeInsertionAndPrint(tree, points));

        System.out.println();

        ArrayList<Result> resultsLinearSearch = new ArrayList<>();
        for (int i = 0; i < pointsToSearchFor.size(); i++) {
            resultsLinearSearch.add(resultSupplier.get());
        }
        averageSearchAndPrint(linearSearch, pointsToSearchFor, resultsLinearSearch);

        System.out.println();

        for (MetricDistanceSearchTree<T> tree : trees) {
            ArrayList<Result> results = new ArrayList<>();
            for (int i = 0; i < pointsToSearchFor.size(); i++) {
                results.add(resultSupplier.get());
            }
            averageSearchAndPrint(tree, pointsToSearchFor, results);
            System.out.println("Average distance function calls: " + results.stream().mapToInt(result -> result.distanceFunctionCalls).average().orElse(0));
            testForSameResult(results, resultsLinearSearch, pointsToSearchFor);
            System.out.println();
        }
    }

    public void test(ArrayList<ArrayList<T>> pointList, ArrayList<T> pointsToSearchFor, Supplier<Result> resultSupplier) {
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

        ArrayList<Result> resultsLinearSearch = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            resultsLinearSearch.add(resultSupplier.get());
        }
        averageSearchAndPrint(linearSearches, pointsToSearchFor, resultsLinearSearch);

        System.out.println();

        for (ArrayList<MetricDistanceSearchTree<T>> trees : treesList) {
            ArrayList<Result> results = new ArrayList<>();
            for (int i = 0; i < pointList.size(); i++) {
                results.add(resultSupplier.get());
            }
            averageSearchAndPrint(trees, pointsToSearchFor, results);
            System.out.println("Average distance function calls: " + results.stream().mapToInt(result -> result.distanceFunctionCalls).average().orElse(0));
            testForSameResult(results, resultsLinearSearch, pointsToSearchFor);
            System.out.println();
        }
        System.out.println("-------------------------------------------\n");
    }

    private void testForSameResult(ArrayList<Result> tested, ArrayList<Result> linearSearchResults, ArrayList<T> pointsTested) {
        for (int i = 0; i < tested.size(); i++) {
            int finalI = i;
            var testedResultPoints = tested.get(i).toArray().stream().map(point -> point.distance(pointsTested.get(finalI))).sorted().toList();
            var linearResultPoints = linearSearchResults.get(i).toArray().stream().map(point -> point.distance(pointsTested.get(finalI))).sorted().toList();
            if (!testedResultPoints.equals(linearResultPoints)) {
                System.out.println("\tFor point \"" + pointsTested.get(i).toString() + "\" the results are different:");
                System.out.println("\t\tTested: " + testedResultPoints);
                System.out.println("\t\tLinear Search: " + linearResultPoints);
            }
        }
    }

    private double timeInsertion(MetricDistanceSearchTree<T> tree, ArrayList<T> points) {
        double start = Main.getSec();
        tree.addNodes(points);
        double elapsed = Main.getSec() - start;
//        insertionResults.add(new InsertionResults(elapsed, tree));
        return elapsed;
    }

    private void timeInsertionAndPrint(MetricDistanceSearchTree<T> tree, ArrayList<T> points) {
        double time = timeInsertion(tree, points);
        System.out.println("Timed insertion " + tree.getStrategy() + ": " + Main.df.format(time));
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
        double elapsed = Main.getSec() - start;
//        searchResults.add(new SearchResults(elapsed, tree, result));
        return elapsed;
    }

    private void timeSearchAndPrint(MetricDistanceSearchTree<T> tree, T point, Result result) {
        double time = timeSearch(tree, point, result);
        System.out.println("Timed search " + tree.getStrategy() + ": " + Main.df.format(time));
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

    private <U extends MetricDistanceSearchTree<T>> double averageSearch(U tree, ArrayList<T> points, ArrayList<Result> results) {
        double totalTime = 0;
        for (int i = 0; i < points.size(); i++) {
            T point = points.get(i);
            Result result = results.get(i);
            totalTime += timeSearch(tree, point, result);
        }
        return totalTime / points.size();
    }

    private <U extends MetricDistanceSearchTree<T>> void averageSearchAndPrint(U tree, ArrayList<T> points, ArrayList<Result> results) {
        double average = averageSearch(tree, points, results);
        System.out.println("Average search " + tree.getStrategy() + ": " + Main.df.format(average));
    }

    private <U extends MetricDistanceSearchTree<T>> void averageSearchAndPrint(ArrayList<U> trees, ArrayList<T> points, ArrayList<Result> results) {
        double average = averageSearch(trees, points, results);
        System.out.println("Average search " + trees.get(0).getStrategy() + ": " + Main.df.format(average));
    }

}
