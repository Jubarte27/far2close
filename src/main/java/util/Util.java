package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class Util {
    private static final Random random = new Random();

    public static double medianDouble(List<Double> array) {
        if (array.size() % 2 == 0) {
            return 0.5 *
                    (quickSelectGeneric(array, array.size() / 2, Double::compareTo) + quickSelectGeneric(array, (array.size() / 2) - 1, Double::compareTo));
        } else {
            return quickSelectGeneric(array, array.size() / 2, Double::compareTo);
        }
    }

    public static <T> T middleElement(List<T> array, BiFunction<T, T, Integer> comparisonFunction) {
        return quickSelectGeneric(array, array.size() / 2, comparisonFunction);
    }

    public static <T> T middleElement(T[] array, BiFunction<T, T, Integer> comparisonFunction) {
        return quickSelectGeneric(array, array.length / 2, comparisonFunction);
    }

    public static <T> T quickSelectGeneric(Object[] array, int k, BiFunction<T, T, Integer> comparisonFunction) {
        if (array.length == 1) {
            return (T) array[0];
        }

        T pivot = (T) array[random.nextInt(array.length)];
        Object[] smaller = new Object[array.length];
        Object[] greater = new Object[array.length];

        int smallerCount = 0;
        int greaterCount = 0;
        int equalCount = 0;

        for (Object t : array) {
            int result = comparisonFunction.apply((T) t, pivot);
            if (result > 0) {
                greater[greaterCount] = t;
                greaterCount++;
            } else if (result < 0) {
                smaller[smallerCount] = t;
                smallerCount++;
            } else {
                equalCount++;
            }
        }

        T selected;
        if (k < smallerCount) {
            selected = quickSelectGeneric(Arrays.copyOfRange(smaller, 0, smallerCount), k, comparisonFunction);
        } else {
            if (k < (smallerCount + equalCount)) {
                selected = pivot;
            } else {
                int nextK = k - (smallerCount + equalCount);
                selected = quickSelectGeneric(Arrays.copyOfRange(greater, 0, greaterCount), nextK, comparisonFunction);
            }
        }
        return selected;
    }

    public static <T> T quickSelectGeneric(List<T> array, int k, BiFunction<T, T, Integer> comparisonFunction) {
        if (array.size() == 1) {
            return array.get(0);
        }

        T pivot = array.get(random.nextInt(array.size()));
        ArrayList<T> smaller = new ArrayList<>();
        ArrayList<T> greater = new ArrayList<>();
        ArrayList<T> equal = new ArrayList<>();

        for (T t : array) {
            int result = comparisonFunction.apply(t, pivot);
            if (result > 0) {
                greater.add(t);
            } else if (result < 0) {
                smaller.add(t);
            } else {
                equal.add(t);
            }
        }

        T selected;
        if (k < smaller.size()) {
            selected = quickSelectGeneric(smaller, k, comparisonFunction);
        } else {
            if (k < (smaller.size() + equal.size())) {
                selected = pivot;
            } else {
                int nextK = k - (smaller.size() + equal.size());
                selected = quickSelectGeneric(greater, nextK, comparisonFunction);
            }
        }
        return selected;
    }
}
