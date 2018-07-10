
<%@page import="cs.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Success</title>
        <style>
            div {
            border-radius: 5px;
            background-color: #45a049;
            padding: 50px;
            height: 200px;
            }
        </style>
		
		<style>
            div.heading {
                /*margin: 5px;*/
                margin-bottom: 5px;
                border-radius: 5px;
                background-color: #45a049;
                padding-top: 80px;
                padding-left: 30px;
                height: 10px;
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
                top:3%;
                font-family: "Open Sans Condensed", sans-serif;
            }
			
            h2{
                color: white;
                position:absolute;
                top:8%;
                font-family: "Open Sans Condensed", sans-serif;
            }

            body{
                margin: 5px;
                /*background-color: #f2f2f2;*/
            }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
        %>
        <div class="heading">
        <h1>Hello <%=user.getUserName()%></h1>
		<h2>You have successfully logged in as a SCHEDULER</h2>
        <br><br><br>
        </div>
        
		<center>
		<div class="heading" style="background-color:transparent; height:50px; left:8px; top:95px; padding:0px;">
        
		<ul>
			<li><a href="scheduler_sched.jsp">Plot Schedules</a></li>
			<!--<li><a href="scheduler_validate.jsp">Validation</a></li>-->
			<li><a href="scheduler_collab.jsp">Chat & Notifications</a></li>
			<li><a href="index.jsp">Log Out</a></li>
		</ul>
        </div>
		</center>
        
    </body>
</html>
