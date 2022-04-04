package bktree.result;

import bktree.point.Point;

public class TopKResult extends Result {

    protected int maxSize = -1;

    public TopKResult(int size) {
        this.maxSize = size;
    }


    public void clear() {
        super.clear();
        maxDist = Integer.MAX_VALUE;
    }


    public boolean addWord(Point word, double dist) {
        if (curSize >= maxSize && maxDist <= dist) return false;

        if (first == null) {
            first = new Word(word, dist);
            last = first;

            curSize++;
            if (curSize == maxSize)
                maxDist = dist;

            return true;
        } else {
            Word cur = first;
            Word ant = null;
            while (cur != null) {
                if (cur.dist >= dist) break;
                ant = cur;
                cur = cur.next;
            }
            if (curSize >= maxSize && cur == null)
                return false;

            curSize++;

            Word newWord = new Word(word, dist);
            if (ant == null) {
                newWord.next = first;
                first.prev = newWord;
                first = newWord;
            } else {
                newWord.next = ant.next;
                if (ant.next != null)
                    ant.next.prev = newWord;
                ant.next = newWord;
                newWord.prev = ant;
                if (cur == null)
                    last = newWord;

            }
            if (curSize > maxSize) {
                last.prev.next = null;
                last = last.prev;

                if (last.dist < maxDist)
                    maxDist = last.dist;

                curSize--;
            }


        }
        return true;
    }

    @Override
    public boolean needsOrder() {
        return true;
    }
}
