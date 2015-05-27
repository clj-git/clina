package org.clina.core;

/**
 * Created by zjh on 15-5-23.
 */
public class Tuple<X, Y> {
    public final X left;
    public final Y right;

    public Tuple(X left, Y right) {
        this.left = left;
        this.right = right;
    }
}
