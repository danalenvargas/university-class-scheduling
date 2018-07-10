
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>JSP Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <!--<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>-->
        <script src="script/angular.js" type="text/javascript"></script>
        <script src="script/angular-route.js" type="text/javascript"></script>
        <script src="script/angular-animate.js" type="text/javascript"></script>
        <script src="script/classScheduler.js" type="text/javascript"></script>
        <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
        <link href="css/classScheduler.css" rel="stylesheet" type="text/css"/>
        <style>
            /*            table, th , td {
                            border: 1px solid grey;
                            border-collapse: collapse;
                            padding: 5px;
                        }
            
                        table tr:nth-child(odd) {
                            background-color: #f2f2f2;
                        }
            
                        table tr:nth-child(even) {
                            background-color: #ffffff;
                        }*/

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
                margin-bottom: 5px;
                border-radius: 5px;
                background-color: #45a049;
                padding-top: 80px;
                padding-left: 30px;
                height: 10px;
           
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
            div.tab {
                overflow: hidden;
                border: 1px solid #ccc;
                background-color: #333;;
            }


    div.btn {
        background-color: inherit;
        float: left;
        border: none;
        outline: none;
        cursor: pointer;
        padding: 14px 16px;
        transition: 0.3s;
    }


        div.tab a:hover {
            background-color: #ddd;
	}

	div.tab a.active {
		background-color: #ccc;
                color: steelblue;
        }

        div.tab{
                color: #4CAF50;
        }

        .tabcontent {
            display: none;
            padding: 6px 12px;
            border: 1px solid #ccc;
            border-top: none;
	
        }
        a:link{
            color:greenyellow;
            text-decoration:none;
        }
        a:visited {color:greenyellow;}
</style>
    </head>
    <body ng-app="main" ng-controller="SchedCtrl">
        <div class="heading">
            <h1>Plot Schedules</h1>
        </div>
	<div class="tab">
            <a class="btn" href="colreg_account_mngt.jsp">Account Management <br>(create and edit scheduler accounts)</a>
            <a class="btn"  href="colreg_collab.jsp">Chat & Notifications & <br> Schedule Sharing</a>
            <a class="btn active"  href="colreg_sched.jsp">Scheduling <br> (plot schedules)</a>
            <a class="btn"  href="colreg_setup.jsp">Setup (create edit delete info)<br><br></a>
            <a class="btn" style = "float: right;"  href="index.jsp">Log Out<br><br></a>
	</div>
        <br /><br />
        <label>View:</label>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<select name="ddlView" ng-model="selectedView" ng-change="changeView(selectedView)">
            <option ng-repeat="item in views" value="{{item}}">{{item}}</option>
        </select>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
        <label>Scope:</label>&nbsp&nbsp<select name="ddlViewScope" ng-model="selectedViewScope" ng-change="changeView()">
            <option value="assigned">Assigned</option>
            <option value="all">All</option>
        </select>
        <br />
        <label>Selection:</label> <select name="ddlSelect" ng-model="selectedItem" ng-options="item as item.name group by item.groupName for item in selectionList" options-class="{ 'is-assigned' : isAssigned, 'not-assigned': !isAssigned }" ng-change="getSched()">
        </select>
        <br /><br />
        <button id="btnReturnAll" ng-click="returnAll()" ng-disabled="!selectedItem.isAssigned">Return All</button>
        <button id="btnLockAll" ng-click="lockAll()" ng-disabled="!selectedItem.isAssigned">Lock All</button>
        <button id="btnApproveAll" ng-click="approveAll()" ng-disabled="!selectedItem.isAssigned">Approve All</button>

        <div ng-view=""></div>
        <script type="text/ng-template" id="sectionsTable.html">
            <h2> Section Sched </h2>
            <div style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            Time
            <div ng-repeat="(timeblockIndx, timeblock) in schedule.timeblocks" style="box-sizing: border-box; border:1px solid black;height:{{schedule.heightPerCell + 'px'}}" 
            ng-class="{'lastCell':timeblockIndx === 24}">
            {{timeblock}}
            </div>
            </div>
            <div ng-repeat="(colIndx,column) in schedule.contents" style="box-sizing: border-box; display: inline-block; position: relative; height:{{schedule.height + 'px'}};width:{{schedule.width + 'px'}}">
            {{dayList[colIndx].text}}
            <div ng-repeat="(rowIndx,row) in column" style="box-sizing: border-box;border:1px solid black;background-color:{{row.bgColor}};height:{{row.height + 'px'}}" 
            ng-click="openSchedModal(row)" ng-class="{'lastCell':rowIndx === column.length - 1}">
            <span ng-if="row.span > 1">{{row.schedEntry.subject.code}} {{row.schedEntry.subject.type}}
            {{"   " + row.schedEntry.room.name}}
            <br />{{row.schedEntry.instructor.name}}</span>
            <span ng-if="row.span <= 1 && row.schedEntry!=null" uib-popover="{{row.schedEntry.subject.code}}-{{row.schedEntry.subject.type}},  room: {{row.schedEntry.room.name}}, instructor: {{row.schedEntry.instructor.name}}" popover-trigger="mouseenter">{{row.schedEntry.subject.code}} {{row.schedEntry.subject.type}}
            {{"   " + row.schedEntry.room.name}} ... <br/></span>
            <span ng-if="row.span <= 1 && row.schedEntry==null"><br/></span>
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
        <!--<button class="btn btn-danger" type="button" ng-click="debugGetCollection()">debug getCollection</button>-->

    </body>
</html>

