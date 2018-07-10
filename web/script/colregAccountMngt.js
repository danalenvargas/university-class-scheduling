/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('colregAccountMngt', ['ivh.treeview', 'ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider

                .when('/setupSchedulers', {
                    templateUrl: 'partials/setupSchedulers.html',
                    controller: 'AccountMngtCtrl'
                })

                .when('/addSchedulers', {
                    templateUrl: 'partials/addSchedulers.html',
                    controller: 'AddSchedulerCtrl'
                })

                .otherwise({
                    redirectTo: '/setupSchedulers'
                });
    }]);

app.controller('AccountMngtCtrl', function ($scope, $http, ivhTreeviewBfs, ivhTreeviewMgr) {
    $scope.departmentList = [];
    $scope.selectedScheduler;
    $scope.selectedDepartment = null;
    $scope.name = "";
    $scope.password = "";
    $scope.canEditTime = false;
    $scope.schedulerList;
    $scope.mySectionsList;
    $scope.mySubjectsList;
    $scope.myRoomsList;
    $scope.myInstructorsList;
    
    $scope.onSchedulerChange = function(){
        $scope.name = $scope.selectedScheduler.userName;
        $scope.password = $scope.selectedScheduler.password;
        $scope.getCanEditTime();
        $scope.getSchedulerDepartment();
        $scope.getSchedulerSections();
        $scope.getSchedulerRooms();
        $scope.getSchedulerSubjects();
        $scope.getSchedulerInstructors();
    };
    
    $scope.getSchedulerList = function(userIdToSelect){
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getSchedulerList"}
        }).success(function (data, status, headers, config) {
            $scope.schedulerList = data;
            console.log("success getting schedulers")
            if(userIdToSelect != null){
                for(var i=0; i<$scope.schedulerList.length; i++){
                    if($scope.schedulerList[i].userID == userIdToSelect){
                        $scope.selectedScheduler = $scope.schedulerList[i];
                        break;
                    }
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("error getting schedulers")
        });  
    };
    
    $scope.getSchedulerDepartment = function(){
        $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getDepartmentList"}
        }).success(function (data, status, headers, config) {
            $scope.departmentList = data;
            console.log("success getting departments:")
            for(i=0;i<$scope.departmentList.length;i++){
                if($scope.departmentList[i].departmentID == $scope.selectedScheduler.departmentID){
                    $scope.selectedDepartment = $scope.departmentList[i];
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("error getting departments")
        });  
    };
    
    $scope.getSchedulerSections = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMySections"}
        }).success(function (data, status, headers, config) {
            $scope.mySectionsList = data;
            console.log("got My Sections");
            $scope.getAssignedSections();
        }).error(function (data, status, headers, config) {
            console.log("error getting mySections");
        });
    };
    
    $scope.getAssignedSections = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getAssignedSections", schedulerId: $scope.selectedScheduler.userID}
        }).success(function (data, status, headers, config) {
            var assignedSections = data;
            $scope.setCheckedNodes($scope.mySectionsList, assignedSections);
            console.log("got Assigned Sections");
        }).error(function (data, status, headers, config) {
            console.log("error getting Assigned Sections");
        });
    };
    
    $scope.getSchedulerRooms = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMyRooms"}
        }).success(function (data, status, headers, config) {
            $scope.myRoomsList = data;
            console.log("success getting myRooms");
            $scope.getAssignedRooms();
        }).error(function (data, status, headers, config) {
            console.log("error getting myRooms");
        });
    };
    
    $scope.getAssignedRooms = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getAssignedRooms", schedulerId: $scope.selectedScheduler.userID}
        }).success(function (data, status, headers, config) {
            var assignedRooms = data;
            $scope.setCheckedNodes($scope.myRoomsList, assignedRooms);
            console.log("got Assigned Rooms");
        }).error(function (data, status, headers, config) {
            console.log("error getting Assigned Rooms");
        });
    };
    
    $scope.getSchedulerSubjects = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMySubjects"}
        }).success(function (data, status, headers, config) {
            $scope.mySubjectsList = data;
            console.log("success getting mySubjects");
            $scope.getAssignedSubjects();
        }).error(function (data, status, headers, config) {
            console.log("error getting mySubjects");
        });
    };
    
    $scope.getAssignedSubjects = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getAssignedSubjects", schedulerId: $scope.selectedScheduler.userID}
        }).success(function (data, status, headers, config) {
            var assignedSubjects = data;
            $scope.setCheckedNodes($scope.mySubjectsList, assignedSubjects);
            console.log("got Assigned Subjects");
        }).error(function (data, status, headers, config) {
            console.log("error getting Assigned Subjects");
        });
    };
    
    $scope.getSchedulerInstructors = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMyInstructors"}
        }).success(function (data, status, headers, config) {
            $scope.myInstructorsList = data;
            console.log("success getting MyInstructors");
            $scope.getAssignedInstructors();
        }).error(function (data, status, headers, config) {
            console.log("error getting MyInstructors");
        });
    };
    
    $scope.getAssignedInstructors = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getAssignedInstructors", schedulerId: $scope.selectedScheduler.userID}
        }).success(function (data, status, headers, config) {
            var assignedInstructors = data;
            $scope.setCheckedNodes($scope.myInstructorsList, assignedInstructors);
            console.log("got Assigned instructors");
        }).error(function (data, status, headers, config) {
            console.log("error getting Assigned instructors");
        });
    };
    
    $scope.getCanEditTime = function(){
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getCanEditTime", schedulerId: $scope.selectedScheduler.userID}
        }).success(function (data, status, headers, config) {
            $scope.canEditTime = (data == 1) ? true : false;
            console.log("success getitng CanEditTime");
        }).error(function (data, status, headers, config) {
            console.log("error getting CanEditTime");
        });
    };
    
    $scope.clearChanges = function(){
        $scope.onSchedulerChange();
//        note - should replace with something  more efficient later
    };
    
    $scope.editScheduler = function(){
        if ($scope.selectedDepartment != null && $scope.name != "" && $scope.password != ""){
            var selectedSections = $scope.getSelectedBoxes($scope.mySectionsList);
            var selectedSubjects = $scope.getSelectedBoxes($scope.mySubjectsList);
            var selectedRooms = $scope.getSelectedBoxes($scope.myRoomsList);
            var selectedInstructors = $scope.getSelectedBoxes($scope.myInstructorsList);

            $http({
                method: 'POST',
                url: 'accountManagement',
                params: {action: "editScheduler", departmentId: $scope.selectedDepartment.departmentID, userId: $scope.selectedScheduler.userID, name: $scope.name, password: $scope.password, canEditTime: $scope.canEditTime, selectedSections: selectedSections, selectedSubjects: selectedSubjects, selectedInstructors: selectedInstructors, selectedRooms: selectedRooms}
            }).success(function (data, status, headers, config) {
                alert("success in editing scheduler");
                $scope.getSchedulerList($scope.selectedScheduler.userID);
            }).error(function (data, status, headers, config) {
                alert("error in editing scheduler");
            });
        }else{
            alert("Please fill out all the required information.");
        }
    }
    
    $scope.setCheckedNodes = function(tree, assignedList){
        ivhTreeviewBfs(tree, function (node) {
            for(var i=0; i<assignedList.length; i++){
                if (node.value!=null && node.value.id == assignedList[i]) {
                    //node.selected = true;
                    ivhTreeviewMgr.select(tree, node);
                    break;
                }
            }
        });
    }
    
    $scope.getSelectedBoxes = function (tree) {
        var selectedNodes = [];
        ivhTreeviewBfs(tree, function (node) {
            if (node.selected && !node.children) {
                selectedNodes.push(node.value.id);
            }
        });
        return selectedNodes;
    };
    
    $scope.getSchedulerList();
});

app.controller('AddSchedulerCtrl', function ($scope, $http, ivhTreeviewBfs){
    $scope.departmentList = [];
    $scope.selectedDepartment = null;
    $scope.name = "";
    $scope.password = "";
    $scope.canEditTime = false;
    $scope.mySectionsList;
    $scope.mySubjectsList;
    $scope.myRoomsList;
    $scope.myInstructorsList;
    
    $scope.getDepartmentList = function(){
        $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getDepartmentList"}
        }).success(function (data, status, headers, config) {
            $scope.departmentList = data;
            console.log("success getting departments")
        }).error(function (data, status, headers, config) {
            console.log("error getting departments")
        });  
    };
    
    $scope.getMySections = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMySections"}
        }).success(function (data, status, headers, config) {
            $scope.mySectionsList = data;
            console.log("got My Sections:");
            console.log($scope.mySectionsList);
        }).error(function (data, status, headers, config) {
            console.log("error getting mySections");
        });
    };
    
    $scope.getMyRooms = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMyRooms"}
        }).success(function (data, status, headers, config) {
            $scope.myRoomsList = data;
            console.log("success getting myRooms");
        }).error(function (data, status, headers, config) {
            console.log("error getting myRooms");
        });
    };
    
    $scope.getMySubjects = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMySubjects"}
        }).success(function (data, status, headers, config) {
            $scope.mySubjectsList = data;
            console.log("success getting mySubjects");
        }).error(function (data, status, headers, config) {
            console.log("error getting mySubjects");
        });
    };
    
    $scope.getMyInstructors = function () {
        $http({
            method: 'GET',
            url: 'accountManagement',
            params: {action: "getMyInstructors"}
        }).success(function (data, status, headers, config) {
            $scope.myInstructorsList = data;
            console.log("got My myInstructorsList:");
            console.log($scope.myInstructorsList);
            console.log("success getting MyInstructors");
        }).error(function (data, status, headers, config) {
            console.log("error getting MyInstructors");
        });
    };
    
    $scope.clear = function(){
        $scope.canEditTime = false;
        $scope.selectedDepartment = null;
        $scope.name = "";
        $scope.password = "";
        ivhTreeviewBfs($scope.mySectionsList, function (node) {
            node.selected = false;
        });
        ivhTreeviewBfs($scope.mySubjectsList, function (node) {
            node.selected = false;
        });
        
        ivhTreeviewBfs($scope.myRoomsList, function (node) {
            node.selected = false;
        });
        
        ivhTreeviewBfs($scope.myInstructorsList, function (node) {
            node.selected = false;
        });
    };
    
    $scope.addScheduler = function(){
        if ($scope.selectedDepartment != null && $scope.name != "" && $scope.password != ""){
            var selectedSections = $scope.getSelectedBoxes($scope.mySectionsList);
            var selectedSubjects = $scope.getSelectedBoxes($scope.mySubjectsList);
            var selectedRooms = $scope.getSelectedBoxes($scope.myRoomsList);
            var selectedInstructors = $scope.getSelectedBoxes($scope.myInstructorsList);

            $http({
                method: 'POST',
                url: 'accountManagement',
                params: {action: "addScheduler", departmentId: $scope.selectedDepartment.departmentID, name: $scope.name, password: $scope.password, canEditTime: $scope.canEditTime, selectedSections: selectedSections, selectedSubjects: selectedSubjects, selectedInstructors: selectedInstructors, selectedRooms: selectedRooms}
            }).success(function (data, status, headers, config) {
                $scope.clear();
                console.log("success in adding scheduler");
            }).error(function (data, status, headers, config) {
                console.log("error in adding scheduler");
            });
        }else{
            alert("Please fill out all the required information.");
        }
    }
    
    $scope.getSelectedBoxes = function (tree) {
        var selectedNodes = [];
        ivhTreeviewBfs(tree, function (node) {
            if (node.selected && !node.children) {
                selectedNodes.push(node.value.id);
            }
        });
        return selectedNodes;
    };
    
    $scope.getDepartmentList();
    $scope.getMySections();
    $scope.getMyRooms();
    $scope.getMySubjects();
    $scope.getMyInstructors();
});