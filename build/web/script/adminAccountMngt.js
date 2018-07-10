/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('AdminAccountMngt', ['ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider

                .when('/setupColregs', {
                    templateUrl: 'partials/setupColregs.html',
                    controller: 'AdminAccountMngtCtrl'
                })

                .when('/addColregs', {
                    templateUrl: 'partials/addColregs.html',
                    controller: 'AddColregCtrl'
                })

                .otherwise({
                    redirectTo: '/setupColregs'
                });
    }]);

app.controller('AdminAccountMngtCtrl', function ($scope, $http) {
    $scope.collegeList = [];
    $scope.selectedColreg;
    $scope.selectedCollege = null;
    $scope.name = "";
    $scope.password = "";
    $scope.colregList;
    
    $scope.onColregChange = function(){
        if($scope.selectedColreg!=null){
            $scope.name = $scope.selectedColreg.userName;
            $scope.password = $scope.selectedColreg.password;
            $scope.getColregCollege();
        }
    };
    
    $scope.getColregList = function(userIdToSelect){
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getColregList"}
        }).success(function (data, status, headers, config) {
            $scope.colregList = data;
            console.log("success getting colregs")
            if(userIdToSelect != null){
                for(var i=0; i<$scope.colregList.length; i++){
                    if($scope.colregList[i].userID == userIdToSelect){
                        $scope.selectedColreg = $scope.colregList[i];
                        break;
                    }
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("error getting colregs")
        });  
    };
    
    $scope.getCollegeList = function(){
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getCollegeList"}
        }).success(function (data, status, headers, config) {
            $scope.collegeList = data;
            console.log("success getting colleges")
        }).error(function (data, status, headers, config) {
            console.log("error getting colleges")
        });  
    };
    
    $scope.getColregCollege = function(){
        for(i=0;i<$scope.collegeList.length;i++){
            if($scope.collegeList[i].collegeID == $scope.selectedColreg.collegeID){
                $scope.selectedCollege = $scope.collegeList[i];
            }
        }
    };
    
    $scope.clearChanges = function(){
        $scope.onColregChange();
//        note - should replace with something  more efficient later
    };
    
    $scope.editColreg = function(){
        if ($scope.name != "" && $scope.password != ""){
            $http({
                method: 'POST',
                url: 'accountManagement',
                params: {action: "editColreg", userId: $scope.selectedColreg.userID, name: $scope.name, password: $scope.password}
            }).success(function (data, status, headers, config) {
                alert("success in editing college registrar");
                $scope.getColregList($scope.selectedColreg.userID);
            }).error(function (data, status, headers, config) {
                alert("error in editing college registrar");
            });
        }else{
            alert("Please fill out all the required information.");
        }
    }
    
    $scope.getCollegeList();
    $scope.getColregList();
});

app.controller('AddColregCtrl', function ($scope, $http){
    $scope.collegeList = [];
    $scope.selectedCollege = null;
    $scope.name = "";
    $scope.password = "";
    
    $scope.getCollegeList = function(){
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getCollegeList"}
        }).success(function (data, status, headers, config) {
            $scope.collegeList = data;
            console.log("success getting colleges")
        }).error(function (data, status, headers, config) {
            console.log("error getting colleges")
        });  
    };
    
    $scope.clear = function(){
        $scope.selectedCollege = null;
        $scope.name = "";
        $scope.password = "";
    };
    
    $scope.addColreg = function(){
        if ($scope.selectedCollege != null && $scope.name != "" && $scope.password != ""){
            $http({
                method: 'POST',
                url: 'accountManagement',
                params: {action: "addColreg", collegeId: $scope.selectedCollege.collegeID, name: $scope.name, password: $scope.password}
            }).success(function (data, status, headers, config) {
                $scope.clear();
                console.log("success in adding colreg");
            }).error(function (data, status, headers, config) {
                console.log("error in adding colreg");
            });
        }else{
            alert("Please fill out all the required information.");
        }
    }
    
    $scope.getCollegeList();
});