package bktree.result;

import bktree.point.Point;

public class RangeQueryResult extends Result {


    public RangeQueryResult(int maxDist) {
        this.maxDist = maxDist;
    }

    public boolean addWord(Point word, double dist) {
        if (maxDist < dist) return false;

        if (first == null) {
            first = new Word(word, dist);
            last = first;
            curSize++;
            return true;
        } else {
            Word newWord = new Word(word, dist);
            first.prev = newWord;
            newWord.next = first;
            first = newWord;
        }
        curSize++;
        return true;
    }

    @Override
    public boolean needsOrder() {
        return false;
    }
}
