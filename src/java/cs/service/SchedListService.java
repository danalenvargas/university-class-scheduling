/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import cs.model.SchedEntry;

import com.google.gson.Gson;
import cs.model.College;
import cs.model.Course;
import cs.model.Department;
import cs.model.Instructor;
import cs.model.IntervalST;
import cs.model.Message;
import cs.model.Room;
import cs.model.ScheduleCollection;
import cs.model.ScheduleCollection.SelectionEntry;
import cs.model.Section;
import cs.model.Subject;
import cs.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author Dan
 */
public class SchedListService {

    //SchedList schedList;
    private ArrayList<SchedEntry> schedList;
    private ScheduleCollection schedCollection;
    //**temporary variable
    private int tempVarSem = 1; // tempVarSem = sem_id of curent SY&sem being worked on
    private int globalVarSem = 2; // globalVarSem = 1 (1st sem), 2 (2nd sem), 3(summer)
    //**temporary variable
    Connection conn = null;
    DataSource ds;
    InitialContext ctx;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public SchedListService() {

    }

    public SchedListService(ScheduleCollection schedCollection) {
        this.schedCollection = schedCollection;
    }

    public String generateSchedList(String type, int collegeID, int departmentID, int itemId) {
        schedList = new ArrayList<>();
        
        //**TEST CODE
        System.out.println("INSIDE generatedSchedList: ");
        System.out.println(type + " " + collegeID + " " + departmentID + " " + itemId);
        //**TEST CODE
        
        IntervalST<SchedEntry> schedTree = schedCollection.getUsedTree(type, collegeID, departmentID, itemId);
        
        try{
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            Section section = null;
            Instructor instructor = null;
            Room room = null; 
            Subject subject = null;
            SchedEntry newSchedEntry = null;
            String status="";
            int creatorId;
            User creator = null;
            Timestamp ts;
            Date timestamp = null;
            boolean isReturnRequested = false;
                    
            for (SchedEntry entry : schedTree.getAllValues()) {
                //schedList.add(entry);
                section = generateSection(entry.getSection().getSectionID(), conn);
                if(entry.getInstructor()!=null) instructor = generateInstructor(entry.getInstructor().getInstructorID(), conn);
                else instructor = null;
                if(entry.getRoom()!=null) room = generateRoom(entry.getRoom().getRoomID(), conn);
                else room = null;
                subject = generateSubject(entry.getSubjectId(), conn);
                
                pst = conn.prepareStatement("SELECT status, fk_user_id, entryDate, isReturnRequested FROM tblSchedule WHERE sched_id=?");
                pst.setString(1, String.valueOf(entry.getSchedID()));
                rs = pst.executeQuery();
                if (rs.next()) {
                    status = rs.getString("status");
                    creatorId = rs.getInt("fk_user_id");
                    ts = rs.getTimestamp("entryDate");
                    timestamp = ts;
                    creator = SchedEntryCreator.generateUser(creatorId, conn);
                    isReturnRequested = rs.getBoolean("isReturnRequested");
                }
                
                newSchedEntry = new SchedEntry(entry.getSchedID(), entry.getStart(), entry.getEnd(), instructor, room, subject, section, status, creator, timestamp, isReturnRequested);
                schedList.add(newSchedEntry);
            }
            
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

        //Collections.reverse(schedList);
        String jsonSched = new Gson().toJson(schedList);
        System.out.println("JSONSCHED: ");
        System.out.println(jsonSched);
        //**TESTING
//        String jsonTree = new Gson().toJson(schedTree);
//        System.out.println("JSONTREE: ");
//        System.out.println(jsonTree);
        //**TESTING
        return jsonSched;
    }

    public String generateSubjectList(int collegeId, int departmentId, int sectionId) {
        ArrayList<Subject> subjectList = new ArrayList<>();
//        HashMap<Integer, Subject> subjectMap = schedCollection.getSubjectList(collegeId, departmentId, sectionId, sem);
//
//        for (HashMap.Entry<Integer, Subject> entry : subjectMap.entrySet()) {
//            subjectList.add(entry.getValue());
//        }
        
        try{
            int year, courseID=0;
            double curriculumSem=0;
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM tblSection WHERE section_id=?");
            pst.setString(1, String.valueOf(sectionId));
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                year = rs.getInt("year");
                courseID = rs.getInt("fk_course_id");
                curriculumSem = year * globalVarSem;
                if(globalVarSem == 1) curriculumSem = curriculumSem - 1;
                else if(globalVarSem == 3) curriculumSem = curriculumSem + .5;
            }
            
            if(curriculumSem!=0){
                pst = conn.prepareStatement("SELECT tblSubjectList.fk_subject_id FROM tblCurriculum "
                        + "INNER JOIN tblsubjectlist ON tblcurriculum.curriculum_id = tblsubjectlist.fk_curriculum_id "
                        + "WHERE tblCurriculum.fk_course_id = ? AND tblCurriculum.sem = ?");
                pst.setString(1, String.valueOf(courseID));
                pst.setString(2, String.valueOf(curriculumSem));
                rs = pst.executeQuery();
                
                while(rs.next()){
                    int subjectId = rs.getInt("fk_subject_id");
                    Subject subject = generateSubject(subjectId, conn);
                    subject.setHoursPlotted(schedCollection.getSubjectPlottedHours(collegeId, departmentId, sectionId, subject.getSubjectID()));
//                    System.out.println("adding subject to subjectList:");
//                    System.out.println(subject.getTitle());
//                    System.out.println(subject.getType());
                    System.out.println(subject.getHoursPlotted());
                    subjectList.add(subject);
                }
            }
            
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

        String jsonList = new Gson().toJson(subjectList);
        return jsonList;
    }

    public String generateSelectionList(User user, String type, String scope) { //type = {section, room, instructor}
        ArrayList<SelectionEntry> selectionList = new ArrayList<>();
        String userType = user.getUserType();
        int userId = user.getUserID();
        int collegeId = user.getCollegeID();
        int departmentId = user.getDepartmentID();

        switch (userType) {
            case "COLLEGE_REGISTRAR":
                switch (type) {
                    case "section":
                        ArrayList<Integer> sectionList = schedCollection.getAllCollegeSections(collegeId);
                        addSectionsToSelectionList(sectionList, selectionList);
                        appendBorrowedSections(selectionList, userId);
                        break;
                    case "instructor":
                        //selectionList = schedCollection.getAllCollegeInstructors(collegeId);
                        ArrayList<Integer> instructorList = schedCollection.getAllCollegeInstructors(collegeId);
                        addInstructorsToSelectionList(instructorList, selectionList);
                        break;
                    case "room":
//                        selectionList = schedCollection.getAllCollegeRooms(collegeId);
                        ArrayList<Integer> roomList = schedCollection.getAllCollegeRooms(collegeId);
                        addRoomsToSelectionList(roomList, selectionList);
                        appendBorrowedRooms(selectionList, userId);
                        break;
                }
                break;

            case "SCHEDULER":
                switch (type) {
                    case "section":
                        getSchedulerSections(selectionList, userId);
                        break;
                    case "instructor":
                        getSchedulerInstructors(selectionList, userId);
                        break;
                    case "room":
                        getSchedulerRooms(selectionList, userId);
                        break;
                }
                break;
        }
        System.out.println("INSIDE generateSelectionList, scope: " + scope);
        if(scope.equals("all")){ // Quick dirty fix, must improve later
            switch (type) {
                case "section":
                    appendAllSections(selectionList);
                    break;
                case "instructor":
                    break;
                case "room":
                    break;
            }
        }

        String jsonSelectionList = new Gson().toJson(selectionList);
        return jsonSelectionList;
    }
    
    // v SHOULD BE UNUSED BY NOW, FOR DELETION
    public ArrayList<Integer> getCollegeList(int userId) {
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;

        ArrayList<Integer> collegeList = new ArrayList<>();
        int collegeId;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblacl.fk_college_id as collegeId FROM tblacl WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(userId));

            rs = pst.executeQuery();

            while (rs.next()) {
                collegeId = rs.getInt("collegeId");
                collegeList.add(collegeId);
            }

            return collegeList;

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
    // ^ SHOULD BE UNUSED BY NOW, FOR DELETION

    public String generateFreeList(int collegeId, int start, int end, String type, int origStart, int origEnd, int origItemId, User currentUser) {
        //ArrayList selectionList = null;
        ArrayList<SelectionEntry> selectionList = new ArrayList<>();
        switch (type) {
            case "instructor":
                //selectionList = schedCollection.getFreeInstructors(collegeId, start, end, origStart, origEnd, origItem);
                if(currentUser.getUserType().equals("COLLEGE_REGISTRAR")){
                    ArrayList<Integer> instructorList = schedCollection.getFreeInstructors(collegeId, start, end, origStart, origEnd, origItemId);
                    addInstructorsToSelectionList(instructorList, selectionList);
                }else if(currentUser.getUserType().equals("SCHEDULER")){
                    appendFreeBorrowedInstructors(selectionList, currentUser, start, end, origStart, origEnd, origItemId);
                }
                break;
            case "room":
                //selectionList = schedCollection.getFreeRooms(collegeId, start, end, origStart, origEnd, origItem);
                if(currentUser.getUserType().equals("COLLEGE_REGISTRAR")){
                    ArrayList<Integer> roomList = schedCollection.getFreeRooms(collegeId, start, end, origStart, origEnd, origItemId);
                    addRoomsToSelectionList(roomList, selectionList);
                }
                appendFreeBorrowedRooms(selectionList, currentUser, start, end, origStart, origEnd, origItemId);
                break;
        }
        String jsonSelectionList = new Gson().toJson(selectionList);
        return jsonSelectionList;
    }
    
    public String generateSectionHistoryList(int givenStart, int givenEnd, int sectionId){
        ArrayList<SchedEntry> historyList = new ArrayList<>();
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        
        int schedId, roomId, instructorId, subjectId, start, end;
        Timestamp ts;
        Date timestamp;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            pst = conn.prepareStatement("SELECT * FROM tblSchedule WHERE isActive = 0 AND fk_sem_id = ? AND start < ? AND end > ? and fk_section_id = ?");
            pst.setString(1, String.valueOf(tempVarSem));
            pst.setString(2, String.valueOf(givenEnd));
            pst.setString(3, String.valueOf(givenStart));
            pst.setString(4, String.valueOf(sectionId));
            rs = pst.executeQuery();
            
            while(rs.next()){
                schedId = rs.getInt("sched_id");
                roomId = rs.getInt("fk_room_id");
                instructorId = rs.getInt("fk_instructor_id");
                subjectId = rs.getInt("fk_subject_id");
                start = rs.getInt("start");
                end = rs.getInt("end");
                ts = rs.getTimestamp("entryDate");
                timestamp = ts;
                
                SchedEntry newSchedEntry = SchedEntryCreator.createSchedEntry(schedId, sectionId, instructorId, roomId, subjectId, start, end, timestamp, conn);
                historyList.add(newSchedEntry);
            }
            
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
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
        }
        String jsonHistoryList = new Gson().toJson(historyList);
        return jsonHistoryList;
    }

    public void deleteSchedule(SchedEntry origSched, User currentUser) {
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        String defaultStatus = (currentUser.getUserType() == "SCHEDULER") ? "unsubmitted" : "submitted";

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            //** REMOVING SCHED
            if (origSched != null) {
                schedCollection.removeSchedule(origSched);

//                pst = conn.prepareStatement("DELETE FROM tblSchedule WHERE sched_id=?");
                pst = conn.prepareStatement("UPDATE tblSchedule SET status=?, isActive=0 WHERE sched_id=?");
                pst.setString(1, defaultStatus);
                pst.setString(2, String.valueOf(origSched.getSchedID()));
                pst.executeUpdate();
            }
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
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
        }
    }

    public String addSchedule(int sectionId, int instructorId, int roomId, int subjectId, int start, int end, SchedEntry origSched, User currentUser) {
        System.out.println("inside addSchedule");
        System.out.println("sectionId: " + sectionId);
        System.out.println("instructorId: " + instructorId);
        System.out.println("roomId: " + roomId);
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String defaultStatus = (currentUser.getUserType().equals("SCHEDULER")) ? "unsubmitted" : "submitted";
        int userId = currentUser.getUserID();

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            //** REMOVING OVERWRITTEN SCHED
            if (origSched != null) {
                schedCollection.removeSchedule(origSched);

//                pst = conn.prepareStatement("DELETE FROM tblSchedule WHERE sched_id=?");
                pst = conn.prepareStatement("UPDATE tblSchedule SET status=?, isActive=0 WHERE sched_id=?");
                pst.setString(1, defaultStatus);
                pst.setString(2, String.valueOf(origSched.getSchedID()));
                pst.executeUpdate();
            }

            //** ADDING SCHED
            pst = conn.prepareStatement("INSERT INTO tblSchedule (fk_section_id, fk_instructor_id, fk_room_id, fk_subject_id, start, end, fk_sem_id, status, fk_user_id) VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, String.valueOf(sectionId));
            if(instructorId != -1) pst.setString(2, String.valueOf(instructorId)); else pst.setString(2, null);
            if(roomId != -1) pst.setString(3, String.valueOf(roomId)); else pst.setString(3, null);
            pst.setString(4, String.valueOf(subjectId));
            pst.setString(5, String.valueOf(start));
            pst.setString(6, String.valueOf(end));
            pst.setString(7, String.valueOf(tempVarSem));
            pst.setString(8, defaultStatus);
            pst.setString(9, String.valueOf(userId));

            int affectedRows = pst.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating schedule failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int schedId = (generatedKeys.getInt(1));
                    SchedEntry schedEntry = SchedEntryCreator.createSchedEntry(schedId, sectionId, instructorId, roomId, subjectId, start, end, schedCollection, conn);
                    SchedEntry schedEntryForTree = SchedEntryCreator.createSchedEntryForTree(schedId, sectionId, instructorId, roomId, subjectId, start, end, schedCollection, conn);
                    schedCollection.addSchedule(schedEntryForTree);
                    String jsonSchedEntry = new Gson().toJson(schedEntry);
                    return jsonSchedEntry;
                } else {
                    throw new SQLException("Creating schedule failed, no ID obtained.");
                }
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
        return null;
    }
    
    public String getComments(int schedId){
        ArrayList<Message> messages = new ArrayList<>();
        int messageId;
        String senderName, text;
        Timestamp ts;
        Date entryDate;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblComment.comment_id as messageId, tblComment.message AS text, tblComment.entryDate AS entryDate, tblUser.name AS senderName "
                    + "FROM tblComment "
                    + "INNER JOIN tblUser ON tblComment.fk_user_id = tblUser.user_id "
                    + "WHERE fk_sched_id = ?");
            pst.setString(1, String.valueOf(schedId));
            rs = pst.executeQuery();

            while (rs.next()) {
                messageId = rs.getInt("messageId");
                text = rs.getString("text");
                senderName = rs.getString("senderName");
                ts = rs.getTimestamp("entryDate");
                entryDate = ts;
                Message message = new Message(messageId, senderName, text, entryDate);
                messages.add(message);
            }
            
            String jsonSelectionList = new Gson().toJson(messages);
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
    
    public void addComment(int schedId, String text, User currentUser){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblComment(fk_sched_id, fk_user_id, message) VALUES(?,?,?)");
            pst.setString(1, String.valueOf(schedId));
            pst.setString(2, String.valueOf(currentUser.getUserID()));
            pst.setString(3, text);
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
    
    public void validateSchedule(String newStatus, int schedId, boolean isReturn) {
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            if(isReturn){
                pst = conn.prepareStatement("UPDATE tblSchedule SET status=?, isReturnRequested=0 WHERE sched_id=?");
            }else{
                pst = conn.prepareStatement("UPDATE tblSchedule SET status=? WHERE sched_id=?");
            }
            pst.setString(1, newStatus);
            pst.setString(2, String.valueOf(schedId));
            pst.executeUpdate();
                
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
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
        }
    }
    
    public void requestReturn(int schedId) {
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            pst = conn.prepareStatement("UPDATE tblSchedule SET isReturnRequested=1 WHERE sched_id=?");
            pst.setString(1, String.valueOf(schedId));
            pst.executeUpdate();
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
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
        }
    }
    
    public void denyReturn(int schedId) {
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            pst = conn.prepareStatement("UPDATE tblSchedule SET isReturnRequested=0 WHERE sched_id=?");
            pst.setString(1, String.valueOf(schedId));
            pst.executeUpdate();
        } catch (NamingException ex) {
            Logger.getLogger(SchedListService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
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
        }
    }
    
    private void addSectionsToSelectionList (ArrayList<Integer> sectionList, ArrayList<SelectionEntry> selectionList){
        int sectionYear, sectionNumber, departmentId, collegeId;
        String courseCode, departmentCode, collegeCode, sectionName;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSection "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE section_id=?");
            for(int sectionId : sectionList){
                pst.setString(1, String.valueOf(sectionId));
                rs = pst.executeQuery();
                if (rs.next()) {
                    sectionYear = rs.getInt("sectionYear");
                    sectionNumber = rs.getInt("sectionNumber");
                    courseCode = rs.getString("courseCode");
                    departmentId = rs.getInt("departmentId");
                    departmentCode = rs.getString("departmentCode");
                    collegeId = rs.getInt("collegeId");
                    collegeCode = rs.getString("collegeCode");
                    sectionName = courseCode + sectionYear + "-" + sectionNumber;

//                    selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode));
                    selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode, true));
                }
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
    
    private void appendAllSections (ArrayList<SelectionEntry> selectionList){
        System.out.println("INSIDE appendAllSections");
        int sectionYear, sectionNumber, departmentId, collegeId, sectionId;
        String courseCode, departmentCode, collegeCode, sectionName;
        SelectionEntry selectionEntry;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSection.section_id AS sectionId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSection "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE tblSection.isActive = 1");
            rs = pst.executeQuery();
            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                sectionYear = rs.getInt("sectionYear");
                sectionNumber = rs.getInt("sectionNumber");
                courseCode = rs.getString("courseCode");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                sectionName = courseCode + sectionYear + "-" + sectionNumber;

                selectionEntry = new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode, false);
                if(selectionList.contains(selectionEntry)){
                    System.out.println("contains selection");
                } else {
                    System.out.println("does not contain selection");
                    selectionList.add(selectionEntry);
                }
//                selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode, false));
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
    
    private void appendBorrowedSections (ArrayList<SelectionEntry> selectionList, int userId){ //append borrowed sections to selectionList
        int sectionId, sectionYear, sectionNumber, departmentId, collegeId;
        String courseCode, departmentCode, collegeCode, sectionName;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSharedSectionEditor.fk_section_id AS sectionId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSharedSectionEditor "
                    + "INNER JOIN tblSection ON tblSharedSectionEditor.fk_section_id = tblSection.section_id "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblSection.isActive=1");
            pst.setString(1, String.valueOf(userId));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                sectionYear = rs.getInt("sectionYear");
                sectionNumber = rs.getInt("sectionNumber");
                courseCode = rs.getString("courseCode");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                sectionName = courseCode + sectionYear + "-" + sectionNumber;
//                
//                selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode));
                selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode, true));
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
    
    private void getSchedulerSections (ArrayList<SelectionEntry> selectionList, int userId){
        int sectionId, sectionYear, sectionNumber, departmentId, collegeId;
        String courseCode, departmentCode, collegeCode, sectionName;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSectionEditor.fk_section_id AS sectionId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSectionEditor "
                    + "INNER JOIN tblSection ON tblSectionEditor.fk_section_id = tblSection.section_id "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblSection.isActive=1");
            pst.setString(1, String.valueOf(userId));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                sectionYear = rs.getInt("sectionYear");
                sectionNumber = rs.getInt("sectionNumber");
                courseCode = rs.getString("courseCode");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                sectionName = courseCode + sectionYear + "-" + sectionNumber;
//                
//                selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode));
                selectionList.add(new SelectionEntry(sectionId, sectionName, collegeId, departmentId, collegeCode, departmentCode, courseCode, true));
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
    
    private void addInstructorsToSelectionList (ArrayList<Integer> instructorList, ArrayList<SelectionEntry> selectionList){
        int departmentId, collegeId;
        String instructorName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblInstructor.first_name AS firstName, tblInstructor.middle_initial AS middleInitial, tblInstructor.last_name AS lastName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblInstructor "
                    + "INNER JOIN tblDepartment ON tblInstructor.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE instructor_id=?");
            for(int instructorId : instructorList){
                pst.setString(1, String.valueOf(instructorId));
                rs = pst.executeQuery();
                if (rs.next()) {
                    departmentId = rs.getInt("departmentId");
                    departmentCode = rs.getString("departmentCode");
                    collegeId = rs.getInt("collegeId");
                    collegeCode = rs.getString("collegeCode");
                    instructorName = rs.getString("lastName") + ", " + rs.getString("firstName") + " " + rs.getString("middleInitial") + ".";

                    selectionList.add(new SelectionEntry(instructorId, instructorName, collegeId, departmentId, collegeCode, departmentCode));
                }
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
    
    private void getSchedulerInstructors (ArrayList<SelectionEntry> selectionList, int userId){
        int instructorId, departmentId, collegeId;
        String instructorName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblInstructorEditor.fk_instructor_id AS instructorId, tblInstructor.first_name AS firstName, tblInstructor.middle_initial AS middleInitial, tblInstructor.last_name AS lastName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblInstructorEditor "
                    + "INNER JOIN tblInstructor ON tblInstructorEditor.fk_instructor_id = tblInstructor.instructor_id "
                    + "INNER JOIN tblDepartment ON tblInstructor.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblInstructor.isActive=1");
            pst.setString(1, String.valueOf(userId));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                instructorId = rs.getInt("instructorId");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                instructorName = rs.getString("lastName") + ", " + rs.getString("firstName") + " " + rs.getString("middleInitial") + ".";

                selectionList.add(new SelectionEntry(instructorId, instructorName, collegeId, departmentId, collegeCode, departmentCode));
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
    
    private void addRoomsToSelectionList (ArrayList<Integer> roomList, ArrayList<SelectionEntry> selectionList){
        int departmentId, collegeId;
        String roomName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblRoom "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE tblRoom.room_id=?");
            for(int roomId : roomList){
                pst.setString(1, String.valueOf(roomId));
                rs = pst.executeQuery();
                if (rs.next()) {
                    departmentId = rs.getInt("departmentId");
                    departmentCode = rs.getString("departmentCode");
                    collegeId = rs.getInt("collegeId");
                    collegeCode = rs.getString("collegeCode");
                    roomName = rs.getString("roomName");

                    selectionList.add(new SelectionEntry(roomId, roomName, collegeId, departmentId, collegeCode, departmentCode));
                }
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
    
    private void appendBorrowedRooms (ArrayList<SelectionEntry> selectionList, int userId){ //append borrowed sections to selectionList
        int roomId, departmentId, collegeId;
        String roomName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSharedRoomEditor.fk_room_id AS roomId, tblSharedRoomEditor.fk_grantor_id AS grantorId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSharedRoomEditor "
                    + "INNER JOIN tblRoom ON tblSharedRoomEditor.fk_room_id = tblRoom.room_id "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblRoom.isActive=1");
            pst.setString(1, String.valueOf(userId));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                roomId = rs.getInt("roomId");
                roomName = rs.getString("roomName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                
                selectionList.add(new SelectionEntry(roomId, roomName, collegeId, departmentId, collegeCode, departmentCode));
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
    
    private void appendFreeBorrowedRooms (ArrayList<SelectionEntry> selectionList, User currentUser, int start, int end, int origStart, int origEnd, int origItemId){ //append available borrowed rooms to selectionList
        int roomId, departmentId, collegeId;
        String roomName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            if(currentUser.getUserType().equals("COLLEGE_REGISTRAR")) pst = conn.prepareStatement("SELECT tblSharedRoomEditor.fk_room_id AS roomId, tblSharedRoomEditor.fk_grantor_id AS grantorId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSharedRoomEditor "
                    + "INNER JOIN tblRoom ON tblSharedRoomEditor.fk_room_id = tblRoom.room_id "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblRoom.isActive=1");
            else if(currentUser.getUserType().equals("SCHEDULER")) pst = conn.prepareStatement("SELECT tblRoomEditor.fk_room_id AS roomId, tblRoomEditor.fk_grantor_id AS grantorId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblRoomEditor "
                    + "INNER JOIN tblRoom ON tblRoomEditor.fk_room_id = tblRoom.room_id "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblRoom.isActive=1");
            pst.setString(1, String.valueOf(currentUser.getUserID()));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                roomId = rs.getInt("roomId");
                roomName = rs.getString("roomName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                
                if(schedCollection.isRoomFree(collegeId, departmentId, roomId, start, end, origStart, origEnd, origItemId)) selectionList.add(new SelectionEntry(roomId, roomName, collegeId, departmentId, collegeCode, departmentCode));
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
    
    private void appendFreeBorrowedInstructors (ArrayList<SelectionEntry> selectionList, User currentUser, int start, int end, int origStart, int origEnd, int origItemId){ //append available borrowed instuctors to selectionList
        int instructorId, departmentId, collegeId;
        String instructorName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            if(currentUser.getUserType().equals("SCHEDULER")) pst = conn.prepareStatement("SELECT tblInstructorEditor.fk_instructor_id AS instructorId, tblInstructor.first_name AS firstName, tblInstructor.middle_initial AS middleInitial, tblInstructor.last_name AS lastName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblInstructorEditor "
                    + "INNER JOIN tblInstructor ON tblInstructorEditor.fk_instructor_id = tblInstructor.instructor_id "
                    + "INNER JOIN tblDepartment ON tblInstructor.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblInstructor.isActive=1");
            pst.setString(1, String.valueOf(currentUser.getUserID()));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                instructorId = rs.getInt("instructorId");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                instructorName = rs.getString("lastName") + ", " + rs.getString("firstName") + " " + rs.getString("middleInitial") + ".";

                if(schedCollection.isInstructorFree(collegeId, departmentId, instructorId, start, end, origStart, origEnd, origItemId)) selectionList.add(new SelectionEntry(instructorId, instructorName, collegeId, departmentId, collegeCode, departmentCode));
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
    
    private void getSchedulerRooms (ArrayList<SelectionEntry> selectionList, int userId){
        int roomId, departmentId, collegeId;
        String roomName, departmentCode, collegeCode;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblRoomEditor.fk_room_id AS roomId, tblRoomEditor.fk_grantor_id AS grantorId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblRoomEditor "
                    + "INNER JOIN tblRoom ON tblRoomEditor.fk_room_id = tblRoom.room_id "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=? AND tblRoom.isActive=1");
            pst.setString(1, String.valueOf(userId));

            rs = pst.executeQuery();
            
            while (rs.next()) {
                roomId = rs.getInt("roomId");
                roomName = rs.getString("roomName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                
                selectionList.add(new SelectionEntry(roomId, roomName, collegeId, departmentId, collegeCode, departmentCode));
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

    public String debugGetCollectoin() {
        String jsonCollection = new Gson().toJson(schedCollection);
        return jsonCollection;
    }
    
    //  8*************************************************************************************************************************
    //               CODE TAKEN FROM INITIALIZELISTENER, CONTAINS REDUNDANT/UNNECESSARY INFORMATION, SHOULD CLEAN CODE LATER
    //  8*************************************************************************************************************************
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

    private Instructor generateInstructor(int instructorID, Connection conn) throws SQLException {
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

    private Room generateRoom(int roomID, Connection conn) throws SQLException {
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

    private Subject generateSubject(int subjectID, Connection conn) throws SQLException {
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

    private Course generateCourse(int courseID, Connection conn, boolean hasChecklist) throws SQLException {
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
    
    private HashMap<Integer, HashMap<Integer, Subject>> generateCurriculum(int courseId, Connection conn) throws SQLException{
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

    private Department generateDepartment(int departmentID, Connection conn) throws SQLException {
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

    private College generateCollege(int collegeID, Connection conn) throws SQLException {
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
}
