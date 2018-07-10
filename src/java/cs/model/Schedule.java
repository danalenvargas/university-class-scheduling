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
public class Schedule {
    private IntervalST<SchedEntry> usedTree = new IntervalST<>();
    private IntervalST freeTree = new IntervalST();

    public IntervalST<SchedEntry> getUsedTree() {
        return usedTree;
    }

    public void setUsedTree(IntervalST<SchedEntry> usedTree) {
        this.usedTree = usedTree;
    }

    public IntervalST getFreeTree() {
        return freeTree;
    }

    public void setFreeTree(IntervalST freeTree) {
        this.freeTree = freeTree;
    }
}
