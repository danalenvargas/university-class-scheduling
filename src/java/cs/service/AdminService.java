/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import com.google.gson.Gson;
import cs.model.Department;
import cs.model.Sem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

/**
 *
 * @author Dan
 */
public class AdminService {

    static Connection conn = null;
    static DataSource ds;
    static InitialContext ctx;
    static PreparedStatement pst = null;
    static ResultSet rs = null;

    public static String getSemList(int activeSemId) {
        ArrayList<Sem> semList = new ArrayList<>();
        int semId, sem, schoolYear;
        boolean isActive, isFinalized;
        Timestamp ts;
        Date entryDate;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT * "
                    + "FROM tblSem "
                    + "WHERE sem_id!=?");
            pst.setString(1, String.valueOf(activeSemId));

            rs = pst.executeQuery();

            while (rs.next()) {
                semId = rs.getInt("sem_id");
                sem = rs.getInt("sem");
                schoolYear = rs.getInt("school_year");
                isActive = rs.getBoolean("isActive");
                isFinalized = rs.getBoolean("isFinalized");
                ts = rs.getTimestamp("entryDate");
                entryDate = ts;

                Sem newSem = new Sem(semId, sem, schoolYear, isActive, isFinalized, entryDate);
                semList.add(newSem);
            }

            String jsonSelectionList = new Gson().toJson(semList);
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

    public static String addSemester (int schoolYear, int semNum) {
        int lastId;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("INSERT INTO tblSem(sem, school_year) VALUES(?,?)");
            pst.setString(1, String.valueOf(semNum));
            pst.setString(2, String.valueOf(schoolYear));
            pst.executeUpdate();

            pst = conn.prepareStatement("SELECT LAST_INSERT_ID() as lastId from tblSem");
            rs =  pst.executeQuery();
            if(rs.next()){
                lastId = rs.getInt("lastId");
                String jsonLastId = new Gson().toJson(lastId);
                return jsonLastId;
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
    
    public static void changeActiveSemester (int semIdCurrent, int semIdToActivate) {
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblSem SET isActive=0 WHERE sem_id=?");
            pst.setString(1, String.valueOf(semIdCurrent));
            pst.executeUpdate();

            pst = conn.prepareStatement("UPDATE tblSem SET isActive=1 WHERE sem_id=?");
            pst.setString(1, String.valueOf(semIdToActivate));
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
    
    public static void toggleIsFinalized (int semId, boolean isFinalized) {
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("UPDATE tblSem SET isFinalized=? WHERE sem_id=?");
            pst.setString(1, String.valueOf(isFinalized? 0 : 1));
            pst.setString(2, String.valueOf(semId));
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
}
