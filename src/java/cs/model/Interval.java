/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

/**
 *
 * @author Dan
 */
/******************************************************************************
 *  Compilation:  javac Interval1D.java
 *  Execution:    java Interval1D
 *  
 *  Interval ADT for integer coordinates.
 *
 ******************************************************************************/


public class Interval implements Comparable<Interval> {
    public int low;   // left endpoint
    public int high;  // right endpoint

    // precondition: left <= right
    public Interval(int left, int right) {
        if (left <= right) {
            this.low  = left;
            this.high = right;
        }
        else throw new RuntimeException("Illegal interval");
    }

    // does this interval intersect that one?
    public boolean intersects(Interval that) {
        if (that.high <= this.low) return false;
        if (this.high <= that.low) return false;
        return true;
    }

    // does this interval a intersect b?
    public boolean contains(int x) {
        return (low <= x) && (x <= high);
    }

    public boolean contains(Interval that){
        return (contains(that.low) && contains(that.high));
    }
    
    public int compareTo(Interval that) {
        if      (this.low  < that.low)  return -1;
        else if (this.low  > that.low)  return +1;
        else if (this.high < that.high) return -1;
        else if (this.high > that.high) return +1;
        else                            return  0;
    }

    public String toString() {
        return "[" + low + ", " + high + "]";
    }
}



