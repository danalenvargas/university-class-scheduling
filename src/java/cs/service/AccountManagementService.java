/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import com.google.gson.Gson;
import cs.model.College;
import cs.model.User;
import cs.service.SchedSharingService.CheckboxTreeNode;
import cs.service.SchedSharingService.SharedEntry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Dan
 */
public class AccountManagementService {
    Connection conn = null;
    DataSource ds;
    InitialContext ctx;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public String getMySectionsList(User currentUser){
        ArrayList<SchedSharingService.CheckboxTreeNode> mySectionsList = new ArrayList<>();
        int sectionId, sectionYear, sectionNumber, courseId, departmentId;
        String courseCode, departmentCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSection.section_id AS sectionId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.course_id AS courseId, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblCourse ON tblDepartment.department_id = tblCourse.fk_department_id "
                    + "INNER JOIN tblSection ON tblCourse.course_id = tblSection.fk_course_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblSection.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                sectionYear = rs.getInt("sectionYear");
                sectionNumber = rs.getInt("sectionNumber");
                courseId = rs.getInt("courseId");
                courseCode = rs.getString("courseCode");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                
                SharedEntry section = new SharedEntry(sectionYear, sectionNumber, departmentCode, courseCode, sectionId, departmentId, courseId);
                //sectionsList.add(section);
                addToMySectionsTree(mySectionsList, section);
            }
            
            String jsonSelectionList = new Gson().toJson(mySectionsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getMyRoomsList(User currentUser){
        ArrayList<CheckboxTreeNode> myRoomsList = new ArrayList<>();
        int roomId, departmentId;
        String roomName, departmentCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblRoom.room_id AS roomId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblRoom ON tblDepartment.department_id = tblRoom.fk_department_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblRoom.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                roomId = rs.getInt("roomId");
                roomName = rs.getString("roomName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                
                SchedSharingService.SharedEntry room = new SchedSharingService.SharedEntry(roomName, departmentCode, roomId, departmentId);
                addToMyRoomsTree(myRoomsList, room);
            }
            
            String jsonSelectionList = new Gson().toJson(myRoomsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getMySubjectsList(User currentUser){
        ArrayList<CheckboxTreeNode> mySubjectsList = new ArrayList<>();
        int subjectId, departmentId;
        String code, title, type, departmentCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSubject.code AS code, tblSubject.title AS title, tblSubject.type AS type, tblSubject.subject_id AS subjectId, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblSubject ON tblDepartment.department_id = tblSubject.fk_department_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblSubject.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                subjectId = rs.getInt("subjectId");
                code = rs.getString("code");
                title = rs.getString("title");
                type = rs.getString("type");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                
                SchedSharingService.SharedEntry subject = new SchedSharingService.SharedEntry(code, title, type, departmentCode, subjectId, departmentId);
                addToMySubjectsTree(mySubjectsList, subject);
            }
            
            String jsonSelectionList = new Gson().toJson(mySubjectsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public String getMyInstructorsList(User currentUser){
        ArrayList<CheckboxTreeNode> myInstructorsList = new ArrayList<>();
        int instructorId, departmentId;
        String firstName, middleInitial, lastName, departmentCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblInstructor.first_name AS firstName, tblInstructor.middle_initial AS middleInitial, tblInstructor.last_name AS lastName, tblInstructor.instructor_id AS instructorId, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblInstructor ON tblDepartment.department_id = tblInstructor.fk_department_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblInstructor.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                instructorId = rs.getInt("instructorId");
                firstName = rs.getString("firstName");
                middleInitial = rs.getString("middleInitial");
                lastName = rs.getString("lastName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                
                SchedSharingService.SharedEntry subject = new SchedSharingService.SharedEntry(firstName, middleInitial, lastName, instructorId, departmentCode, departmentId);
                addToMyInstructorsTree(myInstructorsList, subject);
            }
            
            String jsonSelectionList = new Gson().toJson(myInstructorsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getSchedulersList(User currentUser){
        ArrayList<User> schedulersList = new ArrayList<>();
        int userId, departmentId, collegeId;
        String name, password, userType;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT * FROM tblUser WHERE fk_college_id=? AND user_type=?");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));
            pst.setString(2, "SCHEDULER");

            rs = pst.executeQuery();

            while (rs.next()) {
                userId = rs.getInt("user_id");
                departmentId = rs.getInt("fk_department_id");
                collegeId = rs.getInt("fk_college_id");
                name = rs.getString("name");
                password = rs.getString("password");
                userType = rs.getString("user_type");
                
                User user = new User(name, password, userType, departmentId, collegeId, userId);
                schedulersList.add(user);
            }
            
            String jsonSelectionList = new Gson().toJson(schedulersList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getColregList(User currentUser){
        ArrayList<User> colregList = new ArrayList<>();
        int userId, collegeId;
        String name, password;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT * FROM tblUser WHERE user_type=?");
            pst.setString(1, "COLLEGE_REGISTRAR");

            rs = pst.executeQuery();

            while (rs.next()) {
                userId = rs.getInt("user_id");
                collegeId = rs.getInt("fk_college_id");
                name = rs.getString("name");
                password = rs.getString("password");
                
                User user = new User(name, password, "COLLEGE_REGISTRAR", 0, collegeId, userId);
                colregList.add(user);
            }
            
            String jsonSelectionList = new Gson().toJson(colregList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getAssignedSections(int schedulerId) {
        ArrayList<Integer> sectionsList = new ArrayList<>();
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT fk_section_id AS sectionId FROM tblSectionEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            rs = pst.executeQuery();
            while (rs.next()) {
                sectionsList.add(rs.getInt("sectionId"));
            }
            String jsonSelectionList = new Gson().toJson(sectionsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getAssignedRooms(int schedulerId) {
        ArrayList<Integer> roomsList = new ArrayList<>();
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT fk_room_id AS roomId FROM tblRoomEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            rs = pst.executeQuery();
            while (rs.next()) {
                roomsList.add(rs.getInt("roomId"));
            }
            String jsonSelectionList = new Gson().toJson(roomsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getAssignedSubjects(int schedulerId) {
        ArrayList<Integer> subjectsList = new ArrayList<>();
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT fk_subject_id AS subjectId FROM tblSubjectEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            rs = pst.executeQuery();
            while (rs.next()) {
                subjectsList.add(rs.getInt("subjectId"));
            }
            String jsonSelectionList = new Gson().toJson(subjectsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getAssignedInstructors(int schedulerId) {
        ArrayList<Integer> intructorsList = new ArrayList<>();
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT fk_instructor_id AS instructorId FROM tblInstructorEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            rs = pst.executeQuery();
            while (rs.next()) {
                intructorsList.add(rs.getInt("instructorId"));
            }
            String jsonSelectionList = new Gson().toJson(intructorsList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public String getCanEditTime(int schedulerId) {
        int canEditTime=-1;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT canEditTime FROM tblCanEditTime WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            rs = pst.executeQuery();
            if (rs.next()) {
                canEditTime = rs.getInt("canEditTime");
            }
            String jsonSelectionList = new Gson().toJson(canEditTime);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    
    //MySectionsTree structure - compatible with Angular IVH Treeview, the library used to create the nested checkbox list in the shareSections modal in colreg_collab.jsp
    private void addToMySectionsTree(ArrayList<CheckboxTreeNode> list, SharedEntry section){
        boolean departmentFound=false, courseFound=false;
        
        for(CheckboxTreeNode department : list){
            if(department.label.equals(section.departmentName)){
                departmentFound = true;
                for(CheckboxTreeNode course : department.children){
                    if(course.label.equals(section.course)){
                        courseFound = true;
                        CheckboxTreeNode sectionNode = new CheckboxTreeNode(section.sectionName, section);
                        course.addChild(sectionNode);
                        return;
                    }
                }

                if(!courseFound) {
                    CheckboxTreeNode courseNode = new CheckboxTreeNode(section.course);
                    department.addChild(courseNode);
                    CheckboxTreeNode sectionNode = new CheckboxTreeNode(section.sectionName, section);
                    department.children.get(department.children.size()-1).addChild(sectionNode);
                    return;
                }
            }
        }

        if(!departmentFound) {
            CheckboxTreeNode departmentNode = new CheckboxTreeNode(section.departmentName);
            list.add(departmentNode);
            CheckboxTreeNode courseNode = new CheckboxTreeNode(section.course);
            list.get(list.size()-1).addChild(courseNode);
            CheckboxTreeNode sectionNode = new CheckboxTreeNode(section.sectionName, section);
            list.get(list.size()-1).children.get(0).addChild(sectionNode);
            return;
        }
           
    }
    
    //MyRoomsTree structure - compatible with Angular IVH Treeview, the library used to create the nested checkbox list in the shareRooms modal in colreg_collab.jsp
    private void addToMyRoomsTree(ArrayList<CheckboxTreeNode> list, SharedEntry room){
        boolean departmentFound=false;
        
        for(CheckboxTreeNode department : list){
            if(department.label.equals(room.departmentName)){
                departmentFound = true;
                CheckboxTreeNode roomNode = new CheckboxTreeNode(room.roomName, room);
                department.addChild(roomNode);
                return;
            }
        }

        if(!departmentFound) {
            CheckboxTreeNode departmentNode = new CheckboxTreeNode(room.departmentName);
            list.add(departmentNode);
            CheckboxTreeNode roomNode = new CheckboxTreeNode(room.roomName, room);
            list.get(list.size()-1).addChild(roomNode);
            return;
        } 
    }
    
    private void addToMySubjectsTree(ArrayList<CheckboxTreeNode> list, SharedEntry subject){
        boolean departmentFound=false;
        
        for(CheckboxTreeNode department : list){
            if(department.label.equals(subject.departmentName)){
                departmentFound = true;
                CheckboxTreeNode subjectNode = new CheckboxTreeNode(subject.subjectName, subject);
                department.addChild(subjectNode);
                return;
            }
        }

        if(!departmentFound) {
            CheckboxTreeNode departmentNode = new CheckboxTreeNode(subject.departmentName);
            list.add(departmentNode);
            CheckboxTreeNode subjectNode = new CheckboxTreeNode(subject.subjectName, subject);
            list.get(list.size()-1).addChild(subjectNode);
            return;
        } 
    }
    
    private void addToMyInstructorsTree(ArrayList<CheckboxTreeNode> list, SharedEntry instructor){
        boolean departmentFound=false;
        
        for(CheckboxTreeNode department : list){
            if(department.label.equals(instructor.departmentName)){
                departmentFound = true;
                CheckboxTreeNode instructorNode = new CheckboxTreeNode(instructor.instructorName, instructor);
                department.addChild(instructorNode);
                return;
            }
        }

        if(!departmentFound) {
            CheckboxTreeNode departmentNode = new CheckboxTreeNode(instructor.departmentName);
            list.add(departmentNode);
            CheckboxTreeNode instructorNode = new CheckboxTreeNode(instructor.instructorName, instructor);
            list.get(list.size()-1).addChild(instructorNode);
            return;
        } 
    }
    
    public void addScheduler(int departmentId, String name, String password, int canEditTime, int[] selectedSections, int[] selectedRooms, int[] selectedInstructors, int[] selectedSubjects, User currentUser){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            int schedulerId=0;

            pst = conn.prepareStatement("INSERT INTO tblUser(name, password, user_type, fk_department_id, fk_college_id) VALUES(?,?,?,?,?)");
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setString(3, "SCHEDULER");
            pst.setString(4, String.valueOf(departmentId));
            pst.setString(5, String.valueOf(currentUser.getCollegeID()));
            pst.executeUpdate();
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblUser;");
            rs =  pst.executeQuery();
            if(rs.next()){
                schedulerId = rs.getInt("lastId");
            }
            
            if(selectedSections != null){
                pst = conn.prepareStatement("INSERT INTO tblSectionEditor(fk_section_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedSections.length; i++){
                    pst.setString(1, String.valueOf(selectedSections[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            if(selectedSubjects != null){
                pst = conn.prepareStatement("INSERT INTO tblSubjectEditor(fk_subject_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedSubjects.length; i++){
                    pst.setString(1, String.valueOf(selectedSubjects[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            if(selectedRooms != null){
                pst = conn.prepareStatement("INSERT INTO tblRoomEditor(fk_room_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedRooms.length; i++){
                    pst.setString(1, String.valueOf(selectedRooms[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            if(selectedInstructors != null){
                pst = conn.prepareStatement("INSERT INTO tblInstructorEditor(fk_instructor_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedInstructors.length; i++){
                    pst.setString(1, String.valueOf(selectedInstructors[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            pst = conn.prepareStatement("INSERT INTO tblCanEditTime(fk_user_id, canEditTime) VALUES(?,?)");
            pst.setString(1, String.valueOf(schedulerId));
            pst.setString(2, String.valueOf(canEditTime));
            pst.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void addColreg(int collegeId, String name, String password){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblUser(name, password, user_type, fk_college_id) VALUES(?,?,?,?)");
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setString(3, "COLLEGE_REGISTRAR");
            pst.setString(4, String.valueOf(collegeId));
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void editScheduler(int schedulerId, int departmentId, String name, String password, int canEditTime, int[] selectedSections, int[] selectedRooms, int[] selectedInstructors, int[] selectedSubjects, User currentUser){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblUser SET name=?, password=?, fk_department_id=? WHERE user_id=?");
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setString(3, String.valueOf(departmentId));
            pst.setString(4, String.valueOf(schedulerId));
            pst.executeUpdate();
            
            pst = conn.prepareStatement("DELETE FROM tblSectionEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            pst.executeUpdate();
            if(selectedSections != null){
                pst = conn.prepareStatement("INSERT INTO tblSectionEditor(fk_section_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedSections.length; i++){
                    pst.setString(1, String.valueOf(selectedSections[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            pst = conn.prepareStatement("DELETE FROM tblSubjectEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            pst.executeUpdate();
            if(selectedSubjects != null){
                pst = conn.prepareStatement("INSERT INTO tblSubjectEditor(fk_subject_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedSubjects.length; i++){
                    pst.setString(1, String.valueOf(selectedSubjects[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            pst = conn.prepareStatement("DELETE FROM tblRoomEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            pst.executeUpdate();
            if(selectedRooms != null){
                pst = conn.prepareStatement("INSERT INTO tblRoomEditor(fk_room_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedRooms.length; i++){
                    pst.setString(1, String.valueOf(selectedRooms[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            pst = conn.prepareStatement("DELETE FROM tblInstructorEditor WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(schedulerId));
            pst.executeUpdate();
            if(selectedInstructors != null){
                pst = conn.prepareStatement("INSERT INTO tblInstructorEditor(fk_instructor_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
                for(int i=0; i<selectedInstructors.length; i++){
                    pst.setString(1, String.valueOf(selectedInstructors[i]));
                    pst.setString(2, String.valueOf(schedulerId));
                    pst.setString(3, String.valueOf(currentUser.getUserID()));
                    pst.executeUpdate();
                }
            }
            
            pst = conn.prepareStatement("UPDATE tblCanEditTime SET canEditTime=? WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(canEditTime));
            pst.setString(2, String.valueOf(schedulerId));
            pst.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void editColreg(int colregId, String name, String password){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblUser SET name=?, password=? WHERE user_id=?");
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setString(3, String.valueOf(colregId));
            pst.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public String getCollegeList(){
        ArrayList<College> collegeList = new ArrayList<>();
        int collegeId;
        College college = null;
        String code, name;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT * FROM tblCollege");

            rs = pst.executeQuery();

            while (rs.next()) {
                collegeId = rs.getInt("college_id");
                code = rs.getString("code");
                name = rs.getString("name");
                college = new College(collegeId, code, name);
                collegeList.add(college);
            }
            
            String jsonSelectionList = new Gson().toJson(collegeList);
            return jsonSelectionList;
        } catch (SQLException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
