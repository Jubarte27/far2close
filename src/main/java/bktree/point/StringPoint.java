package bktree.point;

import util.StringGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StringPoint implements Point<StringPoint> {
    public final String word;
    public double dist = 0;

    public StringPoint(String w) {
        this.word = w.toLowerCase();
    }

    @Override
    public double distance(StringPoint p) {
        return Levenshtein.distance(word, p.word);
    }

    @Override
    public double optimizedDistance(StringPoint p) {
        return distance(p);
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringPoint that = (StringPoint) o;
        
        return word.equals(that.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
    
        @Override
    public void setDist(Double d) {
        this.dist = d;
    }

    @Override
    public Double getDist() {
        return dist;
    }

    @Override
    public int compareTo(StringPoint o) {
        return Double.compare(this.dist, o.dist);
    }

    private static StringGenerator stringGenerator;

    static {
        try {
            stringGenerator = new StringGenerator("/paperER_data");
        } catch (IOException e) {
            e.printStackTrace();
            stringGenerator = null;
        }
    }

    public static ArrayList<StringPoint> randomPoints(int amount) {
        return stringGenerator.random(amount).stream().map(StringPoint::new).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<StringPoint> allPoints() {
        return stringGenerator.all().stream().map(StringPoint::new).collect(Collectors.toCollection(ArrayList::new));
    }
}
