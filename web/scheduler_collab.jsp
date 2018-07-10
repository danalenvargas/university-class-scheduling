
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>JSP Page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

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
    </head>
    <body div ng-app="colregCollab" ng-controller="collabCtrl">
        <div class="heading">
            <h1 class="heading">Collaboration</h1>
        </div>
        <div class="heading" style="background-color:transparent; height:50px; left:8px; top:95px; padding:0px;">
            <ul class="heading"ail>
                <li class="heading"><a class="heading" href="scheduler_sched.jsp">Plot Schedules</a></li>
                <!--<li><a href="scheduler_validate.jsp">Validation</a></li>-->
                <li class="heading"><a class="heading" href="scheduler_collab.jsp">Chat & Notifications</a></li>
                <li class="heading"><a class="heading" href="index.jsp">Log Out</a></li>
            </ul>
        </div>
        <br/>
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
    </body>
</html>
