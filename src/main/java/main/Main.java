package main;

import bktree.far2close.Far2CloseTreeNN;
import bktree.far2close.Far2CloseTreeNNSimpleBinary;
import bktree.point.Coordinate;
import bktree.point.StringPoint;
import bktree.result.ClosestResult;
import bktree.vantagePoint.*;
import bktree.vantagePoint.extended.VantagePointExtended;
import tester.Tester;

import java.text.DecimalFormat;
import java.util.Locale;

public class Main {
    public static DecimalFormat df;

    static {
        Locale.setDefault(Locale.ENGLISH);
        df = new DecimalFormat("0.####################################################################################################");
    }
    public static void main(String[] args) {
        double start = getSec();
        int testingIterations = 30;
        int vCount = 10000;
//        int vantagePoints = 2000;
        int dimensions = 3;

//        Tester<Coordinate> tester = new Tester<>((amount) -> Coordinate.randomCoordinates(amount, dimensions));
        Tester<StringPoint> tester = new Tester<>(StringPoint::randomPoints);
//        tester.addTreeSupplier(Far2CloseTreeNNSimpleBinary::new);
        tester.addTreeSupplier(Far2CloseTreeNN::new);
        tester.addTreeSupplier(VantagePoint::new);
//        tester.addTreeSupplier(() -> new VantagePointExtended<>(0.01f));
//        tester.addTreeSupplier(() -> new VantagePointExtended<>(0.1f));
        tester.addTreeSupplier(() -> new VantagePointExtended<>(0.25f));
//        tester.addTreeSupplier(() -> new VantagePointExtended<>(0.5f));
//        tester.addTreeSupplier(() -> new VantagePointExtended<>(0.75f));
//        tester.addTreeSupplier(() -> new VpTree_a<>(vantagePoints, vCount));
//        tester.addTreeSupplier(() -> new VpTree_b<>(vantagePoints, vCount));
//        tester.addTreeSupplier(() -> new MultiVpTree_a<>(vantagePoints, vCount));
//        tester.addTreeSupplier(() -> new MultiVpTree_b<>(vantagePoints, vCount));
        tester.test(testingIterations, vCount, ClosestResult::new);
//        tester.test(testingIterations, vCount, () -> new TopKResult(1));
//        tester.test(testingIterations, vCount, () -> new TopKResult(2));
//        tester.test(testingIterations, vCount, () -> new TopKResult(3));
//        tester.test(testingIterations, vCount, () -> new TopKResult(4));
//        tester.test(testingIterations, vCount, () -> new TopKResult(5));
//        tester.test(testingIterations, vCount, () -> new TopKResult(6));
//        ArrayList<StringPoint> toSearchFor = new ArrayList<>();
//        toSearchFor.add(new StringPoint("partially"));
//        toSearchFor = StringPoint.randomPoints(300);
//        tester.testUniqueTree(StringPoint.allPoints(), StringPoint.randomPoints(testingIterations), () -> new TopKResult(5));

        System.out.println("Total: " + df.format((getSec() - start)));
//        tester.scatter();
    }

    public static double getSec() {
        return System.currentTimeMillis() * 10E-4;
    }
}
