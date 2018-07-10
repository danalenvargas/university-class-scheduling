<%-- 
    Document   : ivhtest
    Created on : 01 26, 17, 4:56:32 AM
    Author     : Dan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>JS Bin</title>
  <%-- 
  <script src="script/angular.js" type="text/javascript"></script>
    <script src="script/angular-route.js" type="text/javascript"></script>
    <script src="script/angular-animate.js" type="text/javascript"></script>
    <script src="script/ivh-treeview.js" type="text/javascript"></script>
    <link href="css/ivh-treeview.css" rel="stylesheet" type="text/css"/>
    <script src="script/colregCollab.js" type="text/javascript"></script>
    --%>
    
    <script src="script/angular.js" type="text/javascript"></script>
    <script src="script/angular-route.js" type="text/javascript"></script>
    <script src="script/angular-animate.js" type="text/javascript"></script>
    <link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet" type="text/css"/>
    <script src="script/ui-bootstrap-tpls-1.1.2.js" type="text/javascript"></script>
    <script src="script/ivh-treeview.js" type="text/javascript"></script>
    <link href="css/ivh-treeview.css" rel="stylesheet" type="text/css"/>
    <script src="script/colregCollab.js" type="text/javascript"></script>
</head>
<body ng-app="colregCollab">
  
  <div ng-controller="collabCtrl as demo">
    <h3>Stuff</h3>
    <div ivh-treeview="demo.stuff">
    </div>
  </div>
    
</body>
</html>
