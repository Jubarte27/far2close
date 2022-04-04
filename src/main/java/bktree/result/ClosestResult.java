package bktree.result;

import bktree.point.Point;

public class ClosestResult extends Result {


    public void clear() {
        super.clear();
        maxDist = Integer.MAX_VALUE;
    }

    public boolean addWord(Point word, double dist) {
        if (maxDist < dist) return false;

        if (first == null) {
            first = new Word(word, dist);
            last = first;
            maxDist = dist;
            curSize++;
            return true;
        } else {
            Word newWord = new Word(word, dist);
            if (first.dist > dist) {
                maxDist = dist;
                first = newWord;
                last = newWord;
                curSize = 1;
            } else {
                first.prev = newWord;
                newWord.next = first;
                first = newWord;
                curSize++;
            }

        }

        return true;
    }

    @Override
    public boolean needsOrder() {
        return true;
    }
}
