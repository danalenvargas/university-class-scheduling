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
public class Department {
    private int departmentID;
    private String code;
    private String name;
    private College college;

    public Department() {
    }

    public Department(int department_id, String code, String name, College college) {
        this.departmentID = department_id;
        this.code = code;
        this.name = name;
        this.college = college;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }
}
