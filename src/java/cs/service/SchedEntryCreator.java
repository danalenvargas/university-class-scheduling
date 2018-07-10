/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import cs.model.College;
import cs.model.Course;
import cs.model.Department;
import cs.model.Instructor;
import cs.model.Room;
import cs.model.SchedEntry;
import cs.model.ScheduleCollection;
import cs.model.Section;
import cs.model.Subject;
import cs.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Date;

/**
 *
 * @author Dan
 */
public class SchedEntryCreator {

    public static SchedEntry createSchedEntry(int schedID, int sectionID, int instructorID, int roomID, int subjectID, int start, int end, ScheduleCollection schedCollection, Connection conn) {
        try {
            Section section = generateSection(sectionID, conn);
            Instructor instructor = generateInstructor(instructorID, conn);
            Room room = generateRoom(roomID, conn);
            Subject subject = generateSubject(subjectID, conn);
            SchedEntry schedEntry = new SchedEntry(schedID, start, end, instructor, room, subject, section);

            //schedCollection.addSchedule(schedEntry);
            System.out.println("---- created schedEntry -----");
            return schedEntry;
        } catch (SQLException ex) {
            Logger.getLogger(InitializeListener.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        finally {
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return null;
    }
    
    public static SchedEntry createSchedEntry(int schedID, int sectionID, int instructorID, int roomID, int subjectID, int start, int end, Date timestamp, Connection conn) {
        try {
            Section section = generateSection(sectionID, conn);
            Instructor instructor = generateInstructor(instructorID, conn);
            Room room = generateRoom(roomID, conn);
            Subject subject = generateSubject(subjectID, conn);
            SchedEntry schedEntry = new SchedEntry(schedID, start, end, instructor, room, subject, section, timestamp);
            return schedEntry;
        } catch (SQLException ex) {
            Logger.getLogger(InitializeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static SchedEntry createSchedEntryForTree(int schedID, int sectionID, int instructorID, int roomID, int subjectID, int start, int end, ScheduleCollection schedCollection, Connection conn) {
        try {
            Section section = generateSectionForTree(sectionID, conn);
            Instructor instructor = generateInstructorForTree(instructorID, conn);
            Room room = generateRoomForTree(roomID, conn);
            //Subject subject = generateSubject(subjectID, conn);
            //SchedEntry schedEntry = new SchedEntry(schedID, start, end, instructor, room, subject, section);
            SchedEntry schedEntry = new SchedEntry(schedID, start, end, instructor, room, subjectID, section);
            //schedCollection.addSchedule(schedEntry);
            System.out.println("---- created schedEntryForTree -----");
            return schedEntry;
        } catch (SQLException ex) {
            Logger.getLogger(InitializeListener.class.getName()).log(Level.SEVERE, null, ex);
        } 
//        finally {
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        return null;
    }

    private static Section generateSection(int sectionID, Connection conn) throws SQLException {
        int year, number, courseID;
        Section section = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblSection WHERE section_id=?");
        pst.setString(1, String.valueOf(sectionID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            year = rs.getInt("year");
            number = rs.getInt("number");
            courseID = rs.getInt("fk_course_id");

            Course course = generateCourse(courseID, conn, false);

            section = new Section(sectionID, year, number, course);
        }
        return section;
    }

    private static Section generateSection(int sectionID, int year, int number, int courseID, Connection conn) throws SQLException {
        Course course = generateCourse(courseID, conn, true);
        Section section = new Section(sectionID, year, number, course);
        return section;
    }

    private static Instructor generateInstructor(int instructorID, Connection conn) throws SQLException {
        String lastName, firstName, middleInitial;
        int departmentID;
        Instructor instructor = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblInstructor WHERE instructor_id=?");
        pst.setString(1, String.valueOf(instructorID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            lastName = rs.getString("last_name");
            firstName = rs.getString("first_name");
            middleInitial = rs.getString("middle_initial");
            departmentID = rs.getInt("fk_department_id");
            Department department = generateDepartment(departmentID, conn);

            instructor = new Instructor(instructorID, lastName, firstName, middleInitial, department);
        }
        return instructor;
    }

    private static Instructor generateInstructor(int instructorID, String lastName, String firstName, String middleInitial, int departmentID, Connection conn) throws SQLException {
        Department department = generateDepartment(departmentID, conn);
        Instructor instructor = new Instructor(instructorID, lastName, firstName, middleInitial, department);
        return instructor;
    }

    private static Room generateRoom(int roomID, Connection conn) throws SQLException {
        String name;
        int departmentID;
        Room room = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblRoom WHERE room_id=?");
        pst.setString(1, String.valueOf(roomID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            name = rs.getString("name");
            departmentID = rs.getInt("fk_department_id");
            Department department = generateDepartment(departmentID, conn);

            room = new Room(roomID, name, department);
        }
        return room;
    }

    private static Room generateRoom(int roomID, String name, int departmentID, Connection conn) throws SQLException {
        Department department = generateDepartment(departmentID, conn);
        Room room = new Room(roomID, name, department);
        return room;
    }

    public static Subject generateSubject(int subjectID, Connection conn) throws SQLException {
        int units, departmentID;
        float hours;
        String code, title, type;
        Subject subject = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblSubject WHERE subject_id=?");
        pst.setString(1, String.valueOf(subjectID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            hours = rs.getFloat("hours");
            units = rs.getInt("units");
            code = rs.getString("code");
            title = rs.getString("title");
            type = rs.getString("type");
            departmentID = rs.getInt("fk_department_id");
            Department department = generateDepartment(departmentID, conn);

            subject = new Subject(subjectID, hours, units, code, title, type, department);
        }
        return subject;
    }

    public static Course generateCourse(int courseID, Connection conn, boolean hasChecklist) throws SQLException {
        String code, name;
        int departmentID;
        HashMap<Integer, HashMap<Integer, Subject>> curriculum;
        Course course = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblCourse WHERE course_id=?");
        pst.setString(1, String.valueOf(courseID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            code = rs.getString("code");
            name = rs.getString("name");
            departmentID = rs.getInt("fk_department_id");
            Department department = generateDepartment(departmentID, conn);

            if (hasChecklist) {
                curriculum = generateCurriculum(courseID, conn);
                course = new Course(courseID, code, name, department, curriculum);
            } else {
                course = new Course(courseID, code, name, department);
            }
        }
        return course;
    }

    private static HashMap<Integer, HashMap<Integer, Subject>> generateCurriculum(int courseId, Connection conn) throws SQLException {
        HashMap<Integer, HashMap<Integer, Subject>> curriculum = new HashMap<>();
        HashMap<Integer, Subject> subjectList;
        //<HashMap<semester, HashMap<subjectID, Subject>>>
        int sem, subjectId;
        Subject subject;
        PreparedStatement pst = conn.prepareStatement("SELECT tblCurriculum.*, tblSubjectList.fk_subject_id FROM tblCurriculum  INNER JOIN tblsubjectlist\n"
                + "ON tblcurriculum.curriculum_id = tblsubjectlist.fk_curriculum_id WHERE tblCurriculum.fk_course_id = ?");
        pst.setString(1, String.valueOf(courseId));
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            sem = rs.getInt("sem");
            subjectId = rs.getInt("fk_subject_id");
            subject = generateSubject(subjectId, conn);

            subjectList = curriculum.putIfAbsent(sem, new HashMap<>());
            if (subjectList == null) {
                subjectList = curriculum.get(sem);
            }
            subjectList.put(subject.getSubjectID(), subject);
        }
        return curriculum;
    }

    public static Department generateDepartment(int departmentID, Connection conn) throws SQLException {
        String code, name;
        int collegeID;
        Department department = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblDepartment WHERE department_id=?");
        pst.setString(1, String.valueOf(departmentID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            code = rs.getString("code");
            name = rs.getString("name");
            collegeID = rs.getInt("fk_college_id");

            College college = generateCollege(collegeID, conn);

            department = new Department(departmentID, code, name, college);
        }
        return department;
    }

    private static College generateCollege(int collegeID, Connection conn) throws SQLException {
        String code, name;
        College college = null;

        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblCollege WHERE college_id=?");
        pst.setString(1, String.valueOf(collegeID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            code = rs.getString("code");
            name = rs.getString("name");

            college = new College(collegeID, code, name);
        }
        return college;
    }
    
    // COPIED CODE, REPEATING INFORMATION, MUST CLEAN CODE LATER
    
    private static Section generateSectionForTree(int sectionId, Connection conn) throws SQLException {
        int departmentId, collegeId;
        Section section = null;

        PreparedStatement pst = conn.prepareStatement("SELECT tblDepartment.department_id AS departmentId, tblDepartment.fk_college_id AS collegeId "
                + "FROM tblSection "
                + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                + "INNER JOIN tblDepartment ON tblDepartment.department_id = tblCourse.fk_department_id "
                + "WHERE section_id=?");
        pst.setString(1, String.valueOf(sectionId));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            departmentId = rs.getInt("departmentId");
            collegeId = rs.getInt("collegeId");
            
            section = new Section(sectionId, departmentId, collegeId);
        }
        return section;
    }
    
    private static Instructor generateInstructorForTree(int instructorId, Connection conn) throws SQLException {
        int departmentId, collegeId;
        Instructor instructor = null;

        PreparedStatement pst = conn.prepareStatement("SELECT tblDepartment.department_id AS departmentId, tblDepartment.fk_college_id AS collegeId "
                + "FROM tblInstructor "
                + "INNER JOIN tblDepartment ON tblDepartment.department_id = tblInstructor.fk_department_id "
                + "WHERE instructor_id=?");
        pst.setString(1, String.valueOf(instructorId));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            departmentId = rs.getInt("departmentId");
            collegeId = rs.getInt("collegeId");
            
            instructor = new Instructor(instructorId, departmentId, collegeId);
        }
        return instructor;
    }
    
    private static Room generateRoomForTree(int roomId, Connection conn) throws SQLException {
        int departmentId, collegeId;
        Room room = null;

        PreparedStatement pst = conn.prepareStatement("SELECT tblDepartment.department_id AS departmentId, tblDepartment.fk_college_id AS collegeId "
                + "FROM tblRoom "
                + "INNER JOIN tblDepartment ON tblDepartment.department_id = tblRoom.fk_department_id "
                + "WHERE room_id=?");
        pst.setString(1, String.valueOf(roomId));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            departmentId = rs.getInt("departmentId");
            collegeId = rs.getInt("collegeId");
            
            room = new Room(roomId, departmentId, collegeId);
        }
        return room;
    }
    
    public static User generateUser(int userID, Connection conn) throws SQLException {
        int userId, departmentId, collegeId;
        String name, password, userType;
        User user = null;
        
        PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblUser WHERE user_id=?");
        pst.setString(1, String.valueOf(userID));

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
                userId = rs.getInt("user_id");
                departmentId = rs.getInt("fk_department_id");
                collegeId = rs.getInt("fk_college_id");
                name = rs.getString("name");
                password = rs.getString("password");
                userType = rs.getString("user_type");
                
                user = new User(name, password, userType, departmentId, collegeId, userId);
        }
        return user;
    }
}
