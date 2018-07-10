
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        
        <script src="script/angular.js" type="text/javascript"></script>
        <script src="script/angular-route.js" type="text/javascript"></script>
        <script src="script/angular-animate.js" type="text/javascript"></script>
        <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
        <script src="script/ivh-treeview.js" type="text/javascript"></script>
        <link href="css/ivh-treeview.css" rel="stylesheet" type="text/css"/>
        <script src="script/colregAccountMngt.js" type="text/javascript"></script>

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

<!--        <style>		
            input[type=text], select {
                width: 100%;
                padding: 12px 20px;
                margin: 8px 0;
                display: inline-block;
                border: 1px solid #ccc;
                border-radius: 4px;
                box-sizing: border-box;
            }
            input[type=password], select {
                width: 100%;
                padding: 12px 20px;
                margin: 8px 0;
                display: inline-block;
                border: 1px solid #ccc;
                border-radius: 4px;
                box-sizing: border-box;
            }
            input[type=submit] {
                width: 100%;
                background-color: #4CAF50;
                color: white;
                padding: 14px 20px;
                margin: 8px 0;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            input[type=submit]:hover {
                background-color: #45a049;

            }

            input[type=reset] {
                width: 100%;
                background-color: #4CAF50;
                color: white;
                padding: 14px 20px;
                margin: 8px 0;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            input[type=reset]:hover {
                background-color: #45a049;
            }
        </style>-->
        <style>
            div.modalColumn {width: 25%; float: left;}
            div.modalColumn div.modalColumn {border: 1px solid lightskyblue; width: 100%;}
        </style>
    </head>
    <body div ng-app="colregAccountMngt" ng-controller="AccountMngtCtrl">
        <div class="heading">
            <h1 class="heading">Create and edit scheduler accounts</h1>
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
        <br/>
        <br/>
        <a href="#/setupSchedulers">Existing Schedulers</a>
        <a href="#/addSchedulers">Add new Scheduler</a>
        <div ng-view=""></div>
    </body>
</html>
