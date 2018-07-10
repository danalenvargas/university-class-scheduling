/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;
import java.io.Serializable;
/**
 *
 * @author Dan
 */
public class User implements Serializable{

    private String userName;
    private String password;
    private String userType;
    private int departmentID;
    private int collegeID;
    private int userID;
    
    public User(){
    }

    public User(String userName, String password, String userType, int departmentID, int collegeID, int userID) {
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.departmentID = departmentID;
        this.collegeID = collegeID;
        this.userID = userID;
    }
    
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getCollegeID() {
        return collegeID;
    }

    public void setCollegeID(int collegeID) {
        this.collegeID = collegeID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
