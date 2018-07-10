
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <style>
            div {
                border-radius: 5px;
                background-color: #45a049;
                padding-top: 50px;
                padding-left: 30px;
                height: 20px;
            }
            a{
                color:#45a049;
                text-decoration:none;
            }

            ul {
                list-style-type: none;
                margin: 0;
                padding: 0;
                overflow: hidden;
                background-color: #333;
            }

            li {
                float: left;
                border-right: 1px solid #bbb;
            }

            li a {
                display: block;
                color: white;
                text-align: center;
                padding: 14px 16px;
                text-decoration: none;
            }

            .active {
                background-color: #4CAF50;
            }

            h1{
                color: white;
                position:absolute;
                top:19px;
                font-family: "Open Sans Condensed", sans-serif;
            }

            body{
                background-color: #f2f2f2;
            }
        </style>

    </head>
    <body>
        <div>
            <h1>Admin's Chat and Notifications</h1>
        </div>
        <br><br><br>
    <div style="position:absolute; background-color:transparent; height:50px; left:8px; top:95px; padding:0px;">
        <ul>
            <li><a href="admin_account_mngt.jsp">Account Management (create and edit college registrar accounts)</a></li>
            <li><a href="admin_sched.jsp">View Schedules</a></li>
            <li><a href="admin_collab.jsp">Chat & Notifications</a></li>
            <li><a href="index.jsp">Log Out</a></li>
        </ul>    
    </div>
</body>
</html>
