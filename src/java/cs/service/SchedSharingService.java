/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cs.model.Message;
import cs.model.Notification;
import cs.model.SchedEntry;
import cs.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class SchedSharingService {

    Connection conn = null;
    DataSource ds;
    InitialContext ctx;
    PreparedStatement pst = null;
    ResultSet rs = null;

    // LIST OF OTHER COLLEGE REGISTRARS, FOR DROPDOWN SELECTION WHEN SHARING SECTIONS/ROOMS
    public String getUsersList(User currentUser) {
        ArrayList<UserEntry> usersList = new ArrayList<>();
        int id, collegeId;
        String name, collegeName;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblUser.user_id AS userId, tblUser.name AS userName, tblCollege.college_id AS collegeId, tblCollege.name AS collegeName "
                    + "FROM tblUser "
                    + "INNER JOIN tblCollege ON tblUser.fk_college_id = tblCollege.college_id "
                    + "WHERE user_id!=? AND user_type=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            pst.setString(2, "COLLEGE_REGISTRAR");

            rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getInt("userId");
                name = rs.getString("userName");
                collegeId = rs.getInt("collegeId");
                collegeName = rs.getString("collegeName");
                UserEntry user = new UserEntry(name, collegeName, id, collegeId);
                user.setBorrowedSections(currentUser.getUserID(), conn);
                user.setBorrowedRooms(currentUser.getUserID(), conn);
                usersList.add(user);
            }
            
            String jsonSelectionList = new Gson().toJson(usersList);
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
    
    public String getMySectionsList(User currentUser){
        ArrayList<CheckboxTreeNode> mySectionsList = new ArrayList<>();
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
                
                SharedEntry room = new SharedEntry(roomName, departmentCode, roomId, departmentId);
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
    
    // SECTIONS SHARED BY THE CURRENT USER TO OTHER USERS
    public String getSharedSectionsList(User currentUser) {
        //ArrayList<SharedEntry> sectionsList = new ArrayList<>();
        ArrayList<CheckboxTreeNode> sectionsList = new ArrayList<>();
        int sectionId, sectionYear, sectionNumber, courseId, departmentId, collegeId, userId; // userId and userName refers to the other user whom the current user has shared the section to
        String courseCode, departmentCode, collegeCode, userName;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSharedSectionEditor.fk_section_id AS sectionId, tblSharedSectionEditor.fk_user_id AS userId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.course_id AS courseId, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblSharedSectionEditor "
                    + "INNER JOIN tblSection ON tblSharedSectionEditor.fk_section_id = tblSection.section_id "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "WHERE fk_grantor_id=?");
            
            pst.setString(1, String.valueOf(currentUser.getUserID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                sectionYear = rs.getInt("sectionYear");
                sectionNumber = rs.getInt("sectionNumber");
                courseId = rs.getInt("courseId");
                courseCode = rs.getString("courseCode");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                userId = rs.getInt("userId");
                UserEntry otherUser = new UserEntry(userId);
                otherUser.setUserDetails(userId, conn);
                userName = otherUser.name;
                collegeCode = otherUser.collegeName;
                collegeId = otherUser.collegeId;
                
                SharedEntry section = new SharedEntry(sectionYear, sectionNumber, collegeCode, departmentCode, courseCode, sectionId, collegeId, departmentId, courseId, userId, userName);
                //sectionsList.add(section);
                addToSectionTree(sectionsList, section);
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
    
    // ROOMS SHARED BY THE CURRENT USER TO OTHER USERS
    public String getSharedRoomsList(User currentUser) {
        ArrayList<CheckboxTreeNode> roomsList = new ArrayList<>();
        int roomId, departmentId, collegeId, userId; // userId and userName refers to the other user whom the current user has shared the room to
        String roomName, departmentCode, collegeCode, userName;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSharedRoomEditor.fk_room_id AS roomId, tblSharedRoomEditor.fk_user_id AS userId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblSharedRoomEditor "
                    + "INNER JOIN tblRoom ON tblSharedRoomEditor.fk_room_id = tblRoom.room_id "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
//                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_grantor_id=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                roomId = rs.getInt("roomId");
                roomName = rs.getString("roomName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
//                collegeId = rs.getInt("collegeId");
//                collegeCode = rs.getString("collegeCode");
                userId = rs.getInt("userId");
                //userName = getUserName(userId, conn);
                UserEntry otherUser = new UserEntry(userId);
                otherUser.setUserDetails(userId, conn);
                userName = otherUser.name;
                collegeCode = otherUser.collegeName;
                collegeId = otherUser.collegeId;
                
                SharedEntry room = new SharedEntry(roomName, collegeCode, departmentCode, roomId, collegeId, departmentId, userId, userName);
                //roomsList.add(room);
                addToRoomTree(roomsList, room);
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
    
    // SECTIONS BORROWED BY THE CURRENT USER FROM OTHER USERS
    public String getBorrowedSectionsList(User currentUser) {
        //ArrayList<SharedEntry> sectionsList = new ArrayList<>();
        ArrayList<CheckboxTreeNode> sectionsList = new ArrayList<>();
        int sectionId, sectionYear, sectionNumber, courseId, departmentId, collegeId, grantorId;
        String courseCode, departmentCode, collegeCode, grantorName;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSharedSectionEditor.fk_section_id AS sectionId, tblSharedSectionEditor.fk_grantor_id AS grantorId, tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.course_id AS courseId, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSharedSectionEditor "
                    + "INNER JOIN tblSection ON tblSharedSectionEditor.fk_section_id = tblSection.section_id "
                    + "INNER JOIN tblCourse ON tblSection.fk_course_id = tblCourse.course_id "
                    + "INNER JOIN tblDepartment ON tblCourse.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                sectionId = rs.getInt("sectionId");
                sectionYear = rs.getInt("sectionYear");
                sectionNumber = rs.getInt("sectionNumber");
                courseId = rs.getInt("courseId");
                courseCode = rs.getString("courseCode");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                grantorId = rs.getInt("grantorId");
                grantorName = getUserName(grantorId, conn);
                
                SharedEntry section = new SharedEntry(sectionYear, sectionNumber, collegeCode, departmentCode, courseCode, sectionId, collegeId, departmentId, courseId, grantorId, grantorName);
                //sectionsList.add(section);
                addToSectionTree(sectionsList, section);
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
    
    // ROOMS BORROWED BY THE CURRENT USER FROM OTHER USERS
    public String getBorrowedRoomsList(User currentUser) {
        ArrayList<CheckboxTreeNode> roomsList = new ArrayList<>();
//        ArrayList<SharedEntry> roomsList = new ArrayList<>();
        int roomId, departmentId, collegeId, grantorId;
        String roomName, departmentCode, collegeCode, grantorName;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSharedRoomEditor.fk_room_id AS roomId, tblSharedRoomEditor.fk_grantor_id AS grantorId, tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                    + "FROM tblSharedRoomEditor "
                    + "INNER JOIN tblRoom ON tblSharedRoomEditor.fk_room_id = tblRoom.room_id "
                    + "INNER JOIN tblDepartment ON tblRoom.fk_department_id = tblDepartment.department_id "
                    + "INNER JOIN tblCollege ON tblDepartment.fk_college_id = tblCollege.college_id "
                    + "WHERE fk_user_id=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));

            rs = pst.executeQuery();

            while (rs.next()) {
                roomId = rs.getInt("roomId");
                roomName = rs.getString("roomName");
                departmentId = rs.getInt("departmentId");
                departmentCode = rs.getString("departmentCode");
                collegeId = rs.getInt("collegeId");
                collegeCode = rs.getString("collegeCode");
                grantorId = rs.getInt("grantorId");
                grantorName = getUserName(grantorId, conn);
                SharedEntry room = new SharedEntry(roomName, collegeCode, departmentCode, roomId, collegeId, departmentId, grantorId, grantorName);
                addToRoomTree(roomsList, room);
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
    
    public String getNotifications(User currentUser, int numOfDays) {
        ArrayList<Notification> notifications = new ArrayList<>();
        int logId, fromId;
        Timestamp ts;
        Date timestamp;
        String action, type;
        User fromUser;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT * FROM tblLog WHERE fk_to=? AND entryDate > DATE_SUB(now(), INTERVAL ? DAY)");
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            pst.setString(2, String.valueOf(numOfDays));

            rs = pst.executeQuery();

            while (rs.next()) {
                logId = rs.getInt("log_id");
                fromId = rs.getInt("fk_from");
                action = rs.getString("action");
                type = rs.getString("type");
                ts = rs.getTimestamp("entryDate");
                timestamp = ts;
                fromUser = SchedEntryCreator.generateUser(fromId, conn);
                Notification notification = new Notification(logId, action, type, fromUser, timestamp);
                notifications.add(notification);
            }
            
            String jsonSelectionList = new Gson().toJson(notifications);
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
    
    public String getAffectedItems(int logId, String type) {
        System.out.println("inside getAfefctedItems " + logId + type);
        ArrayList<Integer> entries = new ArrayList<>();
        int entryId;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT entry_id FROM tblAffectedEntries WHERE fk_log_id=?");
            pst.setString(1, String.valueOf(logId));

            rs = pst.executeQuery();
            while (rs.next()) {
                entryId = rs.getInt("entry_id");
                entries.add(entryId);
            }
            
//            String jsonSelectionList = new Gson().toJson(notifications);
            String jsonEntryList = null;
            if(type.equals("section")) jsonEntryList = generateAffectedSectionsList(entries);
            else if(type.equals("room")) jsonEntryList = generateAffectedRoomsList(entries);
            return jsonEntryList;
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
    
    public String generateAffectedSectionsList(ArrayList<Integer> entries) {
        System.out.println("inside generateAffectedSectionsList: ");
        System.out.println(entries);
        ArrayList<CheckboxTreeNode> sectionsList = new ArrayList<>();
        int sectionId, sectionYear, sectionNumber, courseId, departmentId;
        String courseCode, departmentCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblSection.year AS sectionYear, tblSection.number AS sectionNumber, tblCourse.course_id AS courseId, tblCourse.code AS courseCode, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblCourse ON tblDepartment.department_id = tblCourse.fk_department_id "
                    + "INNER JOIN tblSection ON tblCourse.course_id = tblSection.fk_course_id "
                    + "WHERE tblSection.section_id = ?");
            
            for(int i=0;i<entries.size();i++){
                sectionId = entries.get(i);
                pst.setString(1, String.valueOf(sectionId));
                rs = pst.executeQuery();
                if(rs.next()) {
                    sectionYear = rs.getInt("sectionYear");
                    sectionNumber = rs.getInt("sectionNumber");
                    courseId = rs.getInt("courseId");
                    courseCode = rs.getString("courseCode");
                    departmentId = rs.getInt("departmentId");
                    departmentCode = rs.getString("departmentCode");

                    SharedEntry section = new SharedEntry(sectionYear, sectionNumber, departmentCode, courseCode, sectionId, departmentId, courseId);
                    addToMySectionsTree(sectionsList, section);
                }
            }
            
            System.out.println("ended search: ");
            System.out.println(sectionsList);
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
    
    public String generateAffectedRoomsList(ArrayList<Integer> entries){
        ArrayList<CheckboxTreeNode> roomsList = new ArrayList<>();
        int roomId, departmentId;
        String roomName, departmentCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblRoom.name AS roomName, tblDepartment.department_id AS departmentId, tblDepartment.code AS departmentCode "
                    + "FROM tblDepartment "
                    + "INNER JOIN tblRoom ON tblDepartment.department_id = tblRoom.fk_department_id "
                    + "WHERE tblRoom.room_id = ?");

            for(int i=0;i<entries.size();i++){
                roomId = entries.get(i);
                pst.setString(1, String.valueOf(roomId));
                rs = pst.executeQuery();
                if(rs.next()) {
                    roomName = rs.getString("roomName");
                    departmentId = rs.getInt("departmentId");
                    departmentCode = rs.getString("departmentCode");

                    SharedEntry room = new SharedEntry(roomName, departmentCode, roomId, departmentId);
                    addToMyRoomsTree(roomsList, room);
                }
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
    
    // ====  v TO DELETE
    public String getUserName (int userId, Connection conn) {
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT name FROM tblUser WHERE user_id=?");
            pst.setString(1, String.valueOf(userId));
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                return name;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SchedSharingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    // ===== ^ to delete
    
    // GRANT ANOTHER USER ACCESS TO A SECTION
    public void shareSection(User currentUser, int receiverId, int[] sectionIds){
        int logId = 0;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblSharedSectionEditor(fk_section_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
            
            for(int i=0; i<sectionIds.length; i++){
                pst.setString(1, String.valueOf(sectionIds[i]));
                pst.setString(2, String.valueOf(receiverId));
                pst.setString(3, String.valueOf(currentUser.getUserID()));
                System.out.println("shareSection, toDB: " + " sectionID-" + String.valueOf(sectionIds[i]) + " receiverId-" + String.valueOf(receiverId) + " currentUserId-" + String.valueOf(currentUser.getUserID()));
                pst.executeUpdate(); 
            }
            
            pst = conn.prepareStatement("INSERT INTO tblLog(action, type, fk_from, fk_to) VALUES(?,?,?,?)");
            pst.setString(1, "share");
            pst.setString(2, "section");
            pst.setString(3, String.valueOf(currentUser.getUserID()));
            pst.setString(4, String.valueOf(receiverId));
            pst.executeUpdate(); 
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblLog;");
            rs =  pst.executeQuery();
            if(rs.next()){
                logId = rs.getInt("lastId");
            }
            
            if(logId != 0){
                pst = conn.prepareStatement("INSERT INTO tblAffectedEntries(fk_log_id, entry_id) VALUES(?,?)");
                for(int i=0; i<sectionIds.length; i++){
                    pst.setString(1, String.valueOf(logId));
                    pst.setString(2, String.valueOf(sectionIds[i]));
                    pst.executeUpdate(); 
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
    
    // GRANT ANOTHER USER ACCESS TO A ROOM
    public void shareRoom(User currentUser, int receiverId, int[] roomIds){
        int logId = 0;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblSharedRoomEditor(fk_room_id, fk_user_id, fk_grantor_id) VALUES(?,?,?)");
            for(int i=0; i<roomIds.length; i++){
                pst.setString(1, String.valueOf(roomIds[i]));
                pst.setString(2, String.valueOf(receiverId));
                pst.setString(3, String.valueOf(currentUser.getUserID()));
                pst.executeUpdate();
            }
            
            pst = conn.prepareStatement("INSERT INTO tblLog(action, type, fk_from, fk_to) VALUES(?,?,?,?)");
            pst.setString(1, "share");
            pst.setString(2, "room");
            pst.setString(3, String.valueOf(currentUser.getUserID()));
            pst.setString(4, String.valueOf(receiverId));
            pst.executeUpdate(); 
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblLog;");
            rs =  pst.executeQuery();
            if(rs.next()){
                logId = rs.getInt("lastId");
            }
            
            if(logId != 0){
                pst = conn.prepareStatement("INSERT INTO tblAffectedEntries(fk_log_id, entry_id) VALUES(?,?)");
                for(int i=0; i<roomIds.length; i++){
                    pst.setString(1, String.valueOf(logId));
                    pst.setString(2, String.valueOf(roomIds[i]));
                    pst.executeUpdate(); 
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
    
    // REMOVE OTHER USER'S ACCESS FROM A SECTION WHICH THE CURRENT USER HAS PREVIOUSLY SHARED
    //public void pullSection(User currentUser, int receiverId, int sectionId){
    public void pullSection(User currentUser, String selectedEntriesJsonString){
        HashMap<Integer, ArrayList<Integer>> entries = new HashMap<>(); // <receiverId, itemList>
        ArrayList<Integer> itemList;
        PreparedStatement pst2 = null;
        ResultSet rs2 = null;
        try {
            JsonParser parser = new JsonParser();
            JsonArray jArr = (JsonArray) parser.parse(selectedEntriesJsonString);
            System.out.println("selectedEntriesJsonString: ");
            System.out.println(selectedEntriesJsonString);
            System.out.println("jArr:");
            System.out.println(jArr);
            JsonObject obj;
            String receiverId, sectionId;
            
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("DELETE FROM tblSharedSectionEditor WHERE fk_user_id=? AND fk_grantor_id=? AND fk_section_id=?");
            for(int i=0; i < jArr.size(); i++){
                obj = (JsonObject) jArr.get(i);
                System.out.println("jObj:");
                System.out.println(obj);
                receiverId  = obj.get("otherUserId").getAsString();
                sectionId  = obj.get("entryId").getAsString();
                pst.setString(1, receiverId);
                pst.setString(2, String.valueOf(currentUser.getUserID()));
                pst.setString(3, sectionId);
                pst.executeUpdate();
                
                itemList = entries.get(Integer.parseInt(receiverId));
                if(itemList != null){
                    itemList.add(Integer.parseInt(sectionId));
                }else{
                    itemList = new ArrayList<>();
                    itemList.add(Integer.parseInt(sectionId));
                    entries.put(Integer.parseInt(receiverId), itemList);
                }
            }
            
            pst = conn.prepareStatement("INSERT INTO tblLog(action, type, fk_from, fk_to) VALUES(?,?,?,?)");
            pst.setString(1, "pull");
            pst.setString(2, "section");
            pst.setString(3, String.valueOf(currentUser.getUserID()));
            for (HashMap.Entry<Integer, ArrayList<Integer>> entry : entries.entrySet()) {
                pst.setString(4, String.valueOf(entry.getKey()));
                pst.executeUpdate(); 
                int logId = 0;
                
                pst2 = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblLog;");
                rs2 =  pst2.executeQuery();
                if(rs2.next()){
                    logId = rs2.getInt("lastId");
                }

                if(logId != 0){
                    pst2 = conn.prepareStatement("INSERT INTO tblAffectedEntries(fk_log_id, entry_id) VALUES(?,?)");
                    for(int i=0; i< entry.getValue().size(); i++){
                        pst2.setString(1, String.valueOf(logId));
                        pst2.setString(2, String.valueOf(entry.getValue().get(i)));
                        pst2.executeUpdate(); 
                    }
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
    
    // REMOVE OTHER USER'S ACCESS FROM A ROOM WHICH THE CURRENT USER HAS PREVIOUSLY SHARED
    public void pullRoom(User currentUser, String selectedEntriesJsonString){
        HashMap<Integer, ArrayList<Integer>> entries = new HashMap<>(); // <receiverId, itemList>
        ArrayList<Integer> itemList;
        PreparedStatement pst2 = null;
        ResultSet rs2 = null;
        try {
            JsonParser parser = new JsonParser();
            JsonArray jArr = (JsonArray) parser.parse(selectedEntriesJsonString);
            System.out.println("selectedEntriesJsonString: ");
            System.out.println(selectedEntriesJsonString);
            System.out.println("jArr:");
            System.out.println(jArr);
            JsonObject obj;
            String receiverId, roomId;
            
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("DELETE FROM tblSharedRoomEditor WHERE fk_user_id=? AND fk_grantor_id=? AND fk_room_id=?");
            
            for(int i=0; i < jArr.size(); i++){
                obj = (JsonObject) jArr.get(i);
                System.out.println("jObj:");
                System.out.println(obj);
                receiverId  = obj.get("otherUserId").getAsString();
                roomId  = obj.get("entryId").getAsString();
                pst.setString(1, String.valueOf(receiverId));
                pst.setString(2, String.valueOf(currentUser.getUserID()));
                pst.setString(3, String.valueOf(roomId));
                pst.executeUpdate();
                
                itemList = entries.get(Integer.parseInt(receiverId));
                if(itemList != null){
                    itemList.add(Integer.parseInt(roomId));
                }else{
                    itemList = new ArrayList<>();
                    itemList.add(Integer.parseInt(roomId));
                    entries.put(Integer.parseInt(receiverId), itemList);
                }
            }
            
            pst = conn.prepareStatement("INSERT INTO tblLog(action, type, fk_from, fk_to) VALUES(?,?,?,?)");
            pst.setString(1, "pull");
            pst.setString(2, "room");
            pst.setString(3, String.valueOf(currentUser.getUserID()));
            for (HashMap.Entry<Integer, ArrayList<Integer>> entry : entries.entrySet()) {
                pst.setString(4, String.valueOf(entry.getKey()));
                pst.executeUpdate(); 
                int logId = 0;
                
                pst2 = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblLog;");
                rs2 =  pst2.executeQuery();
                if(rs2.next()){
                    logId = rs2.getInt("lastId");
                }

                if(logId != 0){
                    pst2 = conn.prepareStatement("INSERT INTO tblAffectedEntries(fk_log_id, entry_id) VALUES(?,?)");
                    for(int i=0; i< entry.getValue().size(); i++){
                        pst2.setString(1, String.valueOf(logId));
                        pst2.setString(2, String.valueOf(entry.getValue().get(i)));
                        pst2.executeUpdate(); 
                    }
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
    
    // CURRENT USER REMOVES HIS ACCESS TO A SECTION WHICH OTHER USER HAS PREVIOUSLY SHARED TO HIM
    public void returnSection(User currentUser, String selectedEntriesJsonString){
        HashMap<Integer, ArrayList<Integer>> entries = new HashMap<>(); // <receiverId, itemList>
        ArrayList<Integer> itemList;
        PreparedStatement pst2 = null;
        ResultSet rs2 = null;
        try {
            JsonParser parser = new JsonParser();
            JsonArray jArr = (JsonArray) parser.parse(selectedEntriesJsonString);
            JsonObject obj;
            String grantorId, sectionId;
            
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("DELETE FROM tblSharedSectionEditor WHERE fk_user_id=? AND fk_grantor_id=? AND fk_section_id=?");
            for(int i=0; i < jArr.size(); i++){
                obj = (JsonObject) jArr.get(i);
                grantorId  = obj.get("otherUserId").getAsString();
                sectionId  = obj.get("entryId").getAsString();
                pst.setString(1, String.valueOf(currentUser.getUserID()));
                pst.setString(2, grantorId);
                pst.setString(3, sectionId);
                pst.executeUpdate();
                
                itemList = entries.get(Integer.parseInt(grantorId));
                if(itemList != null){
                    itemList.add(Integer.parseInt(sectionId));
                }else{
                    itemList = new ArrayList<>();
                    itemList.add(Integer.parseInt(sectionId));
                    entries.put(Integer.parseInt(grantorId), itemList);
                }
            }
            
            pst = conn.prepareStatement("INSERT INTO tblLog(action, type, fk_from, fk_to) VALUES(?,?,?,?)");
            pst.setString(1, "return");
            pst.setString(2, "section");
            pst.setString(3, String.valueOf(currentUser.getUserID()));
            for (HashMap.Entry<Integer, ArrayList<Integer>> entry : entries.entrySet()) {
                pst.setString(4, String.valueOf(entry.getKey()));
                pst.executeUpdate(); 
                int logId = 0;
                
                pst2 = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblLog;");
                rs2 =  pst2.executeQuery();
                if(rs2.next()){
                    logId = rs2.getInt("lastId");
                }

                if(logId != 0){
                    pst2 = conn.prepareStatement("INSERT INTO tblAffectedEntries(fk_log_id, entry_id) VALUES(?,?)");
                    for(int i=0; i< entry.getValue().size(); i++){
                        pst2.setString(1, String.valueOf(logId));
                        pst2.setString(2, String.valueOf(entry.getValue().get(i)));
                        pst2.executeUpdate(); 
                    }
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
    
    // CURRENT USER REMOVES HIS ACCESS TO A ROOM WHICH OTHER USER HAS PREVIOUSLY SHARED TO HIM
    public void returnRoom(User currentUser, String selectedEntriesJsonString){
        HashMap<Integer, ArrayList<Integer>> entries = new HashMap<>(); // <receiverId, itemList>
        ArrayList<Integer> itemList;
        PreparedStatement pst2 = null;
        ResultSet rs2 = null;
        try {
            JsonParser parser = new JsonParser();
            JsonArray jArr = (JsonArray) parser.parse(selectedEntriesJsonString);
            JsonObject obj;
            String grantorId, roomId;
            
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("DELETE FROM tblSharedRoomEditor WHERE fk_user_id=? AND fk_grantor_id=? AND fk_room_id=?");
            for(int i=0; i < jArr.size(); i++){
                obj = (JsonObject) jArr.get(i);
                grantorId  = obj.get("otherUserId").getAsString();
                roomId  = obj.get("entryId").getAsString();
                pst.setString(1, String.valueOf(currentUser.getUserID()));
                pst.setString(2, String.valueOf(grantorId));
                pst.setString(3, String.valueOf(roomId));
                pst.executeUpdate();
                
                itemList = entries.get(Integer.parseInt(grantorId));
                if(itemList != null){
                    itemList.add(Integer.parseInt(roomId));
                }else{
                    itemList = new ArrayList<>();
                    itemList.add(Integer.parseInt(roomId));
                    entries.put(Integer.parseInt(grantorId), itemList);
                }
            }
            
            pst = conn.prepareStatement("INSERT INTO tblLog(action, type, fk_from, fk_to) VALUES(?,?,?,?)");
            pst.setString(1, "return");
            pst.setString(2, "room");
            pst.setString(3, String.valueOf(currentUser.getUserID()));
            for (HashMap.Entry<Integer, ArrayList<Integer>> entry : entries.entrySet()) {
                pst.setString(4, String.valueOf(entry.getKey()));
                pst.executeUpdate(); 
                int logId = 0;
                
                pst2 = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblLog;");
                rs2 =  pst2.executeQuery();
                if(rs2.next()){
                    logId = rs2.getInt("lastId");
                }

                if(logId != 0){
                    pst2 = conn.prepareStatement("INSERT INTO tblAffectedEntries(fk_log_id, entry_id) VALUES(?,?)");
                    for(int i=0; i< entry.getValue().size(); i++){
                        pst2.setString(1, String.valueOf(logId));
                        pst2.setString(2, String.valueOf(entry.getValue().get(i)));
                        pst2.executeUpdate(); 
                    }
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
    
    public String getChatListForDropdown(User currentUser) {
        ArrayList<Chat> chatList = new ArrayList<>();
        int id;
        String name;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblUser.user_id AS userId, tblUser.name AS userName "
                    + "FROM tblUser "
                    + "WHERE user_id!=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getInt("userId");
                name = rs.getString("userName");
                Chat chat = new Chat(id, "individual", name);
                chatList.add(chat);
            }
            
            pst = conn.prepareStatement("SELECT tblChat.chat_id AS chatId, tblChat.name AS chatName "
                    + "FROM tblChat "
                    + "INNER JOIN tblChatMembers ON tblChat.chat_id = tblChatMembers.fk_chat_id "
                    + "WHERE tblChat.type = 'group' AND tblChatMembers.fk_user_id=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            rs = pst.executeQuery();
            while (rs.next()) {
                id = rs.getInt("chatId");
                name = rs.getString("chatName");
                Chat chat = new Chat(id, "group", name);
                chatList.add(chat);
            }
            
            String jsonSelectionList = new Gson().toJson(chatList);
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
    
    public String getChatSessions(User currentUser) {
        ArrayList<Chat> chatList = new ArrayList<>();
        int id, chatPartnerId=0;
        String name="", subtitle="", type, userType, collegeCode;
        Timestamp ts;
        Date lastEdit;
        
        PreparedStatement pst2 = null;
        ResultSet rs2 = null;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            pst = conn.prepareStatement("SELECT tblChat.chat_id AS chatId, tblChat.type AS type, tblChat.name AS chatName, tblChat.lastEdit AS lastEdit "
                    + "FROM tblChat "
                    + "INNER JOIN tblChatMembers ON tblChat.chat_id = tblChatMembers.fk_chat_id "
                    + "WHERE tblChatMembers.fk_user_id=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            rs = pst.executeQuery();
            while (rs.next()) {
                id = rs.getInt("chatId");
                type = rs.getString("type");
                ts = rs.getTimestamp("lastEdit");
                lastEdit = ts;
                
                if(type.equals("group")){
                    name = rs.getString("chatName");
                    subtitle = "group chat";
                    chatPartnerId=0;
                }else{
                    System.out.println("type.equals = individual; currentUserId: " + currentUser.getUserID() + " chatId: " + id);
                    pst2 = conn.prepareStatement("SELECT tbluser.user_id AS userId, tblUser.name AS userName, tblUser.user_type AS userType, tblCollege.code AS collegeCode "
                    + "FROM tblChatMembers "
                    + "INNER JOIN tblUser ON tblUser.user_id = tblChatMembers.fk_user_id "        
                    + "INNER JOIN tblCollege ON tblUser.fk_college_id = tblCollege.college_id "
                    + "WHERE tblChatMembers.fk_user_id!=? AND tblChatMembers.fk_chat_id=?");
                    pst2.setString(1, String.valueOf(currentUser.getUserID()));
                    pst2.setString(2, String.valueOf(id));
                    rs2 = pst2.executeQuery();
                    
                    if(rs2.next()){
                        System.out.println("rs2 got content");
                        chatPartnerId = rs2.getInt("userId");
                        name = rs2.getString("userName");
                        userType = rs2.getString("userType");
                        collegeCode = rs2.getString("collegeCode");
                        if(userType.equals("ADMINISTRATOR")) subtitle = "Administrator";
                        else if(userType.equals("COLLEGE_REGISTRAR")) subtitle = "College Registrar, " + collegeCode;
                        else if(userType.equals("SCHEDULER")) subtitle = "Scheduler, " + collegeCode;
                    }else{ // ADMIN CHAT PARTNER, DIRTY QUICK FIX
                        pst2 = conn.prepareStatement("SELECT tbluser.user_id AS userId, tblUser.name AS userName, tblUser.user_type AS userType "
                        + "FROM tblChatMembers "
                        + "INNER JOIN tblUser ON tblUser.user_id = tblChatMembers.fk_user_id "
                        + "WHERE tblChatMembers.fk_user_id!=? AND tblChatMembers.fk_chat_id=?");
                        pst2.setString(1, String.valueOf(currentUser.getUserID()));
                        pst2.setString(2, String.valueOf(id));
                        rs2 = pst2.executeQuery();
                        if(rs2.next()){
                            chatPartnerId = rs2.getInt("userId");
                            name = rs2.getString("userName");
                            userType = rs2.getString("userType");;
                            if(userType.equals("ADMINISTRATOR")) subtitle = "Administrator";
                        }
                    }
                }
                
                Chat chat = new Chat(id, type, name, subtitle, lastEdit, chatPartnerId);
                chatList.add(chat);
            }
            
            String jsonSelectionList = new Gson().toJson(chatList);
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
    
    public String getMessages(User currentUser, int chatId) {
        ArrayList<Message> messages = new ArrayList<>();
        int messageId;
        String senderName, position, text;
        Timestamp ts;
        Date entryDate;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblMessages.message_id AS messageId, tblMessages.message AS text, tblMessages.entryDate AS entryDate, tblUser.name AS senderName "
                    + "FROM tblMessages "
                    + "INNER JOIN tblUser ON tblMessages.fk_user_id = tblUser.user_id "
                    + "WHERE fk_chat_id = ?");
            pst.setString(1, String.valueOf(chatId));
            rs = pst.executeQuery();

            while (rs.next()) {
                messageId = rs.getInt("messageId");
                text = rs.getString("text");
                senderName = rs.getString("senderName");
                ts = rs.getTimestamp("entryDate");
                entryDate = ts;
                if(senderName.equals(currentUser.getUserName())) position = "right";
                else position = "left";
                Message message = new Message(messageId, position, senderName, text, entryDate);
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
    
    public String getMembers(User currentUser, int chatId) {
        ArrayList<Chat> members = new ArrayList<>();
        int chatPartnerId=0;
        String name="", subtitle="", userType, collegeCode;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();
            
            pst = conn.prepareStatement("SELECT tblUser.user_id AS userId, tblUser.name as userName, tblUser.user_type AS userType, tblCollege.code AS collegeCode "
                    + "FROM tblChatMembers "
                    + "INNER JOIN tblUser ON tblChatMembers.fk_user_id = tblUser.user_id "  
                    + "LEFT JOIN tblCollege ON tblUser.fk_college_id = tblCollege.college_id "
                    + "WHERE tblChatMembers.fk_user_id!=? AND tblChatMembers.fk_chat_id=?");
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            pst.setString(2, String.valueOf(chatId));
            rs = pst.executeQuery();
            while (rs.next()) {
                chatPartnerId = rs.getInt("userId");
                name = rs.getString("userName");
                userType = rs.getString("userType");
                collegeCode = rs.getString("collegeCode");
                if(userType.equals("ADMINISTRATOR")) subtitle = "Administrator";
                else if(userType.equals("COLLEGE_REGISTRAR")) subtitle = "College Registrar, " + collegeCode;
                else if(userType.equals("SCHEDULER")) subtitle = "Scheduler, " + collegeCode;
                
                Chat chat = new Chat(name, subtitle, chatPartnerId);
                members.add(chat);
            }
            
            String jsonSelectionList = new Gson().toJson(members);
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
    
    public String getInvitees(User currentUser, int chatId) {
        ArrayList<Chat> invitees = new ArrayList<>();
        int userId;
        String name="", subtitle="", userType, collegeCode;

        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblUser.user_id AS userId, tblUser.name AS userName, tblUser.user_type AS userType, tblCollege.code AS collegeCode  "
                    + "FROM tblUser "
//                    + "RIGHT JOIN tblUser ON tblUser.user_id = tblChatMembers.fk_user_id "
                    + "LEFT JOIN tblCollege ON tblUser.fk_college_id = tblCollege.college_id "
                    + "WHERE tblUser.user_id != ?");
//            tblChatMembers.fk_user_id IS NULL
//            pst.setString(1, String.valueOf(chatId));
            pst.setString(1, String.valueOf(currentUser.getUserID()));
            rs = pst.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("userId");
                name = rs.getString("userName");
                userType = rs.getString("userType");
                collegeCode = rs.getString("collegeCode");
                if(userType.equals("ADMINISTRATOR")) subtitle = "Administrator";
                else if(userType.equals("COLLEGE_REGISTRAR")) subtitle = "College Registrar, " + collegeCode;
                else if(userType.equals("SCHEDULER")) subtitle = "Scheduler, " + collegeCode;
                
                Chat chat = new Chat(name, subtitle, userId);
                invitees.add(chat);
            }
            
            pst = conn.prepareStatement("SELECT fk_user_id FROM tblChatMembers WHERE fk_chat_id = ?");
            pst.setString(1, String.valueOf(chatId));
            rs = pst.executeQuery();
            while (rs.next()) {
                userId = rs.getInt("fk_user_id");
                for(int i=0; i< invitees.size(); i++){
                    if(invitees.get(i).chatPartnerId == userId){
                        invitees.remove(i);
                        break;
                    }
                }
            }
            String jsonSelectionList = new Gson().toJson(invitees);
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
    
    public String getLastEdit(int chatId) {
        Timestamp ts;
        Date lastEdit;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT tblChat.lastEdit "
                    + "FROM tblChat "
                    + "WHERE chat_id = ?");
            pst.setString(1, String.valueOf(chatId));
            rs = pst.executeQuery();

            if (rs.next()) {
                ts = rs.getTimestamp("lastEdit");
                lastEdit = ts;
                String jsonSelectionList = new Gson().toJson(lastEdit);
                return jsonSelectionList;
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
    
    public String createGroupChat(User currentUser, String name){
        int chatId=0;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblChat(type, name) VALUES(?,?)");
            pst.setString(1, "group");
            pst.setString(2, name);
            pst.executeUpdate(); 
            
            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblChat;");
            rs =  pst.executeQuery();
            if(rs.next()){
                chatId = rs.getInt("lastId");
            }
            
            if(chatId != 0){
                pst = conn.prepareStatement("INSERT INTO tblChatMembers(fk_chat_id, fk_user_id) VALUES(?,?)");
                pst.setString(1, String.valueOf(chatId));
                pst.setString(2, String.valueOf(currentUser.getUserID()));
                pst.executeUpdate(); 
                String jsonSchedEntry = new Gson().toJson(chatId);
                return jsonSchedEntry;
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
    
    public String addGroupChatMember(int chatId, int userId){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblChatMembers(fk_chat_id, fk_user_id) VALUES(?,?)");
            pst.setString(1, String.valueOf(chatId));
            pst.setString(2, String.valueOf(userId));
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
        return null;
    }
    
        
    public String createIndividualChat(User currentUser, int chatPartnerId){
        int chatId=0;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblChat(type) VALUES(?)");
            pst.setString(1, "individual");
            pst.executeUpdate(); 

            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblChat;");
            rs =  pst.executeQuery();
            if(rs.next()){
                chatId = rs.getInt("lastId");
            }

            if(chatId != 0){
                pst = conn.prepareStatement("INSERT INTO tblChatMembers(fk_chat_id, fk_user_id) VALUES(?,?)");
                pst.setString(1, String.valueOf(chatId));
                pst.setString(2, String.valueOf(currentUser.getUserID()));
                pst.executeUpdate(); 

                pst.setString(2, String.valueOf(chatPartnerId));
                pst.executeUpdate(); 
                String jsonSchedEntry = new Gson().toJson(chatId);
                return jsonSchedEntry;
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
    
    public void sendMessage(int chatId, String text, User currentUser){
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblMessages(fk_chat_id, fk_user_id, message) VALUES(?,?,?)");
            pst.setString(1, String.valueOf(chatId));
            pst.setString(2, String.valueOf(currentUser.getUserID()));
            pst.setString(3, text);
            pst.executeUpdate(); 

            pst = conn.prepareStatement("UPDATE tblChat SET lastEdit=? WHERE chat_id=?");
            java.util.Date date = new java.util.Date();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
            pst.setTimestamp(1, timestamp);
            pst.setString(2, String.valueOf(chatId));
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
    
    class Chat{
        int id, chatPartnerId;
        String type, name, subtitle;
        Date lastEdit;

        public Chat(int id, String type, String name, String subtitle, Date lastEdit, int chatPartnerId) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.subtitle = subtitle;
            this.lastEdit = lastEdit;
            this.chatPartnerId = chatPartnerId;
        }

        public Chat(int id, String type, String name) {
            this.id = id;
            this.type = type;
            this.name = name;
        }

        public Chat(String name, String subtitle, int chatPartnerId) {
            this.chatPartnerId = chatPartnerId;
            this.name = name;
            this.subtitle = subtitle;
        }
        
        
    }
    
    // v TO REMOVE, not yeeeet, being used for userslist
    class UserEntry {
        String name, collegeName;
        int id, collegeId;
        ArrayList<Integer> borrowedRooms; // list of Room ID's of rooms shared to this/other user by current user - for use in sharing modal in colregCollab.jsp
        ArrayList<Integer> borrowedSections; // list of Section ID's of sections shared to this/other user by current user - for use in sharing modal in colregCollab.jsp
        
        public UserEntry(String name, String collegeName, int id, int collegeId) {
            this.name = name;
            this.collegeName = collegeName;
            this.id = id;
            this.collegeId = collegeId;
        }
        
        public UserEntry(int id) {
            this.id = id;
        }
        
        public void setUserDetails (int userId, Connection conn) {
            try {
                PreparedStatement pst = conn.prepareStatement("SELECT tblUser.name AS userName, tblCollege.college_id AS collegeId, tblCollege.code AS collegeCode "
                        + "FROM tblUser INNER JOIN tblCollege ON tblUser.fk_college_id = tblCollege.college_id "
                        + "WHERE user_id=?");
                pst.setString(1, String.valueOf(userId));

                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    this.name = rs.getString("userName");
                    this.collegeName = rs.getString("collegeCode");
                    this.collegeId = rs.getInt("collegeId");
                    return;
                }
            } catch (SQLException ex) {
                Logger.getLogger(SchedSharingService.class.getName()).log(Level.SEVERE, null, ex);
            } 
            return;
        }
        
        public void setBorrowedSections(int grantorId, Connection conn){
            this.borrowedSections = new ArrayList<>();
            try {
                PreparedStatement pst = conn.prepareStatement("SELECT fk_section_id AS sectionId from tblSharedSectionEditor WHERE fk_user_id=? AND fk_grantor_id=?");
                pst.setString(1, String.valueOf(this.id));
                pst.setString(2, String.valueOf(grantorId));

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    this.borrowedSections.add(rs.getInt("sectionId"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(SchedSharingService.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        public void setBorrowedRooms(int grantorId, Connection conn){
            this.borrowedRooms = new ArrayList<>();
            try {
                PreparedStatement pst = conn.prepareStatement("SELECT fk_room_id AS roomId from tblSharedRoomEditor WHERE fk_user_id=? AND fk_grantor_id=?");
                pst.setString(1, String.valueOf(this.id));
                pst.setString(2, String.valueOf(grantorId));

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    this.borrowedRooms.add(rs.getInt("roomId"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(SchedSharingService.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
    }
    // ^ to remove, not yeeeet nono
    
    public static class SharedEntry{
        String roomName, sectionName, collegeName, departmentName, course, otherUserName; // on otherUserName and otherUserId, this refers to the other user which can either be the grantor of the access () of the receiver of the access
        int sectionYear, sectionNumber;
        int id, collegeId, departmentId, courseId, otherUserId;
        String code, title, type, subjectName;
        String firstName, lastName, middleInitial, instructorName;
        /* on otherUserName and otherUserId, 
        this refers to the other user which can either be:
        1. the user who granted CurrentUser with access to room or section (for the getBorrowedSectionsList() and getBorrowedRoomsList())
        2. the user who received access to room or section, granted by the CurrentUser (for the getSharedSectionsList() and getSharedRoomsList())
        */
        
        // for section
        public SharedEntry(int sectionYear, int sectionNumber, String collegeName, String departmentName, String course, int id, int collegeId, int departmentId, int courseId, int otherUserId, String otherUserName) {
            this.sectionYear = sectionYear;
            this.sectionNumber = sectionNumber;
            this.collegeName = collegeName;
            this.departmentName = departmentName;
            this.course = course;
            this.sectionName = course + " " + sectionYear + "-" + sectionNumber;
            this.id = id;
            this.collegeId = collegeId;
            this.departmentId = departmentId;
            this.courseId = courseId;
            this.otherUserId = otherUserId;
            this.otherUserName = otherUserName;
        }

        // for room
        public SharedEntry(String roomName, String collegeName, String departmentName, int id, int collegeId, int departmentId, int otherUserId, String otherUserName) {
            this.roomName = roomName;
            this.collegeName = collegeName;
            this.departmentName = departmentName;
            this.id = id;
            this.collegeId = collegeId;
            this.departmentId = departmentId;
            this.otherUserId = otherUserId;
            this.otherUserName = otherUserName;
        }
        
        // for current-user-owned section - for use in getMySectionsList()
        public SharedEntry(int sectionYear, int sectionNumber, String departmentName, String course, int id, int departmentId, int courseId) {
            this.sectionYear = sectionYear;
            this.sectionNumber = sectionNumber;
            this.departmentName = departmentName;
            this.course = course;
            this.sectionName = course + " " + sectionYear + "-" + sectionNumber;
            this.id = id;
            this.departmentId = departmentId;
            this.courseId = courseId;
        }
        
        // for current-user-owned room - for use in getMyRoomsList()
        public SharedEntry(String roomName, String departmentName, int id, int departmentId) {
            this.roomName = roomName;
            this.departmentName = departmentName;
            this.id = id;
            this.departmentId = departmentId;
        }
        
        // for getMySubjectsList()
        public SharedEntry(String code, String title, String type, String departmentName, int id, int departmentId) {
            this.code = code;
            this.title = title;
            this.type = type;
            this.departmentName = departmentName;
            this.subjectName = code + " - " + type;
            this.id = id;
            this.departmentId = departmentId;
        }
        
        // for getMyInstructorsList()
        public SharedEntry(String firstName, String middleInitial, String lastName, int id, String departmentName, int departmentId) {
            this.firstName = firstName;
            this.middleInitial = middleInitial;
            this.lastName = lastName;
            this.departmentName = departmentName;
            this.instructorName = lastName + ", " + firstName + " " + middleInitial + ".";
            this.id = id;
            this.departmentId = departmentId;
        }
    }
    
    // Node compatible with Angular IVH Treeview, the library used to create the nested checkbox list in the colreg_collab.jsp
    public static class CheckboxTreeNode{
        String label;
        SharedEntry value;
        ArrayList<CheckboxTreeNode> children;
        
        public CheckboxTreeNode(String label){
            this.label = label;
        }
        
        public CheckboxTreeNode(String label, SharedEntry value){
            this.label = label;
            this.value = value;
        }
        
        public void addChild(CheckboxTreeNode child){
          if(children == null) children = new ArrayList<>();
          children.add(child);
        }
    }
    
    //SectionTree structure - compatible with Angular IVH Treeview, the library used to create the nested checkbox list in the colreg_collab.jsp
    private void addToSectionTree(ArrayList<CheckboxTreeNode> list, SharedEntry section){
        boolean collegeFound=false, departmentFound=false, courseFound=false;
        
        for(CheckboxTreeNode college : list){
            if(college.label.equals(section.collegeName)){
                collegeFound = true;
                for(CheckboxTreeNode department : college.children){
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
                    college.addChild(departmentNode);
                    CheckboxTreeNode courseNode = new CheckboxTreeNode(section.course);
                    college.children.get(college.children.size()-1).addChild(courseNode);
                    CheckboxTreeNode sectionNode = new CheckboxTreeNode(section.sectionName, section);
                    college.children.get(college.children.size()-1).children.get(0).addChild(sectionNode);
                    return;
                }
            }
        }
        
        if(!collegeFound){
            CheckboxTreeNode collegeNode = new CheckboxTreeNode(section.collegeName);
            list.add(collegeNode);
            CheckboxTreeNode departmentNode = new CheckboxTreeNode(section.departmentName);
            list.get(list.size()-1).addChild(departmentNode);
            CheckboxTreeNode courseNode = new CheckboxTreeNode(section.course);
            list.get(list.size()-1).children.get(0).addChild(courseNode);
            CheckboxTreeNode sectionNode = new CheckboxTreeNode(section.sectionName, section);
            list.get(list.size()-1).children.get(0).children.get(0).addChild(sectionNode);
            return;
        }
    }
    
    //RoomTree structure - compatible with Angular IVH Treeview, the library used to create the nested checkbox list in the colreg_collab.jsp
    private void addToRoomTree(ArrayList<CheckboxTreeNode> list, SharedEntry room){
        boolean collegeFound=false, departmentFound=false;
        
        for(CheckboxTreeNode college : list){
            if(college.label.equals(room.collegeName)){
                collegeFound = true;
                for(CheckboxTreeNode department : college.children){
                    if(department.label.equals(room.departmentName)){
                        departmentFound = true;
                        CheckboxTreeNode roomNode = new CheckboxTreeNode(room.roomName, room);
                        department.addChild(roomNode);
                        return;
                    }
                }
                
                if(!departmentFound) {
                    CheckboxTreeNode departmentNode = new CheckboxTreeNode(room.departmentName);
                    college.addChild(departmentNode);
                    CheckboxTreeNode roomNode = new CheckboxTreeNode(room.roomName, room);
                    college.children.get(college.children.size()-1).addChild(roomNode);
                    return;
                }
            }
        }
        
        if(!collegeFound){
            CheckboxTreeNode collegeNode = new CheckboxTreeNode(room.collegeName);
            list.add(collegeNode);
            CheckboxTreeNode departmentNode = new CheckboxTreeNode(room.departmentName);
            list.get(list.size()-1).addChild(departmentNode);
            CheckboxTreeNode roomNode = new CheckboxTreeNode(room.roomName, room);
            list.get(list.size()-1).children.get(0).addChild(roomNode);
            return;
        }
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
}
