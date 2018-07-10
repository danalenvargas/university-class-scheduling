/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cs.model.SchedEntry;
import cs.model.SchedList;
import cs.model.ScheduleCollection;
import cs.model.User;
import cs.service.SchedListService;
import java.io.BufferedReader;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Dan
 */
@WebServlet(name = "ScheduleServlet", urlPatterns = {"/SchedTable"})
public class SchedTableServlet extends HttpServlet {

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
            out.println("<title>Servlet ScheduleServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ScheduleServlet at " + request.getContextPath() + "</h1>");
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
        String function = request.getParameter("function");
        String jsonString;
        String type;
        int itemId, departmentId, collegeId, schedId;
        int start, end, origStart, origEnd, origItem;
        HttpSession session;
        User user;

        ServletContext servletContext = request.getServletContext();
        ScheduleCollection schedCollection = (ScheduleCollection) servletContext.getAttribute("schedCollection");
        SchedListService tableService = new SchedListService(schedCollection);
        
        Integer changeCounter = (Integer) servletContext.getAttribute("changeCounter");
        System.out.println("inside doGet, changeCounter: " + changeCounter);

        switch (function) {
            case "getChangeCounter":
                jsonString = String.valueOf(changeCounter);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getSched":
                type = request.getParameter("type");
                itemId = Integer.parseInt(request.getParameter("itemId"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));

                jsonString = tableService.generateSchedList(type, collegeId, departmentId, itemId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "changeView":
                session = request.getSession(true);
                user = (User) session.getAttribute("user");

                //** TEST CODE
                if (user == null) {
                    user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
                }
                //** TEST CODE

                jsonString = tableService.generateSelectionList(user, request.getParameter("type"), request.getParameter("scope"));
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getSubjectList":
                itemId = Integer.parseInt(request.getParameter("itemId"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));

                jsonString = tableService.generateSubjectList(collegeId, departmentId, itemId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getFreeList":
                session = request.getSession(true);
                user = (User) session.getAttribute("user");
                //** TEST CODE
//                if (user == null) {
//                    user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
//                }
                //** TEST CODE
//                int origStart, origEnd, origItem;
                String type2 = request.getParameter("type");
                start = Integer.parseInt(request.getParameter("start"));
                end = Integer.parseInt(request.getParameter("end"));
                origStart = Integer.parseInt(request.getParameter("origStart"));
                origEnd = Integer.parseInt(request.getParameter("origEnd"));
                origItem = Integer.parseInt(request.getParameter("origItem"));

                collegeId = user.getCollegeID();
                System.out.println("at servlet: " + collegeId + " " + start + " " + end + " " + type2);   //DEBUG
                jsonString = tableService.generateFreeList(collegeId, start, end, type2, origStart, origEnd, origItem, user);
                System.out.println("FREELIST: " + jsonString);  //DEBUG
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getHistory":
                type = request.getParameter("type");
                itemId = Integer.parseInt(request.getParameter("itemId"));
                start = Integer.parseInt(request.getParameter("start"));
                end = Integer.parseInt(request.getParameter("end"));
                jsonString = tableService.generateSectionHistoryList(start, end, itemId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "debugGetCollection":
                jsonString = tableService.debugGetCollectoin();
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            
            case "getCurrentUser":
                session = request.getSession(true);
                user = (User) session.getAttribute("user");
                jsonString = new Gson().toJson(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getComments":
                schedId = Integer.parseInt(request.getParameter("schedId"));
                jsonString = tableService.getComments(schedId);
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

        ServletContext servletContext = request.getServletContext();
        ScheduleCollection schedCollection = (ScheduleCollection) servletContext.getAttribute("schedCollection");
        SchedListService tableService = new SchedListService(schedCollection);
        
        Integer changeCounter = (Integer) servletContext.getAttribute("changeCounter");
        System.out.println("in doPost, initial changeCounter:" + changeCounter);
        
        HttpSession session;
        User user;
        
        session = request.getSession(true);
        user = (User) session.getAttribute("user");
        //** TEST CODE
//        if (user == null) {
//            user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
//        }
        //** TEST CODE

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(request.getReader());

        String action = obj.get("action").getAsString();
        System.out.println("inside POST, action: ");
        System.out.println(action);
        SchedEntry origSched;
        if (null != action) switch (action) {
            case "add":
                int sectionId = obj.get("section").getAsInt();
                int instructorId = obj.get("instructor").getAsInt();
                int roomId = obj.get("room").getAsInt();
                int subjectId = obj.get("subject").getAsInt();
                int start = obj.get("start").getAsInt();
                int end = obj.get("end").getAsInt();
                origSched = gson.fromJson(obj.get("origSched"), SchedEntry.class);
                String jsonString = tableService.addSchedule(sectionId, instructorId, roomId, subjectId, start, end, origSched, user);
                response.setContentType("application/json");
                changeCounter += 1;
                servletContext.setAttribute("changeCounter", changeCounter);
                response.getWriter().write(jsonString);
                break;
            case "delete":
                origSched = gson.fromJson(obj.get("origSched"), SchedEntry.class);
                tableService.deleteSchedule(origSched, user);
                changeCounter += 1;
                servletContext.setAttribute("changeCounter", changeCounter);
                break;
            case "validate":
                tableService.validateSchedule(obj.get("newStatus").getAsString(), obj.get("schedId").getAsInt(), obj.get("isReturn")!=null? obj.get("isReturn").getAsBoolean():false);
                changeCounter += 1;
                servletContext.setAttribute("changeCounter", changeCounter);
                break;
            case "addComment":
                tableService.addComment(obj.get("schedId").getAsInt(), obj.get("text").getAsString(), user);
                break;
            case "requestReturn":
                tableService.requestReturn(obj.get("schedId").getAsInt());
                changeCounter += 1;
                servletContext.setAttribute("changeCounter", changeCounter);
                break;
            case "denyReturn":
                tableService.denyReturn(obj.get("schedId").getAsInt());
                changeCounter += 1;
                servletContext.setAttribute("changeCounter", changeCounter);
                break;
        }
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
