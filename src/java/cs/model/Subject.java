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
public class Subject {
    private int subjectID;
    private float hours;
    private int units;
    private String code;
    private String title;
    private String type;
    private Department department;
    private float hoursPlotted;

    public Subject() {
    }

    public Subject(int subjectID, float hours, int units, String code, String title, String type, Department department) {
        this.subjectID = subjectID;
        this.hours = hours;
        this.units = units;
        this.code = code;
        this.title = title;
        this.type = type;
        this.department = department;
    } 

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public float getHoursPlotted() {
        return hoursPlotted;
    }
    
    public float getRemainingHours() { // get remaining hours that can be plotted
        return hours - hoursPlotted;
    }

    public void setHoursPlotted(float hoursPlotted) {
        this.hoursPlotted = hoursPlotted;
    }
}
