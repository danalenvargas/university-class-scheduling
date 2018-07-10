/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.controller;

import com.google.gson.Gson;
import cs.model.Sem;
import cs.service.AdminService;
import cs.service.InitializeListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
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
@WebServlet(name = "AdminManageServlet", urlPatterns = {"/AdminManage"})
public class AdminManageServlet extends HttpServlet {

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
            out.println("<title>Servlet AdminManageServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminManageServlet at " + request.getContextPath() + "</h1>");
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
        ServletContext servletContext = request.getServletContext();
        Sem activeSem = (Sem) servletContext.getAttribute("activeSem");
        
        switch(action){
            case "getActiveSem":
                jsonString = new Gson().toJson(activeSem);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getSemList":
                jsonString = AdminService.getSemList(activeSem.getSemId());
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
        ServletContext servletContext = request.getServletContext();
        Sem activeSem = (Sem) servletContext.getAttribute("activeSem");
        
        int semId, schoolYear, semNum;
        boolean isFinalized;
        
        switch(action){
            case "addSem":
                schoolYear = Integer.parseInt(request.getParameter("schoolYear"));
                semNum = Integer.parseInt(request.getParameter("semNum"));
                jsonString = AdminService.addSemester(schoolYear, semNum);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "changeActiveSem":
                semId = Integer.parseInt(request.getParameter("semId"));
                AdminService.changeActiveSemester(activeSem.getSemId(), semId);
                InitializeListener.initializeSchedCollection(servletContext);
                break;
            case "toggleIsFinalized":
                semId = Integer.parseInt(request.getParameter("semId"));
                isFinalized = Boolean.parseBoolean(request.getParameter("isFinalized"));
                AdminService.toggleIsFinalized(semId, isFinalized);
                Sem sem = (Sem) servletContext.getAttribute("activeSem");
                sem.setIsFinalized(!isFinalized);
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
