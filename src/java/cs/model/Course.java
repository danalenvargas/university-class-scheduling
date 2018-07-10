/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

import java.util.HashMap;
import cs.model.Subject;
/**
 *
 * @author Dan
 */
public class Course {
    private int courseID;
    private String code;
    private String name;
    private Department department;
    private HashMap<Integer, HashMap<Integer, Subject>>  curriculum;
    //<HashMap<semester, HashMap<subjectID, Subject>>>
    public Course() {
    }

    public Course(int courseID, String code, String name, Department department) {
        this.courseID = courseID;
        this.code = code;
        this.name = name;
        this.department = department;
    }

    public Course(int courseID, String code, String name, Department department, HashMap<Integer, HashMap<Integer, Subject>> curriculum) {
        this.courseID = courseID;
        this.code = code;
        this.name = name;
        this.department = department;
        this.curriculum = curriculum;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public HashMap<Integer, HashMap<Integer, Subject>> getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(HashMap<Integer, HashMap<Integer, Subject>> curriculum) {
        this.curriculum = curriculum;
    }

}
