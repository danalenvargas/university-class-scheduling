
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>

        <script src="script/jquery-3.2.0.min.js"></script>
        <script src="script/angular.js" type="text/javascript"></script>
        <script src="script/angular-route.js" type="text/javascript"></script>
        <script src="script/angular-animate.js" type="text/javascript"></script>
        <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
        <script src="script/ivh-treeview.js" type="text/javascript"></script>
        <link href="css/ivh-treeview.css" rel="stylesheet" type="text/css"/>
        <script src="script/colregCollab.js" type="text/javascript"></script>
        <link href="css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
        <script src="script/jquery.nicescroll.min.js"  type="text/javascript"></script>
        <link href="css/collab-chat.css" rel="stylesheet" type="text/css"/>
        <!--<link href="css/w3.css" rel="stylesheet" type="text/css"/>-->
        <script src="script/collab-chat.js"  type="text/javascript"></script>

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
        <style>
            div.modalColumnMain {width: 80%; float: left;}
            div.modalColumnSecondary {width: 20%; float: left;}
            div.modalColumnSecondary div.modalColumn {border: 2px solid lightskyblue; width: 100%;}
        </style>
    </head>
    <body div ng-app="colregCollab" ng-controller="collabCtrl">
        <div class="heading">
            <h1 class="heading">Chat & Notifications & Schedule Sharing</h1>
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
        <uib-tabset active="active">
            <uib-tab index="0" heading="Chat">
                <h3>Chat</h3>
                <div class="w3-container"> 
                <div class="content container-fluid bootstrap snippets">
                    <div class="row row-broken">
                      <div class="col-sm-2 col-xs-12">
                        <div class="col-inside-lg decor-default chat" style="overflow: hidden; outline: none;" tabindex="5000">
                        Search: <select name="ddlChat" ng-model="selectedDropdownChat" ng-change="onSelectedDropdownChatChange(selectedDropdownChat)" ng-options="chat as chat.name for chat in chatListForDropdown">
                        </select>  <br/>
                        <button ng-click="openCreateGroupChatModal()">New Group Chat</button>
                          <div class="chat-users"><br/>
                            <h6>Conversations</h6>
                            <ul style="padding-left:0;">
                                <li style="list-style: none; padding-left:0;" ng-repeat="chat in chatSessionList | orderBy:'lastEdit':true" ng-click="onSelectedChatChange(chat)">
                                    <div class="user">
                                        <div class="avatar">
                                        <img src="img/{{chat.type}}.png" alt="User name">
                                        <!--<div class="status off"></div>-->
                                        </div>
                                        <div class="name">{{chat.name}}</div>
                                        <div class="mood">{{chat.subtitle}}</div>
                                    </div>
                                </li>
                            </ul>
                          </div>
                        </div>
                      </div>
                      <div class="col-sm-8 col-xs-12 chat" style="overflow: hidden; outline: none;" tabindex="5001">
                        <div class="col-inside-lg decor-default">
                          <div class="chat-body">
                            <h6>Chat started</h6>
                            <div ng-repeat="message in messages | orderBy:'entryDate'">
                                <div class="answer {{message.position}}">
                                    <div class="avatar">
                                      <img src="img/individual.png" alt="User name">
                                      <!--<div class="status offline"></div>-->
                                    </div>
                                    <div class="name">{{message.senderName}}</div>
                                    <div class="text">
                                      {{message.text}}
                                    </div>
                                    <div class="time">{{message.entryDate}}</div>
                                </div>
                            </div>
                            <div class="answer-add">
                              <input type="text" name="txtInput" ng-model="data.txtMessage">
                              <!--<span class="answer-btn answer-btn-1"></span>-->
                              <span class="answer-btn answer-btn-2" ng-click="sendMessage(data.txtMessage)"></span>
                              <!--<button ng-click="sendMessage(data.txtMessage)">SEND</button>-->
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="col-sm-2 col-xs-12">
                          <div class="col-inside-lg decor-default chat" style="overflow: hidden; outline: none;" tabindex="5005">
                              <h3>Chat with</h3>
                              <button ng-click="openInviteModal()" ng-show="selectedChat != null && selectedChat.type == 'group'">Invite</button></br>
                              <button ng-click="leaveGroupChat()" ng-show="selectedChat != null && selectedChat.type == 'group'">Leave</button>
                              <div class="chat-users"><br/>
                                <ul style="padding-left:0;">
                                    <li style="list-style: none; padding-left:0;" ng-repeat="member in members">
                                        <div class="user">
                                            <div class="avatar">
                                            <img src="img/individual.png" alt="User name">
                                            <!--<div class="status off"></div>-->
                                            </div>
                                            <div class="name">{{member.name}}</div>
                                            <div class="mood">{{member.subtitle}}</div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                          </div>
                      </div>
                    </div>
                  </div>
                  </div>
            </uib-tab>
            <uib-tab index="1" heading="Sharing">
                <div class="modalColumnMain">
                    <uib-tabset active="active">
                        <uib-tab index="0" heading="Shared Sections">
                            <br />
                            <button ng-click="openShareSectionModal()">Share sections</button>
                            <h3>Shared Sections</h3>
                            <button ng-click="openPullSectionModal()">Pull sections</button>
                            <div ivh-treeview="sharedSectionsList">
                            </div>
                        </uib-tab>
                        <uib-tab index="1" heading="Borrowed Sections">
                            <h3>Borrowed Sections</h3>
                            <button ng-click="openReturnSectionModal()">Return sections</button>
                            <div ivh-treeview="borrowedSectionsList">
                            </div>
                        </uib-tab>
                        <uib-tab index="2" heading="Shared Rooms">
                            <br />
                            <button ng-click="openShareRoomModal()">Share rooms</button>
                            <h3>Shared Rooms</h3>
                            <button ng-click="openPullRoomModal()">Pull rooms</button>
                            <div ivh-treeview="sharedRoomsList">
                            </div>
                        </uib-tab>
                        <uib-tab index="3" heading="Borrowed Rooms">
                            <h3>Borrowed Rooms</h3>
                            <button ng-click="openReturnRoomModal()">Return rooms</button>
                            <div ivh-treeview="borrowedRoomsList">
                            </div>
                        </uib-tab>
                    </uib-tabset>
                </div>
                <div class="modalColumnSecondary">
                    <div class="modalColumn">
                        <h3>Notifications</h3>
                        from last <select ng-model="data.numOfDays" ng-options="n for n in [] | range:1:100" ng-change="getNotifications()"></select> days
                        <ul>
                            <li ng-repeat="notification in notificationsList | orderBy:'timestamp':true" ng-click="openNotificationModal(notification)">{{notification.displayString}}</li>
                        </ul>
                    </div>
                </div>
            </uib-tab>
        </uib-tabset>


        <!--    ===============    MODALS    ===============    -->

        <script type="text/ng-template" id="shareSectionModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Share Sections</h3>
            </div>
            <div class="modal-body">
            User: <select name="ddlUser" ng-model="selectedUser" ng-change="updateSectionTree(selectedUser)" ng-options="user as user.name for user in usersList">
            </select>
            <div ivh-treeview="shareableSectionsList">
            </div>
            <p>Do you want to share your sections with this user?</p>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Share</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>

        <script type="text/ng-template" id="pullSectionModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Pull Sections</h3>
            </div>
            <div class="modal-body">
            <p>Do you want to pull the shared access for all the selected sections?</p>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Pull</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>

        <script type="text/ng-template" id="returnSectionModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Return Sections</h3>
            </div>
            <div class="modal-body">
            <p>Do you want to return the borrowed access for all the selected sections?</p>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Return</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>

        <script type="text/ng-template" id="shareRoomModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Share Rooms</h3>
            </div>
            <div class="modal-body">
            User: <select name="ddlUser" ng-model="selectedUser" ng-change="updateRoomTree(selectedUser)" ng-options="user as user.name for user in usersList">
            </select>
            <div ivh-treeview="shareableRoomsList">
            </div>
            <p>Do you want to share your rooms with this user?</p>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Share</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>

        <script type="text/ng-template" id="pullRoomModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Pull Rooms</h3>
            </div>
            <div class="modal-body">
            <p>Do you want to pull the shared access for all the selected rooms?</p>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Pull</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>

        <script type="text/ng-template" id="returnRoomModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Return Rooms</h3>
            </div>
            <div class="modal-body">
            <p>Do you want to return the borrowed access for all the selected rooms?</p>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Return</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>

        <script type="text/ng-template" id="notificationModal.html">
            <div class="modal-header">
                <h3 class="modal-title">Notification</h3>
            </div>
            <div class="modal-body">
                {{notification.displayString}}:

                <div ivh-treeview="affectedItems" ivh-treeview-use-checkboxes="false" ivh-treeview-expand-to-depth="-1">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-primary" id="btnReturn" type="button" ng-click="ok()">Ok</button>
            </div>
        </script>
        
        <script type="text/ng-template" id="createGroupChatModal.html">
            <div class="modal-header">
            <h3 class="modal-title">Create New Group Chat</h3>
            </div>
            <div class="modal-body">
            <br/>
            Name: <input type="text" ng-model="name">
            <br/>
            </div>
            <div class="modal-footer">
            <button class="btn btn-primary" type="button" ng-click="ok()">Create</button>
            <button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>
            </div>
        </script>
    </body>
</html>
