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
public class Section extends Schedule{
    private int sectionID;
    private int year;
    private int number;
    private Course course;
    private String name;
    
    private int departmentID, collegeID; // for tree
    
    public Section() {
    }

    public Section(int sectionID, int year, int number, Course course) {
        this.sectionID = sectionID;
        this.year = year;
        this.number = number;
        this.course = course;
        this.name = course.getCode()+ year + "-" + number;
    }
    
    public Section(int sectionId, int departmentId, int collegeId){ // for tree
        this.sectionID = sectionId;
        this.departmentID = departmentId;
        this.collegeID = collegeId;
    }
    
    public int getSectionID() {
        return sectionID;
    }

    public void setSectionID(int sectionID) {
        this.sectionID = sectionID;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // for tree
    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentId) {
        this.departmentID = departmentId;
    }

    public int getCollegeID() {
        return collegeID;
    }

    public void setCollegeID(int collegeId) {
        this.collegeID = collegeId;
    }
    
    
}
