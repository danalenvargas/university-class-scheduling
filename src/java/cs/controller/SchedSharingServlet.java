 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import cs.model.ScheduleCollection;
import cs.model.User;
import cs.service.SchedListService;
import cs.service.SchedSharingService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
@WebServlet(name = "SchedSharingServlet", urlPatterns = {"/schedSharing"})
public class SchedSharingServlet extends HttpServlet {

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
            out.println("<title>Servlet SchedSharingServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SchedSharingServlet at " + request.getContextPath() + "</h1>");
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
        int chatId;

        session = request.getSession(true);
        user = (User) session.getAttribute("user");

        //** TEST CODE
        if (user == null) {
            user = new User("testcolreg", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
            //user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 2, 4);
        }
        //** TEST CODE

        SchedSharingService sharingService = new SchedSharingService();

        switch (action) {
            case "getUsers":
                jsonString = sharingService.getUsersList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMySections":
                jsonString = sharingService.getMySectionsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMyRooms":
                jsonString = sharingService.getMyRoomsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getSharedSections":
                jsonString = sharingService.getSharedSectionsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getSharedRooms":
                jsonString = sharingService.getSharedRoomsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getBorrowedSections":
                jsonString = sharingService.getBorrowedSectionsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getBorrowedRooms":
                jsonString = sharingService.getBorrowedRoomsList(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;

            case "getNotifications":
                int numOfDays = Integer.parseInt(request.getParameter("days"));
                jsonString = sharingService.getNotifications(user, numOfDays);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getAffectedItems":
                String type = request.getParameter("type");
                int logId = Integer.parseInt(request.getParameter("logId"));
                jsonString = sharingService.getAffectedItems(logId, type);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getChatListForDropdown":
                jsonString = sharingService.getChatListForDropdown(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getChatSessions":
                jsonString = sharingService.getChatSessions(user);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMessages":
                chatId = Integer.parseInt(request.getParameter("chatId"));
                jsonString = sharingService.getMessages(user, chatId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getLastEdit":
                chatId = Integer.parseInt(request.getParameter("chatId"));
                jsonString = sharingService.getLastEdit(chatId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getMembers":
                chatId = Integer.parseInt(request.getParameter("chatId"));
                jsonString = sharingService.getMembers(user, chatId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
                
            case "getInvitees":
                chatId = Integer.parseInt(request.getParameter("chatId"));
                jsonString = sharingService.getInvitees(user, chatId);
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
        //int entryId = Integer.parseInt(request.getParameter("entryId")); // entryId is either the roomId or sectionId
        int entryId = 0; // DUMMY, to be deleted once functions has been updateds
        int otherUserId2 = 0; // DUMMY, to be deleted once functions has been updateds
        int chatPartnerId, chatId, userId;
        String selectedEntriesString;
        String jsonString, name;
        HttpSession session;
        User user;
        int otherUserId, i;
        String[] selectedEntriesArrayString;
        int selectedEntries[];

        session = request.getSession(true);
        user = (User) session.getAttribute("user");
        //** TEST CODE
        if (user == null) {
            user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 1, 2);
            //user = new User("testcolreg2", "123", "COLLEGE_REGISTRAR", 1, 2, 4);
        }
        //** TEST CODE
        
        SchedSharingService sharingService = new SchedSharingService();

        switch (action) {
            case "shareSection":
                otherUserId = Integer.parseInt(request.getParameter("otherUserId"));
//                selectedEntriesString = request.getParameter("selectedEntries");
//                System.out.println("shareSection, selectedEntries:");
//                System.out.println(selectedEntriesString);
//                int selectedEntries[] = null;
//                if(selectedEntriesString !=null){
//                       String array[] = selectedEntriesString.split(",");
//                       selectedEntries = new int[array.length];
//                       for(int i=0;i<array.length;i++){
//                           try{
//                            selectedEntries[i] = Integer.parseInt(array[i]); 
//                           }catch(Exception e){
//                           }
//                       }
//                }
                selectedEntriesArrayString = request.getParameterValues("selectedEntries");
                System.out.println("shareSection, selectedEntries:");
                System.out.println(Arrays.toString(selectedEntriesArrayString));
                selectedEntries = new int[selectedEntriesArrayString.length];
                i=0 ;
                for (String element : selectedEntriesArrayString) {
                    selectedEntries[i] = Integer.parseInt(element);
                    i++;
                }
                sharingService.shareSection(user, otherUserId, selectedEntries);
                break;
            case "shareRoom":
                otherUserId = Integer.parseInt(request.getParameter("otherUserId"));
                selectedEntriesArrayString = request.getParameterValues("selectedEntries");
                System.out.println("shareSection, selectedEntries:");
                System.out.println(Arrays.toString(selectedEntriesArrayString));
                selectedEntries = new int[selectedEntriesArrayString.length];
                i=0 ;
                for (String element : selectedEntriesArrayString) {
                    selectedEntries[i] = Integer.parseInt(element);
                    i++;
                }
                sharingService.shareRoom(user, otherUserId, selectedEntries);
                //sharingService.shareRoom(user, otherUserId2, entryId);
                break;
            case "pullSection":
                selectedEntriesString = request.getParameter("selectedEntries");
                sharingService.pullSection(user, selectedEntriesString);
                break;
            case "pullRoom":
                selectedEntriesString = request.getParameter("selectedEntries");
                sharingService.pullRoom(user, selectedEntriesString);
                //sharingService.pullRoom(user, otherUserId2, entryId);
                break;
            case "returnSection":
                selectedEntriesString = request.getParameter("selectedEntries");
                sharingService.returnSection(user, selectedEntriesString);
                break;
            case "returnRoom":
                selectedEntriesString = request.getParameter("selectedEntries");
                sharingService.returnRoom(user, selectedEntriesString);
                //sharingService.returnRoom(user, otherUserId2, entryId);
                break;
            case "createGroupChat":
                name = request.getParameter("name");
                jsonString = sharingService.createGroupChat(user, name);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "createIndividualChat":
                chatPartnerId = Integer.parseInt(request.getParameter("chatPartnerId"));
                jsonString = sharingService.createIndividualChat(user, chatPartnerId);
                response.setContentType("application/json");
                response.getWriter().write(jsonString);
                break;
            case "sendMessage":
                chatId = Integer.parseInt(request.getParameter("chatId"));
                String text = request.getParameter("text");
                sharingService.sendMessage(chatId, text, user);
                break;
            case "addGroupChatMember":
                chatId = Integer.parseInt(request.getParameter("chatId"));
                userId = Integer.parseInt(request.getParameter("userId"));
                sharingService.addGroupChatMember(chatId, userId);
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
