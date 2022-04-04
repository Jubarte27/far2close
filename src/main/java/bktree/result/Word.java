package bktree.result;

import bktree.point.Point;

public class Word {
    public double dist;
    public Point word;
    Word next;
    Word prev;

    public Word(Point word, double dist) {
        this.dist = dist;
        this.word = word;
    }

    public String toString() {
        String result = "";
        //if (prev!=null)
        //result +=prev.word+"<-";
        result += word + "(" + dist + ")";
        //if (next!=null)
        //result += "->"+next.word;
        return result;
    }
}
