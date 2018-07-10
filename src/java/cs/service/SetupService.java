/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import com.google.gson.Gson;
import cs.model.Course;
import cs.model.Department;
import cs.model.Instructor;
import cs.model.Room;
import cs.model.ScheduleCollection;
import cs.model.Section;
import cs.model.Subject;
import cs.model.User;
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
public class SetupService {
    private ScheduleCollection schedCollection;
    
    Connection conn = null;
    DataSource ds;
    InitialContext ctx;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public SetupService() {

    }

    public SetupService(ScheduleCollection schedCollection) {
        this.schedCollection = schedCollection;
    }
    
    public String getSectionList(User currentUser){
        ArrayList<Section> sectionList = new ArrayList<>();
        int sectionId, sectionYear, sectionNumber, courseId;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSection.section_id AS sectionId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.course_id AS courseId "
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
                Course course = SchedEntryCreator.generateCourse(courseId, conn, false);
                
                Section section = new Section(sectionId, sectionYear, sectionNumber, course);
                sectionList.add(section);
            }
            
            String jsonSelectionList = new Gson().toJson(sectionList);
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
    
    public String getCourseList(User currentUser){
        ArrayList<Course> courseList = new ArrayList<>();
        int courseId;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblCourse.course_id AS courseId "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblCourse ON tblDepartment.department_id = tblCourse.fk_department_id "
                    + "WHERE fk_college_id=?  AND tblCourse.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                courseId = rs.getInt("courseId");
                Course course = SchedEntryCreator.generateCourse(courseId, conn, false);
                courseList.add(course);
            }
            
            String jsonSelectionList = new Gson().toJson(courseList);
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
    
    public String getTakenNumbersList(User currentUser, int courseId, int year){
        ArrayList<Integer> numberList = new ArrayList<>();
        int sectionNumber;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSection.number AS sectionNumber "
                    + "FROM tblCourse "
                    + "INNER JOIN tblSection ON tblCourse.course_id = tblSection.fk_course_id "
                    + "WHERE tblCourse.course_id=? AND tblSection.year = ?");
            pst.setString(1, String.valueOf(courseId));
            pst.setString(2, String.valueOf(year));

            rs = pst.executeQuery();

            while (rs.next()) {
                sectionNumber = rs.getInt("sectionNumber");
                numberList.add(sectionNumber);
            }
            
            String jsonSelectionList = new Gson().toJson(numberList);
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
    
    public void addSection(User currentUser, int courseId, int sectionYear, int sectionNumber, int departmentId, int collegeId){
        int sectionId;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblSection(year, number, fk_course_id) VALUES(?,?,?)");
            pst.setString(1, String.valueOf(sectionYear));
            pst.setString(2, String.valueOf(sectionNumber));
            pst.setString(3, String.valueOf(courseId));
            pst.executeUpdate();
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblSection;");
            rs =  pst.executeQuery();
            
            if(rs.next()){
                sectionId = rs.getInt("lastId");
                System.out.println("lastSectionId: " + sectionId );
                schedCollection.addSection(sectionId, departmentId, collegeId);
            }
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
    
    public void deleteSection(User currentUser, int sectionId, int departmentId, int collegeId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblSection SET isActive=0 WHERE section_id=?");
            pst.setString(1, String.valueOf(sectionId));
            pst.executeUpdate();
            
            schedCollection.removeSection(sectionId, departmentId, collegeId);
            
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
    
    public void editSection(int sectionId, int newNumber){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblSection SET number=? WHERE section_id=?");
            pst.setString(1, String.valueOf(newNumber));
            pst.setString(2, String.valueOf(sectionId));
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
    
    public String getRoomList(User currentUser){
        ArrayList<Room> roomList = new ArrayList<>();
        int roomId, departmentId;
        String name;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblRoom.room_id as roomId, tblRoom.name as name, tblRoom.fk_department_id as departmentId "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblRoom ON tblDepartment.department_id = tblRoom.fk_department_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblRoom.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                roomId = rs.getInt("roomId");
                departmentId = rs.getInt("departmentId");
                name = rs.getString("name");
                Department department = SchedEntryCreator.generateDepartment(departmentId, conn);
                
                Room room = new Room(roomId, name, department);
                roomList.add(room);
            }
            
            String jsonSelectionList = new Gson().toJson(roomList);
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
    
    public String getDepartmentList(User currentUser){
        ArrayList<Department> departmentList = new ArrayList<>();
        int departmentId;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblDepartment.department_id AS departmentId "
                    + "FROM tblDepartment "
                    + "WHERE fk_college_id=?");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                departmentId = rs.getInt("departmentId");
                Department department = SchedEntryCreator.generateDepartment(departmentId, conn);
                departmentList.add(department);
            }
            
            String jsonSelectionList = new Gson().toJson(departmentList);
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
    
    public void addRoom(String name, int departmentId, int collegeId){
        int roomId;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblRoom(name, fk_department_id) VALUES(?,?)");
            pst.setString(1, name);
            pst.setString(2, String.valueOf(departmentId));
            pst.executeUpdate();
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblRoom;");
            rs =  pst.executeQuery();
            
            if(rs.next()){
                roomId = rs.getInt("lastId");
                System.out.println("lastRoomId: " + roomId );
                schedCollection.addRoom(roomId, departmentId, collegeId);
            }
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
    
    public void deleteRoom(int roomId, int departmentId, int collegeId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblRoom SET isActive=0 WHERE room_id=?");
            pst.setString(1, String.valueOf(roomId));
            pst.executeUpdate();
            
            schedCollection.removeRoom(roomId, departmentId, collegeId);
            
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
    
    public void editRoom(int roomId, String newName){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblRoom SET name=? WHERE room_id=?");
            pst.setString(1, String.valueOf(newName));
            pst.setString(2, String.valueOf(roomId));
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
    
    public String getInstructorList(User currentUser){
        ArrayList<Instructor> instructorList = new ArrayList<>();
        int instructorId, departmentId;
        String firstName, middleInitial, lastName;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblInstructor.instructor_id as instructorId, tblInstructor.first_name as firstName, tblInstructor.middle_initial as middleInitial, tblInstructor.last_name as lastName, tblInstructor.fk_department_id as departmentId "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblInstructor ON tblDepartment.department_id = tblInstructor.fk_department_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblInstructor.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                instructorId = rs.getInt("instructorId");
                departmentId = rs.getInt("departmentId");
                firstName = rs.getString("firstName");
                middleInitial = rs.getString("middleInitial");
                lastName = rs.getString("lastName");
                Department department = SchedEntryCreator.generateDepartment(departmentId, conn);
                
                Instructor instructor = new Instructor(instructorId, lastName, firstName, middleInitial, department);
                instructorList.add(instructor);
            }
            
            String jsonSelectionList = new Gson().toJson(instructorList);
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
    
    public void addInstructor(String firstName, String middleInitial, String lastName, int departmentId, int collegeId){
        int instructorId;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblInstructor(first_name, middle_initial, last_name, fk_department_id) VALUES(?,?,?,?)");
            pst.setString(1, firstName);
            pst.setString(2, middleInitial);
            pst.setString(3, lastName);
            pst.setString(4, String.valueOf(departmentId));
            pst.executeUpdate();
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblInstructor;");
            rs =  pst.executeQuery();
            
            if(rs.next()){
                instructorId = rs.getInt("lastId");
                schedCollection.addInstructor(instructorId, departmentId, collegeId);
            }
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
    
    public void deleteInstructor(int instructorId, int departmentId, int collegeId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblInstructor SET isActive=0 WHERE instructor_id=?");
            pst.setString(1, String.valueOf(instructorId));
            pst.executeUpdate();
            
            schedCollection.removeInstructor(instructorId, departmentId, collegeId);
            
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
    
    public void editInstructor(int instructorId, String newFirstName, String newMiddleInitial, String newLastName){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblInstructor SET first_name=?, middle_initial=?, last_name=? WHERE instructor_id=?");
            pst.setString(1, newFirstName);
            pst.setString(2, newMiddleInitial);
            pst.setString(3, newLastName);
            pst.setString(4, String.valueOf(instructorId));
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
    
    public String getSubjectList(User currentUser){
        ArrayList<Subject> subjectList = new ArrayList<>();
        int subjectId, departmentId, hours, units;
        String title, code, type;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSubject.subject_id as subjectId, tblSubject.code as code, tblSubject.title as title, tblSubject.hours as hours, tblSubject.type as type, tblSubject.units as units, tblSubject.fk_department_id as departmentId "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblSubject ON tblDepartment.department_id = tblSubject.fk_department_id "
                    + "WHERE tblDepartment.fk_college_id=? AND tblSubject.isActive = 1");
            pst.setString(1, String.valueOf(currentUser.getCollegeID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                subjectId = rs.getInt("subjectId");
                departmentId = rs.getInt("departmentId");
                title = rs.getString("title");
                code = rs.getString("code");
                type = rs.getString("type");
                hours = rs.getInt("hours");
                units = rs.getInt("units");
                Department department = SchedEntryCreator.generateDepartment(departmentId, conn);
                
                Subject subject = new Subject(subjectId, hours, units, code, title, type, department);
                subjectList.add(subject);
            }
            
            String jsonSelectionList = new Gson().toJson(subjectList);
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
    
    public void addSubject(String title, String code, String type, int hours, int units, int departmentId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblSubject(code, title, type, hours, units, fk_department_id) VALUES(?,?,?,?,?,?)");
            pst.setString(1, code);
            pst.setString(2, title);
            pst.setString(3, type);
            pst.setString(4, String.valueOf(hours));
            pst.setString(5, String.valueOf(units));
            pst.setString(6, String.valueOf(departmentId));
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
    
    public void editSubject(int subjectId, String newTitle, String newCode, String newType, int newHours, int newUnits){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblSubject SET title=?, code=?, type=?, hours=?, units=? WHERE subject_id=?");
            pst.setString(1, newTitle);
            pst.setString(2, newCode);
            pst.setString(3, newType);
            pst.setString(4, String.valueOf(newHours));
            pst.setString(5, String.valueOf(newUnits));
            pst.setString(6, String.valueOf(subjectId));
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
    
    public void deleteSubject(int subjectId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblSubject SET isActive=0 WHERE subject_id=?");
            pst.setString(1, String.valueOf(subjectId));
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
    
    public void addCourse(String name, String code, int departmentId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblCourse(code, name, fk_department_id) VALUES(?,?,?)");
            pst.setString(1, code);
            pst.setString(2, name);
            pst.setString(3, String.valueOf(departmentId));
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
    
    public void editCourse(int courseId, String newName, String newCode){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblCourse SET name=?, code=? WHERE course_id=?");
            pst.setString(1, newName);
            pst.setString(2, newCode);
            pst.setString(3, String.valueOf(courseId));
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
    
    public void deleteCourse(int courseId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblCourse SET isActive=0 WHERE course_id=?");
            pst.setString(1, String.valueOf(courseId));
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
    
    public String getCurriculum(int courseId){
        ArrayList<CurriculumEntry> curriculum = new ArrayList<>();
        int curriculumId, subjectId;
        float sem;
        Subject subject;
        CurriculumEntry curriculumEntry;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblCurriculum.curriculum_id as curriculumId, tblCurriculum.sem as sem, tblSubjectList.fk_subject_id as subjectId "
                    + "FROM tblCurriculum "
                    + "INNER JOIN tblSubjectList ON tblCurriculum.curriculum_id = tblSubjectList.fk_curriculum_id "
                    + "WHERE tblCurriculum.fk_course_id=?");
            pst.setString(1, String.valueOf(courseId));

            rs = pst.executeQuery();

            while (rs.next()) {
                curriculumId = rs.getInt("curriculumId");
                sem = rs.getFloat("sem");
                subjectId = rs.getInt("subjectId");
                subject = SchedEntryCreator.generateSubject(subjectId, conn);
                curriculumEntry = new CurriculumEntry(curriculumId, sem, subject);
                        
                curriculum.add(curriculumEntry);
            }
            
            String jsonSelectionList = new Gson().toJson(curriculum);
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
    
    public String getAllSubjects(User currentUser){
        ArrayList<Subject> subjectList = new ArrayList<>();
        int subjectId, departmentId, hours, units;
        String title, code, type;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSubject.subject_id as subjectId, tblSubject.code as code, tblSubject.title as title, tblSubject.hours as hours, tblSubject.type as type, tblSubject.units as units, tblSubject.fk_department_id as departmentId "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblSubject ON tblDepartment.department_id = tblSubject.fk_department_id "
                    + "WHERE tblSubject.isActive = 1");

            rs = pst.executeQuery();

            while (rs.next()) {
                subjectId = rs.getInt("subjectId");
                departmentId = rs.getInt("departmentId");
                title = rs.getString("title");
                code = rs.getString("code");
                type = rs.getString("type");
                hours = rs.getInt("hours");
                units = rs.getInt("units");
                Department department = SchedEntryCreator.generateDepartment(departmentId, conn);
                
                Subject subject = new Subject(subjectId, hours, units, code, title, type, department);
                subjectList.add(subject);
            }
            
            String jsonSelectionList = new Gson().toJson(subjectList);
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
    
    public void addToCurriculum(int subjectId, float sem, int courseId){
        int curriculumId=0;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            pst = conn.prepareStatement("SELECT curriculum_id FROM tblCurriculum WHERE sem =? AND fk_course_id=?");
            pst.setString(1, String.valueOf(sem));
            pst.setString(2, String.valueOf(courseId));
            rs = pst.executeQuery();
            
            if(rs.next()){
                curriculumId = rs.getInt("curriculum_id");
            }else{
                pst = conn.prepareStatement("INSERT INTO tblCurriculum(sem, fk_course_id) VALUES(?,?)");
                pst.setString(1, String.valueOf(sem));
                pst.setString(2, String.valueOf(courseId));
                pst.executeUpdate();
                
                pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblSection;");
                rs =  pst.executeQuery();
                if(rs.next()){
                    curriculumId = rs.getInt("lastId");
                }
            }
            
            if(curriculumId != 0){
                pst = conn.prepareStatement("INSERT INTO tblSubjectList(fk_subject_id, fk_curriculum_id) VALUES(?,?)");
                pst.setString(1, String.valueOf(subjectId));
                pst.setString(2, String.valueOf(curriculumId));
                pst.executeUpdate();
            }
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
    
    public void deleteFromCurriculum(int subjectId, int curriculumId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("DELETE FROM tblSubjectList WHERE fk_subject_id=? AND fk_curriculum_id=?");
            pst.setString(1, String.valueOf(subjectId));
            pst.setString(2, String.valueOf(curriculumId));
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
    
    class CurriculumEntry{
        int curriculumId;
        float sem;
        Subject subject;

        public CurriculumEntry(int curriculumId, float sem, Subject subject) {
            this.curriculumId = curriculumId;
            this.sem = sem;
            this.subject = subject;
        }
    }
}
