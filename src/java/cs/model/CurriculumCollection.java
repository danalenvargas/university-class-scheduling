/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

import java.util.HashMap;

/**
 *
 * @author Dan
 */
public class CurriculumCollection {
    HashMap<Integer, CourseElement> courseCollection = new HashMap<>();
    
    class CourseElement{
        HashMap<Integer, SemElement> semCollection = new HashMap<>();
    }
    
    class SemElement{
        HashMap<Integer, Subject> subjectList = new HashMap<>();
    }
    
//    public HashMap<Integer, Subject> getSubjectList(int courseId, int sem){
//    
//    }
}
