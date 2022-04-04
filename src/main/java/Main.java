import bktree.far2close.Far2CloseTreeNN;
import bktree.far2close.Far2CloseTreeNNSimpleBinary;
import bktree.linearSearch.LinearSearch;
import bktree.point.Coordinate;
import bktree.point.StringPoint;
import bktree.result.ClosestResult;
import bktree.result.RangeQueryResult;
import bktree.result.TopKResult;
import bktree.vantagePoint.*;

import java.text.DecimalFormat;

public class Main {
    public static DecimalFormat df = new DecimalFormat("0.####################################################################################################");

    public static void main(String[] args) {
        double start = getSec();
        int testingIterations = 30;
        int vCount = 100000;
        int vantagePoints = 2000;
        int dimensions = 3;

        Tester<Coordinate> tester = new Tester<>((amount) -> Coordinate.randomCoordinates(amount, dimensions));
//        Tester<StringPoint> tester = new Tester<>(StringPoint::randomPoints);
        tester.addTreeSupplier(LinearSearch::new);
        tester.addTreeSupplier(Far2CloseTreeNNSimpleBinary::new);
        tester.addTreeSupplier(Far2CloseTreeNN::new);
        tester.addTreeSupplier(VantagePoint::new);
//        tester.addTreeSupplier(() -> new VpTree_a<>(vantagePoints, vCount));
//        tester.addTreeSupplier(() -> new VpTree_b<>(vantagePoints, vCount));
//        tester.addTreeSupplier(() -> new MultiVpTree_a<>(vantagePoints, vCount));
//        tester.addTreeSupplier(() -> new MultiVpTree_b<>(vantagePoints, vCount));
        tester.test(testingIterations, vCount, () -> new TopKResult(1));

        System.out.println("Total: " + df.format((getSec() - start)));
    }

    static double getSec() {
        return System.currentTimeMillis() * 10E-4;
    }
}
