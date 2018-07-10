/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.service;

import cs.model.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.SQLException;
import java.util.Set;

/**
 *
 * @author Dan
 */
public class LoginService {

    private User user;

    public LoginService() {
    }

    public boolean isValidUser(String userName, String password) {
        user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        
        boolean status = false;
        Connection conn = null;
        DataSource ds;
        InitialContext ctx;
        PreparedStatement pst = null;
        ResultSet rs = null;
        //Integer type;
        String userType;
        int departmentID, collegeID, userID;
        
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/ClassSchedulerDS");
            conn = ds.getConnection();

            pst = conn.prepareStatement("SELECT * FROM tblUser WHERE name=? AND password=?");
            pst.setString(1, userName);
            pst.setString(2, password);

            rs = pst.executeQuery();
            status = rs.next();
            
            if(status){
                userType = rs.getString("user_type");
                departmentID = rs.getInt("fk_department_id");
                collegeID = rs.getInt("fk_college_id");
                userID = rs.getInt("user_id");
                user.setUserType(userType);
                user.setCollegeID(collegeID);
                user.setDepartmentID(departmentID);
                user.setUserID(userID);
            }
        } catch (NamingException ex) {
            Logger.getLogger(LoginService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(LoginService.class.getName()).log(Level.SEVERE, null, ex);
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
        return status;
    }

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
}
