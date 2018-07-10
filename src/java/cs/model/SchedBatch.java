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
public class SchedBatch {
    private int schedBatchID;    private int year;    private int sem;    private int version;

    public SchedBatch() {
    }

    public SchedBatch(int schedBatchID, int year, int sem, int version) {
        this.schedBatchID = schedBatchID;
        this.year = year;
        this.sem = sem;
        this.version = version;
    }

    public int getSchedBatchID() {
        return schedBatchID;
    }

    public void setSchedBatchID(int schedBatchID) {
        this.schedBatchID = schedBatchID;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
