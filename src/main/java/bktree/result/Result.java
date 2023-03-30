package bktree.result;

import bktree.point.Point;

import java.util.ArrayList;

public abstract class Result {
    protected Word first;
    protected Word last;

    protected int curSize = 0;
    public double maxDist = Double.POSITIVE_INFINITY;
    public int distanceFunctionCalls = 0;


    public int length() {
        return curSize;
    }

    public void clear() {
        first = null;
        last = null;
        curSize = 0;
    }

    public abstract boolean needsOrder();

    public abstract boolean addWord(Point word, double dist);

    public ArrayList<Point> toArray() {
        ArrayList<Point> list = new ArrayList<Point>();
        Word f = first;
        while (f != null) {
            list.add(f.word);
            f = f.next;
        }
        return list;
    }

    public ArrayList<Word> toWordArray() {
        ArrayList<Word> list = new ArrayList<Word>();
        Word f = first;
        while (f != null) {
            list.add(f);
            f = f.next;
        }
        return list;
    }


}
