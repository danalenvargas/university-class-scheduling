/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.controller;

import cs.model.ScheduleCollection;
import cs.model.User;
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
@WebServlet(name = "SetupServlet", urlPatterns = {"/setup"})
public class SetupServlet extends HttpServlet {

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
            out.println("<title>Servlet SetupServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SetupServlet at " + request.getContextPath() + "</h1>");
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
        
        int courseId, sectionYear, sectionNumber;

        session = request.getSession(true);
        user = (User) session.getAttribute("user");

        //** TEST CODE
        if (user == null) {
            user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
            //user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 2, 4);
        }
        
        SetupService setupService = new SetupService();

        switch (action) {
            case "getSectionList":
                jsonString = setupService.getSectionList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getCourseList":
                jsonString = setupService.getCourseList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getTakenNumbersList":
                courseId = Integer.parseInt(request.getParameter("courseId"));
                sectionYear = Integer.parseInt(request.getParameter("year"));
                jsonString = setupService.getTakenNumbersList(user, courseId, sectionYear);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getRoomList":
                jsonString = setupService.getRoomList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getDepartmentList":
                jsonString = setupService.getDepartmentList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getInstructorList":
                jsonString = setupService.getInstructorList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getSubjectList":
                jsonString = setupService.getSubjectList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getCurriculum":
                courseId = Integer.parseInt(request.getParameter("courseId"));
                jsonString = setupService.getCurriculum(courseId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "getAllSubjects":
                jsonString = setupService.getAllSubjects(user);
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
        
        int courseId, sectionId, sectionYear, sectionNumber, departmentId, collegeId, roomId, instructorId, subjectId, hours, units, curriculumId;
        float sem;
        int newNumber, newHours, newUnits;
        String name, newName, firstName, middleInitial, lastName, title, code, type;
        String newFirstName, newMiddleInitial, newLastName, newTitle, newCode, newType;

        session = request.getSession(true);
        user = (User) session.getAttribute("user");

        //** TEST CODE
        if (user == null) {
            user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
            //user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 2, 4);
        }
        
        
        ServletContext servletContext = request.getServletContext();
        ScheduleCollection schedCollection = (ScheduleCollection) servletContext.getAttribute("schedCollection");
        SetupService setupService = new SetupService(schedCollection);

        switch (action) {
            case "addSection":
                courseId = Integer.parseInt(request.getParameter("courseId"));
                sectionYear = Integer.parseInt(request.getParameter("year"));
                sectionNumber = Integer.parseInt(request.getParameter("number"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.addSection(user, courseId, sectionYear, sectionNumber, departmentId, collegeId);
                break;
            case "deleteSection":
                sectionId = Integer.parseInt(request.getParameter("sectionId"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.deleteSection(user, sectionId, departmentId, collegeId);
                break;
            case "editSection":
                sectionId = Integer.parseInt(request.getParameter("sectionId"));
                newNumber = Integer.parseInt(request.getParameter("newNumber"));
                setupService.editSection(sectionId, newNumber);
                break;
            case "addRoom":
                name = request.getParameter("name");
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.addRoom(name, departmentId, collegeId);
                break;
            case "deleteRoom":
                roomId = Integer.parseInt(request.getParameter("roomId"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.deleteRoom(roomId, departmentId, collegeId);
                break;
            case "editRoom":
                roomId = Integer.parseInt(request.getParameter("roomId"));
                newName = request.getParameter("newName");
                setupService.editRoom(roomId, newName);
                break;
            case "addInstructor":
                firstName = request.getParameter("firstName");
                middleInitial = request.getParameter("middleInitial");
                lastName = request.getParameter("lastName");
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.addInstructor(firstName, middleInitial, lastName, departmentId, collegeId);
                break;
            case "deleteInstructor":
                instructorId = Integer.parseInt(request.getParameter("instructorId"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.deleteInstructor(instructorId, departmentId, collegeId);
                break;
            case "editInstructor":
                instructorId = Integer.parseInt(request.getParameter("instructorId"));
                newFirstName = request.getParameter("newFirstName");
                newMiddleInitial = request.getParameter("newMiddleInitial");
                newLastName = request.getParameter("newLastName");
                setupService.editInstructor(instructorId, newFirstName, newMiddleInitial, newLastName);
                break;
            case "addSubject":
                title = request.getParameter("title");
                code = request.getParameter("code");
                type = request.getParameter("type");
                hours = Integer.parseInt(request.getParameter("hours"));
                units = Integer.parseInt(request.getParameter("units"));
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                //collegeId = Integer.parseInt(request.getParameter("collegeId"));
                setupService.addSubject(title, code, type, hours, units, departmentId);
                break;
            case "editSubject":
                subjectId = Integer.parseInt(request.getParameter("subjectId"));
                newTitle = request.getParameter("newTitle");
                newCode = request.getParameter("newCode");
                newType = request.getParameter("newType");
                newHours = Integer.parseInt(request.getParameter("newHours"));
                newUnits = Integer.parseInt(request.getParameter("newUnits"));
                setupService.editSubject(subjectId, newTitle, newCode, newType, newHours, newUnits);
                break;
            case "deleteSubject":
                subjectId = Integer.parseInt(request.getParameter("subjectId"));
                setupService.deleteSubject(subjectId);
                break;
            case "addCourse":
                name = request.getParameter("name");
                code = request.getParameter("code");
                departmentId = Integer.parseInt(request.getParameter("departmentId"));
                setupService.addCourse(name, code, departmentId);
                break;
            case "editCourse":
                courseId = Integer.parseInt(request.getParameter("courseId"));
                newName = request.getParameter("newName");
                newCode = request.getParameter("newCode");
                setupService.editCourse(courseId, newName, newCode);
                break;
            case "deleteCourse":
                courseId = Integer.parseInt(request.getParameter("courseId"));
                setupService.deleteCourse(courseId);
                break;
            case "addToCurriculum":
                subjectId = Integer.parseInt(request.getParameter("subjectId"));
                sem = Float.parseFloat(request.getParameter("sem"));
                courseId = Integer.parseInt(request.getParameter("courseId"));
                setupService.addToCurriculum(subjectId, sem, courseId);
                break;
            case "deleteFromCurriculum":
                subjectId = Integer.parseInt(request.getParameter("subjectId"));
                curriculumId = Integer.parseInt(request.getParameter("curriculumId"));
                setupService.deleteFromCurriculum(subjectId, curriculumId);
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
