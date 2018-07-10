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
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import cs.model.ScheduleCollection;
import cs.model.SchedEntry;
import cs.model.Section;
import cs.model.Sem;
import cs.model.Subject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Dan
 */
public class InitializeListener implements ServletContextListener {
    
    public void contextInitialized(ServletContextEvent sce) {
        initializeSchedCollection(sce.getServletContext());
    }
    
    public static void initializeSchedCollection(ServletContext sc){
        ScheduleCollection schedCollection = new ScheduleCollection();
        
//        ArrayList<Section> sectionList;
//        ArrayList<Room> roomList;
//        ArrayList<Instructor> instructorList;

        //Populate
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;

        // v to remove
        int schedID, sectionID, start, end, instructorID, roomID, subjectID, year, number, courseID, departmentID;
        String course, name;
        //^ to remove
        
        int sectionId, roomId, instructorId, departmentId, collegeId;
        int semId=0;

        try {
            System.out.println("CHECKPOINT 1");
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            //** getting semId and global variable sem
            pst = conn.prepareStatement("SELECT * from tblSem WHERE isActive = 1");
            rs = pst.executeQuery();
            if(rs.next()){
                semId = rs.getInt("sem_id");
                int sem = rs.getInt("sem");
                int schoolYear = rs.getInt("school_year");
                boolean isActive = rs.getBoolean("isActive");
                boolean isFinalized = rs.getBoolean("isFinalized");
                Timestamp ts = rs.getTimestamp("entryDate");
                Date entryDate = ts;
                
                Sem activeSem = new Sem(semId, sem, schoolYear, isActive, isFinalized, entryDate);
                sc.setAttribute("activeSem", activeSem);
            }
            
            //// ============================ populating ScheduleCollection
            
            //** adding all sections
            pst = conn.prepareStatement("SELECT tblSection.section_id AS sectionId, tblDepartment.department_id AS departmentId, tblDepartment.fk_college_id AS collegeId "
                    + "FROM tblSection "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "WHERE tblSection.isActive = 1");
            rs = pst.executeQuery();
                System.out.println("CHECKPOINT 2");

            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                departmentId = rs.getInt("departmentId");
                collegeId = rs.getInt("collegeId");

                schedCollection.addSection(sectionId, departmentId, collegeId);
            }

            //** adding all rooms
            pst = conn.prepareStatement("SELECT tblRoom.room_id AS roomId, tblDepartment.department_id AS departmentId, tblDepartment.fk_college_id AS collegeId "
                    + "FROM tblRoom "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "WHERE isActive = 1");
            rs = pst.executeQuery();
                System.out.println("CHECKPOINT 3");

            while (rs.next()) {
                roomId = rs.getInt("roomId");
                departmentId = rs.getInt("departmentId");
                collegeId = rs.getInt("collegeId");
                
                schedCollection.addRoom(roomId, departmentId, collegeId);
            }

            //** adding all instructors
            pst = conn.prepareStatement("SELECT tblInstructor.instructor_id AS instructorId, tblDepartment.department_id AS departmentId, tblDepartment.fk_college_id AS collegeId "
                    + "FROM tblInstructor "
                    + "INNER JOIN tblDepartment ON tblInstructor.fk_department_id = tblDepartment.department_id "
                    + "WHERE isActive = 1");
            rs = pst.executeQuery();
                System.out.println("CHECKPOINT 4");

            while (rs.next()) {
                instructorId = rs.getInt("instructorId");
                departmentId = rs.getInt("departmentId");
                collegeId = rs.getInt("collegeId");

                schedCollection.addInstructor(instructorId, departmentId, collegeId);
            }

            //// =============================== POPULATING SCHEDULE TREES
            pst = conn.prepareStatement("SELECT * FROM tblSchedule WHERE isActive = 1 AND fk_sem_id = ?");
            pst.setString(1, String.valueOf(semId));
            rs = pst.executeQuery();
            
                System.out.println("CHECKPOINT 4");
            while (rs.next()) {
                sectionID = rs.getInt("fk_section_id");
                schedID = rs.getInt("sched_id");
//                schedBatchID = rs.getInt("fk_schedBatch_id");
                instructorID = rs.getInt("fk_instructor_id");
                roomID = rs.getInt("fk_room_id");
                subjectID = rs.getInt("fk_subject_id");
                start = rs.getInt("start");
                end = rs.getInt("end");

                Section section = generateSectionForTree(sectionID, conn);
                Instructor instructor = generateInstructorForTree(instructorID, conn);
                Room room = generateRoomForTree(roomID, conn);
                //Subject subject = generateSubject(subjectID, conn);
                //SchedEntry schedEntry = new SchedEntry(schedID, start, end, instructor, room, subject, section);
                SchedEntry schedEntry = new SchedEntry(schedID, start, end, instructor, room, subjectID, section);

                schedCollection.addSchedule(schedEntry);
                System.out.println("---- added schedule -----");
            }

                System.out.println("CHECKPOINT 5");
            sc.setAttribute("schedCollection", schedCollection);
            Integer changeCounter = 0;
            sc.setAttribute("changeCounter", changeCounter);

                System.out.println("CHECKPOINT 6");
        } catch (NamingException ex) {
            Logger.getLogger(InitializeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(InitializeListener.class.getName()).log(Level.SEVERE, null, ex);
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

    private Section generateSection(int sectionID, Connection conn) throws SQLException {
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

    private static Room generateRoom(int roomID, String name, int departmentID, Connection conn) throws SQLException {
        Department department = generateDepartment(departmentID, conn);
        Room room = new Room(roomID, name, department);
        return room;
    }

    private static Subject generateSubject(int subjectID, Connection conn) throws SQLException {
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

    private static Course generateCourse(int courseID, Connection conn, boolean hasChecklist) throws SQLException {
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
            
            if(hasChecklist){
                curriculum = generateCurriculum(courseID, conn);
                course = new Course(courseID, code, name, department, curriculum);
            }else{
                course = new Course(courseID, code, name, department);
            }
        }
        return course;
    }
    
    private static HashMap<Integer, HashMap<Integer, Subject>> generateCurriculum(int courseId, Connection conn) throws SQLException{
        HashMap<Integer, HashMap<Integer, Subject>>  curriculum = new HashMap<>();
        HashMap<Integer, Subject> subjectList;
        //<HashMap<semester, HashMap<subjectID, Subject>>>
        int sem, subjectId;
        Subject subject;
        PreparedStatement pst = conn.prepareStatement("SELECT tblCurriculum.*, tblSubjectList.fk_subject_id FROM tblCurriculum  INNER JOIN tblsubjectlist " +
                                                        "ON tblcurriculum.curriculum_id = tblsubjectlist.fk_curriculum_id WHERE tblCurriculum.fk_course_id = ?");
        pst.setString(1, String.valueOf(courseId));
        ResultSet rs = pst.executeQuery();
        
        while(rs.next()){
            sem = rs.getInt("sem");
            subjectId = rs.getInt("fk_subject_id");
            subject = generateSubject(subjectId, conn);
            
            subjectList = curriculum.putIfAbsent(sem, new HashMap<>());
            if(subjectList == null) subjectList = curriculum.get(sem);
            subjectList.put(subject.getSubjectID(), subject);
        }
        return curriculum;
    }

    private static Department generateDepartment(int departmentID, Connection conn) throws SQLException {
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

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
