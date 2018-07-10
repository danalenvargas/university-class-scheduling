/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.controller;

import cs.model.ScheduleCollection;
import cs.model.User;
import cs.service.AccountManagementService;
import cs.service.SchedSharingService;
import cs.service.SetupService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Dan
 */
@WebServlet(name = "AccountManagementServlet", urlPatterns = {"/accountManagement"})
public class AccountManagementServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AccountManagementServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AccountManagementServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String jsonString;
        HttpSession session;
        User user;
        int schedulerId;

        session = request.getSession(true);
        user = (User) session.getAttribute("user");

        //** TEST CODE
        if (user == null) {
            user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
            //user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 2, 4);
        }
        //** TEST CODE

        AccountManagementService accountManagementService = new AccountManagementService();

        switch (action) {
            case "getMySections":
                jsonString = accountManagementService.getMySectionsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMyRooms":
                jsonString = accountManagementService.getMyRoomsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMySubjects":
                jsonString = accountManagementService.getMySubjectsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMyInstructors":
                jsonString = accountManagementService.getMyInstructorsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getSchedulerList":
                jsonString = accountManagementService.getSchedulersList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getAssignedSections":
                schedulerId = Integer.parseInt(request.getParameter("schedulerId"));
                jsonString = accountManagementService.getAssignedSections(schedulerId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getAssignedRooms":
                schedulerId = Integer.parseInt(request.getParameter("schedulerId"));
                jsonString = accountManagementService.getAssignedRooms(schedulerId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getAssignedSubjects":
                schedulerId = Integer.parseInt(request.getParameter("schedulerId"));
                jsonString = accountManagementService.getAssignedSubjects(schedulerId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getAssignedInstructors":
                schedulerId = Integer.parseInt(request.getParameter("schedulerId"));
                jsonString = accountManagementService.getAssignedInstructors(schedulerId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getCanEditTime":
                schedulerId = Integer.parseInt(request.getParameter("schedulerId"));
                jsonString = accountManagementService.getCanEditTime(schedulerId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getColregList":
                jsonString = accountManagementService.getColregList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getCollegeList":
                jsonString = accountManagementService.getCollegeList();
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String jsonString;
        HttpSession session;
        User user;
        
        int departmentId, canEditTime, schedulerId, colregId, collegeId;
        String name, password;
        int[] selectedSections, selectedRooms, selectedInstructors, selectedSubjects;
        

        session = request.getSession(true);
        user = (User) session.getAttribute("user");

        //** TEST CODE
        if (user == null) {
            user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
            //user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 2, 4);
        }
        
        
        ServletContext servletContext = request.getServletContext();
        ScheduleCollection schedCollection = (ScheduleCollection) servletContext.getAttribute("schedCollection");
        AccountManagementService acctService = new AccountManagementService();

        switch (action) {
            case "addScheduler":
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                canEditTime = Boolean.parseBoolean(request.getParameter("canEditTime")) ? 1 : 0;
                name = request.getParameter("name");
                password = request.getParameter("password");
                selectedSections = parseStringArray(request.getParameterValues("selectedSections"));
                selectedRooms = parseStringArray(request.getParameterValues("selectedRooms"));
                selectedInstructors = parseStringArray(request.getParameterValues("selectedInstructors"));
                selectedSubjects = parseStringArray(request.getParameterValues("selectedSubjects"));
                
                acctService.addScheduler(departmentId, name, password, canEditTime, selectedSections, selectedRooms, selectedInstructors, selectedSubjects, user);
                break;
                
            case "editScheduler":
                schedulerId = Integer.parseInt(request.getParameter("userId"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                canEditTime = Boolean.parseBoolean(request.getParameter("canEditTime")) ? 1 : 0;
                name = request.getParameter("name");
                password = request.getParameter("password");
                selectedSections = parseStringArray(request.getParameterValues("selectedSections"));
                selectedRooms = parseStringArray(request.getParameterValues("selectedRooms"));
                selectedInstructors = parseStringArray(request.getParameterValues("selectedInstructors"));
                selectedSubjects = parseStringArray(request.getParameterValues("selectedSubjects"));
                
                acctService.editScheduler(schedulerId, departmentId, name, password, canEditTime, selectedSections, selectedRooms, selectedInstructors, selectedSubjects, user);
                break;
                
            case "editColreg":
                colregId = Integer.parseInt(request.getParameter("userId"));
                name = request.getParameter("name");
                password = request.getParameter("password");
                
                acctService.editColreg(colregId, name, password);
                break;
            
            case "addColreg":
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                name = request.getParameter("name");
                password = request.getParameter("password");
                
                acctService.addColreg(collegeId, name, password);
                break;
        }
    }
    
    private int[] parseStringArray(String[] arrayString){
        if(arrayString != null){
            int[] selectedEntries = new int[arrayString.length];
            int i=0 ;
            for (String element : arrayString) {
                selectedEntries[i] = Integer.parseInt(element);
                i++;
            }
            return selectedEntries;
        } else return null;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
