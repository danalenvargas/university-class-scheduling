
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Administrator - Account Management</title>

        <script src="script/angular.js" type="text/javascript"></script>
        <script src="script/angular-route.js" type="text/javascript"></script>
        <script src="script/angular-animate.js" type="text/javascript"></script>
        <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
<!--        <script src="script/ivh-treeview.js" type="text/javascript"></script>
        <link href="css/ivh-treeview.css" rel="stylesheet" type="text/css"/>-->
        <script src="script/adminAccountMngt.js" type="text/javascript"></script>

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
            
            .modal-lg .modal-dialog{
                height: 900px
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

/*            .active {
                background-color: #4CAF50;
            }*/

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
    <body ng-app="AdminAccountMngt" ng-controller="AdminAccountMngtCtrl">
        <div class="heading">
            <h1 class="heading">Account Management</h1>
        </div>
        <div class="heading" style="background-color:transparent; height:50px; left:8px; top:95px; padding:0px;">
            <ul class="heading">
                <li class="heading"><a class="heading" href="admin_manage.jsp">Manage</a></li>
                <li class="heading"><a class="heading" href="admin_account_mngt.jsp">Account Management (create and edit college registrar accounts)</a></li>
                <li class="heading"><a class="heading" href="admin_sched.jsp">View Schedules</a></li>
                <li class="heading"><a class="heading" href="admin_collab.jsp">Chat & Notifications</a></li>
                <li class="heading"><a class="heading" href="index.jsp">Log Out</a></li>
            </ul>
        </div>
        <br /><br />
        <a href="#/setupColregs">Existing College Registrars</a>
        <a href="#/addColregs">Add new College Registrar</a>
        <div ng-view=""></div>
    </body>
</html>
