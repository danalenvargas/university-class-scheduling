
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        
        <script src="script/angular.js" type="text/javascript"></script>
        <script src="script/angular-route.js" type="text/javascript"></script>
        <script src="script/angular-animate.js" type="text/javascript"></script>
        <script src="script/colregSetup.js" type="text/javascript"></script>
        <link href="css/classScheduler.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
        
        
        <style>
                        table, th , td {
                            border: 1px solid grey;
                            border-collapse: collapse;
                            padding: 5px;
                        }
            
                        table tr:nth-child(odd) {
                            background-color: #f2f2f2;
                        }
            
                        table tr:nth-child(even) {
                            background-color: #ffffff;
                        }

/*            table {
                width:90%;
                border-top:1px solid #e5eff8;
                border-right:1px solid #e5eff8;
                margin:1em auto;
                border-collapse:collapse;
            }
            td {
                color:#678197;
                border-bottom:1px solid #e5eff8;
                border-left:1px solid #e5eff8;
                padding:.3em 1em;
                text-align:center;
            }
            tr.odd td {
                background:#f7fbff
            }
            tr.odd .column1 {
                background:#f4f9fe;
            }
            .column1 {
                background:#f9fcfe;
            }

            thead th {
                background:#f4f9fe;
                text-align:center;
                font:bold 1.2em/2em "Century Gothic","Trebuchet MS",Arial,Helvetica,sans-serif;
                color:#66a3d3;
            }*/
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
            a.heading {
                color:#45a049;
                text-decoration:none;
            }

            ul.heading {
                list-style-type: none;
                margin: 0;
                padding: 0;
                overflow: hidden;
                background-color: #333;

            }

            li.heading {
                float: left;
                border-right: 1px solid #bbb;
            }

            li a.heading {
                display: block;
                color: white;
                text-align: center;
                padding: 14px 16px;
                text-decoration: none;
            }

            .active {
                background-color: #4CAF50;
            }

            h1.heading{
                color: white;
                position:absolute;
                top:19px;
                font-family: "Open Sans Condensed", sans-serif;
            }

            body{
                margin: 5px;
                /*background-color: #f2f2f2;*/
            }
        </style>
    </head>
    <body div ng-app="colregSetup" ng-controller="SetupCtrl">
        <div class="heading">
            <h1 class="heading">Setup sections/subjects/curriculums/instructors/rooms/courses information</h1>
        </div>
        <div class="heading" style="background-color:transparent; height:50px; left:8px; top:95px; padding:0px;">
            <ul class="heading">
                <li class="heading"><a class="heading" href="colreg_account_mngt.jsp">Account Management <br>(create and edit scheduler accounts)</a></li>
                <li class="heading"><a class="heading" href="colreg_collab.jsp">Chat & Notifications & Schedule Sharing</a></li>
                <li class="heading"><a class="heading" href="colreg_sched.jsp">Scheduling (plot schedules)</a></li>
                <!--<li class="heading"><a class="heading" href="colreg_validate.jsp">Validate schedules</a></li>-->
                <li class="heading"><a class="heading" href="colreg_setup.jsp">Setup (create edit delete info)</a></li>
                <li class="heading"><a class="heading" href="index.jsp">Log Out</a></li>
            </ul>
        </div>
        <br /><br />
        
        <!--<h1>Setup sections/subjects/curriculums/instructors/rooms/courses information</h1>-->
        <a href="#/section">Sections</a>
        <a href="#/subject">Subjects</a>
        <a href="#/course">Courses</a>
        <a href="#/curriculum">Curriculum</a>
        <a href="#/instructor">Instructors</a>
        <a href="#/room">Rooms</a>
        <div ng-view=""></div>
    </body>
</html>
