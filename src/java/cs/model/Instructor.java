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
public class Instructor  extends Schedule{
    private int instructorID;
    private String lastName;
    private String firstName;
    private String middleInitial;
    private String name;
    private Department department;
    
    private int departmentID, collegeID; // for tree
    

    public Instructor() {
    }

    public Instructor(int instructorID, String lastName, String firstName, String middleInitial, Department department) {
        this.instructorID = instructorID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.department = department;
        this.name = lastName + ", " + firstName + " " + middleInitial + ".";
    }
    
    public Instructor(int instructorId, int departmentId, int collegeId){ // for tree
        this.instructorID = instructorId;
        this.departmentID = departmentId;
        this.collegeID = collegeId;
    }
    
    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
