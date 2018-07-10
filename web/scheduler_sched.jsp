
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>JSP Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <script src="script/angular.js" type="text/javascript"></script>
        <script src="script/angular-route.js" type="text/javascript"></script>
        <script src="script/angular-animate.js" type="text/javascript"></script>
        <script src="script/classScheduler.js" type="text/javascript"></script>
        <link href="css/classScheduler.css" rel="stylesheet" type="text/css"/>
        <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
        <style>

            table {
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
                top:19px;
                font-family: "Open Sans Condensed", sans-serif;
            }

            body{
                margin: 5px;
                /*background-color: #f2f2f2;*/
            }
        </style>
    </head>
    <body ng-app="main" ng-controller="SchedCtrl">
        <div class="heading">
            <h1>Plot Schedules</h1>
        </div>
        <div class="heading" style="background-color:transparent; height:50px; left:8px; top:95px; padding:0px;">
            <ul>
                <li><a href="scheduler_sched.jsp">Plot Schedules</a></li>
                <!--<li><a href="scheduler_validate.jsp">Validation</a></li>-->
                <li><a href="scheduler_collab.jsp">Chat & Notifications</a></li>
                <li><a href="index.jsp">Log Out</a></li>
            </ul>
        </div>
        <br /><br />
        <label>View:</label>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<select name="ddlView" ng-model="selectedView" ng-change="changeView(selectedView)">
            <option ng-repeat="item in views" value="{{item}}">{{item}}</option>
        </select>
        <br />
        <label>Selection:</label> <select name="ddlSelect" ng-model="selectedItem" ng-options="item as item.name group by item.groupName for item in selectionList" ng-change="getSched()">
        </select>
        <br /><br />
        <button id="btnSubmitAll" ng-click="submitAll()">Submit All</button>
        <button id="btnUnsubmitAll" ng-click="unSubmitAll()">Un-submit All</button>

        <div ng-view=""></div>
        <script type="text/ng-template" id="sectionsTable.html">
            <h2> Section Sched </h2>
            <div style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            <div ng-repeat="(timeblockIndx, timeblock) in schedule.timeblocks" style="box-sizing: border-box; border:1px solid black;height:{{schedule.heightPerCell + 'px'}}" 
            ng-class="{'lastCell':timeblockIndx === 24}">
            {{timeblock}}
            </div>
            </div>
            <div ng-repeat="(colIndx,column) in schedule.contents" style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            <div ng-repeat="(rowIndx,row) in column" style="box-sizing: border-box;border:1px solid black;background-color:{{row.bgColor}};height:{{row.height + 'px'}}" 
            ng-click="openSchedModal(row)" ng-class="{'lastCell':rowIndx === column.length - 1}">
            {{row.schedEntry.subject.code}} {{row.schedEntry.subject.type}}
            {{"   " + row.schedEntry.room.name}}<br />
            {{row.schedEntry.instructor.name}}
            </div>
            </div>
            <br />
            <h2> Subjects: </h2>
            <table>
                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Title</th>
                        <th>Type</th>
                        <th>Units</th>
                        <th>Hours</th>
                        <th>Given</th>
                    </tr>
                </thead>
                <tr ng-repeat="subject in subjectList">
                    <td>{{subject.code}}</td>
                    <td>{{subject.title}}</td>
                    <td>{{subject.type}}</td>
                    <td>{{subject.units}}</td>
                    <td>{{subject.hours}}</td>
                    <td>{{subject.hoursPlotted}}</td>
                </tr>
            </table>
        </script>
        <script type="text/ng-template" id="instructorsTable.html">
            <h2> Instructor sched </h2>
            <div style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            <div ng-repeat="(timeblockIndx, timeblock) in schedule.timeblocks" style="box-sizing: border-box; border:1px solid black;height:{{schedule.heightPerCell + 'px'}}" 
            ng-class="{'lastCell':timeblockIndx === 24}">
            {{timeblock}}
            </div>
            </div>
            <div ng-repeat="(colIndx,column) in schedule.contents" style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            <div ng-repeat="(rowIndx,row) in column" style="box-sizing: border-box;border:1px solid black;background-color:{{row.bgColor}};height:{{row.height + 'px'}}" 
            ng-click="" ng-class="{'lastCell':rowIndx === column.length - 1}">
            {{row.schedEntry.subject.code}}
            {{"   " + row.schedEntry.room.name}}<br />
            {{row.schedEntry.section.name}}
            </div>
            </div>
        </script>
        <script type="text/ng-template" id="roomsTable.html">
            <h2> Room Sched </h2>
            <div style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            <div ng-repeat="(timeblockIndx, timeblock) in schedule.timeblocks" style="box-sizing: border-box; border:1px solid black;height:{{schedule.heightPerCell + 'px'}}" 
            ng-class="{'lastCell':timeblockIndx === 24}">
            {{timeblock}}
            </div>
            </div>
            <div ng-repeat="(colIndx,column) in schedule.contents" style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            <div ng-repeat="(rowIndx,row) in column" style="box-sizing: border-box;border:1px solid black;background-color:{{row.bgColor}};height:{{row.height + 'px'}}" 
            ng-click="" ng-class="{'lastCell':rowIndx === column.length - 1}">
            {{row.schedEntry.subject.code}}
            {{"   " + row.schedEntry.section.name}}<br />
            {{row.schedEntry.instructor.name}}
            </div>
            </div>
        </script>
    </body>
</html>
