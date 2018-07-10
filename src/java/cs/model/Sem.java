/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

import java.util.Date;

/**
 *
 * @author Dan
 */
public class Sem {
    private int semId, sem, schoolYear;
    private boolean isActive, isFinalized;
    Date entryDate;

    public Sem(int semId, int sem, int schoolYear, boolean isActive, boolean isFinalized, Date entryDate) {
        this.semId = semId;
        this.sem = sem;
        this.schoolYear = schoolYear;
        this.isActive = isActive;
        this.isFinalized = isFinalized;
        this.entryDate = entryDate;
    }

    public int getSemId() {
        return semId;
    }

    public void setSemId(int semId) {
        this.semId = semId;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public int getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(int schoolYear) {
        this.schoolYear = schoolYear;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsFinalized() {
        return isFinalized;
    }

    public void setIsFinalized(boolean isFinalized) {
        this.isFinalized = isFinalized;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
}
