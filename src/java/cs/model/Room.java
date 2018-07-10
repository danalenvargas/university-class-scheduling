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
public class Room  extends Schedule{
    private int roomID;
    private String name;
    private Department department;
    
    private int departmentID, collegeID; // for tree

    public Room() {
    }

    public Room(int roomID, String name, Department department) {
        this.roomID = roomID;
        this.name = name;
        this.department = department;
    }
    
    public Room(int roomId, int departmentId, int collegeId){ // for tree
        this.roomID = roomId;
        this.departmentID = departmentId;
        this.collegeID = collegeId;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
