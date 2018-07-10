/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('main', ['ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider

                .when('/section', {
                    templateUrl: 'sectionsTable.html',
//                    controller: 'SchedCtrl'
                })

                .when('/instructor', {
                    templateUrl: 'instructorsTable.html',
//                    controller: 'SchedCtrl'
                })

                .when('/room', {
                    templateUrl: 'roomsTable.html',
//                    controller: 'SchedCtrl'
                })

                .otherwise({
                    redirectTo: '/section'
                });
    }]);

app.factory('schedFactory', function () {
    var factory = {};
    var schedule = {
        height: '500',
        width: '200',
        rows: 25,
        cols: 5,
        contents: [],
        heightPerCell: Math.floor(500 / 25),
        timeblocks: ['7:00 - 7:30', '7:30 - 8:00', '8:00 - 8:30', '8:30 - 9:00', '9:00 - 9:30', '9:30 - 10:00', '10:00 - 10:30', '10:30 - 11:00', '11:00 - 11:30', '11:30 - 12:00', '12:00 - 12:30', '12:30 - 1:00', '1:00 - 1:30', '1:30 - 2:00', '2:00 - 2:30', '2:30 - 3:00', '3:00 - 3:30', '3:30 - 4:00', '4:00 - 4:30', '4:30 - 5:00', '5:00 - 5:30', '5:30 - 6:00', '6:00 - 6:30', '6:30 - 7:00', 'filler']
    };
    factory.getSchedule = function () {
        return schedule;
    };
    return factory;
});

app.controller('SchedCtrl', function ($scope, $http, $location, $uibModal, schedFactory, $interval, $sce) {
    $scope.schedule = schedFactory.getSchedule();
    $scope.views = ["section", "room", "instructor"];
    $scope.selectedView = "section";
    $scope.selectedViewScope = "assigned";
    $scope.selectionList = [];
    $scope.currentUser;
    $scope.changeCounter = 0;
    var toCheck = true;
    var schedInterval;
    $scope.commentList;
    
    
    $scope.dayList = [
        {id: 0, text: 'Monday'},
        {id: 1, text: 'Tuesday'},
        {id: 2, text: 'Wednesday'},
        {id: 3, text: 'Thursday'},
        {id: 4, text: 'Friday'}
    ];
    
    var initialize = function(){
        $scope.getCurrentUser();
        $scope.onViewChange($scope.selectedView);
        getSchedTable();
    };

    $scope.update = function () {
        var counter = 0;
        var schedule = [];
        var heightPerCell = Math.floor($scope.schedule.height / $scope.schedule.rows);
        var status;
        var bgColor="White";
        var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
        var isSchedCreatedInMyCollege;
        var isReturnRequested;
        
//        if($scope.selectedItem.isAssigned)
        if ($scope.schedList!=null && $scope.schedList[counter] != null) {
            var day = Math.floor($scope.schedList[counter].start / 24);
            var timeStart = $scope.schedList[counter].start % 24;
            var timeEnd = ($scope.schedList[counter].end % 24 == 0) ? 24 : $scope.schedList[counter].end % 24;
            for (var i = 0; i < $scope.schedule.cols; i++) {
                var a = [];
                for (var j = 0; j < $scope.schedule.rows; j++) {
                    if (day === i && timeStart === j) {
                        status = $scope.schedList[counter].status;
                        console.log("inside UPDATE");
//                        console.log($scope.currentUser);
//                        console.log($scope.schedList[counter].creator);
                        isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == $scope.schedList[counter].creator.collegeID);
                        isReturnRequested = $scope.schedList[counter].isReturnRequested;
                        console.log("isReturnRequested: " + isReturnRequested);
//                        console.log("determining color: " + $scope.currentUser.userType + " " + $scope.schedList[counter].creator.userID + " " + $scope.currentUser.userID + " " + isSchedCreatedInMyCollege);
                        if (($scope.currentUser.userType == "SCHEDULER" && $scope.schedList[counter].creator.userID != $scope.currentUser.userID) || (!isSchedCreatedInMyCollege && !isSelectedItemMine) || (!isSchedCreatedInMyCollege && $scope.schedList[counter].status !== "locked" && $scope.schedList[counter].status !== "approved")) bgColor="#B7B8B6";
                        else if(status == "unsubmitted") bgColor="Ivory";
                        else if(status == "submitted") bgColor="#89DA59";
                        else if(isReturnRequested) bgColor="#ff0000";
                        else if(status == "locked") bgColor="#F4CC70";
                        else if(status == "approved") bgColor="#87CEEB";
                        a.push({
                            schedEntry: $scope.schedList[counter],
                            index: (i * 24) + j,
                            span: timeEnd - timeStart,
                            height: heightPerCell * (timeEnd - timeStart),
                            bgColor: bgColor
                        });
                        counter++;
                        j = timeEnd - 1;
                        if ($scope.schedList[counter] != null) {
                            day = Math.floor($scope.schedList[counter].start / 24);
                            timeStart = $scope.schedList[counter].start % 24;
                            timeEnd = ($scope.schedList[counter].end % 24 == 0) ? 24 : $scope.schedList[counter].end % 24;
                        }

                    } else {
                        a.push({
                            schedEntry: null,
                            index: (i * 24) + j,
                            height: heightPerCell,
                            span: 1,
                            bgColor: "White"
                        });
                    }
                }
                schedule.push(a);
            }
        } else {
            for (var i = 0; i < $scope.schedule.cols; i++) {
                var a = [];
                for (var j = 0; j < $scope.schedule.rows; j++) {
                    a.push({
                        schedEntry: null,
                        index: (i * 24) + j,
                        height: heightPerCell,
                        span: 1,
                        bgColor: "White"
                    });
                }
                schedule.push(a);
            }
        }
        $scope.schedule.contents = schedule;
        console.log("AFTER UPDATE: ");
        console.log($scope.schedule.contents);
        return $scope.schedule.contents;
    };

    $scope.onViewChange = function () {
        console.log("onViewChange");
        console.log($scope.selectedView);
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "changeView", type: $scope.selectedView, scope: $scope.selectedViewScope}
        }).success(function (data, status, headers, config) {
            $scope.selectionList = data;
            console.log("CHANGED SELECTION");
            console.log($scope.selectionList);
        }).error(function (data, status, headers, config) {
        });
    };
    
    $scope.changeView = function () {
        console.log("changeView function");
        $scope.schedule.contents = [];
        if($scope.selectedView == "room" || $scope.selectedView == "instructor") $scope.subjectList = [];
        $scope.onViewChange($scope.selectedView);
    };
    
    var getSchedTable = function () {
        console.log("getSchedTable called");
//        $scope.getSched();
        $interval(function(){
//            if(toCheck) getIfChanged();
            getIfChanged();
        },1000)
    };
    
    var getIfChanged = function(){
       $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "getChangeCounter"}
        }).success(function (data, status, headers, config) {
            var newCounter = data;
            console.log("got new changeCounter: " + newCounter);
            if(newCounter !== $scope.changeCounter){
                $scope.changeCounter = newCounter;
                $scope.getSched();
            }
        }).error(function (data, status, headers, config) {
        }); 
    };
    
    $scope.getSched = function () {
        $location.path($scope.selectedView);
        if($scope.selectedItem != null){
            $http({
                method: 'GET',
                url: 'SchedTable',
                params: {function: "getSched", type: $scope.selectedView, itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
            }).success(function (data, status, headers, config) {
                $scope.schedList = data;
                $scope.update($scope.schedList);
            }).error(function (data, status, headers, config) {
            });
            if ($scope.selectedView == "section") {
                $http({
                    method: 'GET',
                    url: 'SchedTable',
                    params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
                }).success(function (data, status, headers, config) {
                    $scope.subjectList = data;
                }).error(function (data, status, headers, config) {
                });
            } 
        }
    };

//    $scope.getSchedTable = function () {
////        if($scope.selectedItem != null){
//        console.log("changing view: " + $scope.selectedView);
//        $location.path($scope.selectedView);
//        console.log("selected item: " + $scope.selectedItem.id + $scope.selectedItem.departmentId + $scope.selectedItem.collegeId);
//        $http({
//            method: 'GET',
//            url: 'SchedTable',
//            params: {function: "getSched", type: $scope.selectedView, itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
//        }).success(function (data, status, headers, config) {
//            console.log("GETTING SCHED:!: type: " + $scope.selectedView + " item: " + $scope.selectedItem.name + $scope.selectedItem.id + $scope.selectedItem.departmentId + $scope.selectedItem.collegeId);
//            console.log("testing 1");
//            console.log(data);
//            console.log("testing 2");
//            $scope.schedList = data;
//            console.log($scope.schedList[0]);
//            console.log("testing 3");
//            $scope.update($scope.schedList);
//        }).error(function (data, status, headers, config) {
//        });
//        if ($scope.selectedView == "section") {
//            $http({
//                method: 'GET',
//                url: 'SchedTable',
//                params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
//            }).success(function (data, status, headers, config) {
//                //console.log("GETTING SCHED:!: type: " + $scope.selectedView + " item: " + $scope.selectedItem.name + $scope.selectedItem.id + $scope.selectedItem.departmentId + $scope.selectedItem.collegeId);
//                //console.log("testing 1");
//                console.log("//*** SUBJECT LIST: ");
//                console.log(data);
//                $scope.subjectList = data;
//            }).error(function (data, status, headers, config) {
//            });
//        }
////        }
//    };

    $scope.openSchedModal = function (selectedCell) {
        var modal;
        if($scope.currentUser.userType=="COLLEGE_REGISTRAR") modal = "partials/scheduleModal.html";
        else if($scope.currentUser.userType=="SCHEDULER") modal = "partials/scheduleModalForScheduler.html"
        var modalInstance = $uibModal.open({
            templateUrl: modal,
            controller: "schedModalCtrl",
            controllerAs: '$mCtrl',
            size: "lg",
            resolve: {
                data: {cell: selectedCell,
                    subjectList: $scope.subjectList,
                    selectedView: $scope.selectedView,
                    selectedItem: $scope.selectedItem,
                    selectedItemId: $scope.selectedItem.id,
                    currentUser: $scope.currentUser},
                    changeCounter: $scope.changeCounter
            }
        });

        modalInstance.result.then(function (result) {
            console.log("modal result: " + result.action);
            result.section = $scope.selectedItem.id;
            if (result.action == "add") {
                $scope.addSchedule(result);
            } else if (result.action == "delete") {
                $scope.deleteSchedule(result);
            } else{
                $scope.validateSchedule(result);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
//    $scope.openSchedulerValidationModal = function (selectedCell) {
//        if(selectedCell.schedEntry != null){
//            var modalInstance = $uibModal.open({
//                templateUrl: "partials/schedulerValidationModal.html",
//                controller: "schedValidationCtrl",
//                size: "lg",
//                resolve: {
//                    data: {cell: selectedCell}
//                }
//            });
//
//            modalInstance.result.then(function (result) {
//                result.section = $scope.selectedItem.id;
//                if (result.action == "add") {
//                    $scope.addSchedule(result);
//                } else if (result.action == "delete") {
//                    $scope.deleteSchedule(result);
//                }
//            }, function () {
//                console.log('Modal dismissed at: ' + new Date());
//            });
//        }
//    };

    $scope.addSchedule = function (sched) {
        console.log("========= ADDING SECTIONS ========");
        console.log(sched.section);
        console.log(sched.start);
        console.log(sched.end);
        console.log(sched.subject);
        console.log(sched.instructor);
        console.log(sched.room);
        console.log(sched.origSched);
        $http({
            method: 'POST',
            url: 'SchedTable',
            contentType: 'application/json',
            data: JSON.stringify(sched)
        }).success(function (data, status, headers, config) {
            var t0 = performance.now();
//            console.log("--------------- ADDING... ");
//            console.log($scope.schedList);
//            console.log($scope.schedList.length);
//            if (sched.origSched != null) {
//                for (i = 0; i < $scope.schedList.length; i++) {
//                    if ($scope.schedList[i].schedID == sched.origSched.schedID) {
//                        $scope.schedList.splice(i, 1);
//                    }
//                }
//            }
//            for (i = 0; i <= $scope.schedList.length; i++) {
//                if (i == $scope.schedList.length) {
//                    $scope.schedList.push(data);
//                    break;
//                }
//                else if ($scope.schedList[i].start > data.start) {
//                    $scope.schedList.splice(i, 0, data);
//                    break;
//                }
//            }
            console.log("SUCCESSFULLY ADDED! ");
            console.log($scope.schedList);
            if ($scope.selectedView == "section") {
                $http({
                    method: 'GET',
                    url: 'SchedTable',
                    params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
                }).success(function (data, status, headers, config) {
                    console.log("//*** SUBJECT LIST: ");
                    console.log(data);
                    $scope.subjectList = data;
                }).error(function (data, status, headers, config) {
                });
            }
            $scope.update();
            var t1 = performance.now();
            console.log("PROCESS after add-success took " + (t1 - t0) + " milliseconds.")
        }).error(function (data, status, headers, config) {
            console.log("ERROR IN ADDING!");
        });
    };
    
    $scope.deleteSchedule = function(sched){
        $http({
            method: 'POST',
            url: 'SchedTable',
            contentType: 'application/json',
            data: JSON.stringify(sched)
        }).success(function (data, status, headers, config) {
//            console.log("--------------- removing... ");
//            console.log($scope.schedList);
//            console.log($scope.schedList.length);
            if (sched.origSched != null) {
                for (i = 0; i < $scope.schedList.length; i++) {
                    if ($scope.schedList[i].schedID == sched.origSched.schedID) {
                        $scope.schedList.splice(i, 1);
                    }
                }
            }
            console.log("SUCCESSFULLY DELETED! ");
            console.log($scope.schedList);
            if ($scope.selectedView == "section") {
                $http({
                    method: 'GET',
                    url: 'SchedTable',
                    params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
                }).success(function (data, status, headers, config) {
                    console.log("//*** SUBJECT LIST: ");
                    console.log(data);
                    $scope.subjectList = data;
                }).error(function (data, status, headers, config) {
                });
            }
            $scope.update();
        }).error(function (data, status, headers, config) {
            console.log("ERROR IN DELETING!");
        });
    };

    $scope.debugGetCollection = function(){
        $location.path($scope.selectedView);
         $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "debugGetCollection"}
        }).success(function (data, status, headers, config) {
            $scope.debugCollection = data;
            console.log("DEBUG COLLECTION: ");
            console.log(data);
        }).error(function (data, status, headers, config) {
        });
    };
    
//    $scope.getCurrentUserId = function(){
//        $http({
//            method: 'GET',
//            url: 'SchedTable',
//            params: {function: "getCurrentUserId"}
//        }).success(function (data, status, headers, config) {
//            $scope.currentUserId = data;
//        }).error(function (data, status, headers, config) {
//        });
//    };

$scope.submitAll = function(){
        console.log("STARTING SUBMITALL");
        console.log($scope.schedList);
        var tempList = $scope.schedList;
        for(var i=0; i< tempList.length; i++){
            console.log("AT submitAll, comparing: " + i + " " + tempList[i].creator + " " + $scope.currentUser.userID + " " + tempList[i].status + " ");
            console.log(tempList[i]);
            if(tempList[i].creator.userID == $scope.currentUser.userID && tempList[i].status == "unsubmitted"){
                console.log("true, validating");
                $scope.validateSchedule({
                    action: "validate",
                    newStatus: "submitted",
                    schedId: tempList[i].schedID
                });
            }
        }
    };
    
    $scope.unSubmitAll = function(){
        console.log("STARTING UN-SUBMITALL");
        console.log($scope.schedList);
        var tempList = $scope.schedList;
        for(var i=0; i< tempList.length; i++){
            console.log("AT UN-submitAll, comparing: " + i + " " + tempList[i].creator.userID + " " + $scope.currentUser.userID + " " + tempList[i].status + " ");
            console.log(tempList[i]);
            if(tempList[i].creator.userID == $scope.currentUser.userID && tempList[i].status == "submitted"){
                console.log("true, validating");
                $scope.validateSchedule({
                    action: "validate",
                    newStatus: "unsubmitted",
                    schedId: tempList[i].schedID
                });
            }
        }
    };
    
    $scope.approveAll = function(){
        var tempList = $scope.schedList;
        for(var i=0; i< tempList.length; i++){
            var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
            var isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == tempList[i].creator.collegeID);
        
            if(isSelectedItemMine && ((isSchedCreatedInMyCollege && tempList[i].status == "submitted") || tempList[i].status == "locked" )){
                $scope.validateSchedule({
                    action: "validate",
                    newStatus: "approved",
                    schedId: tempList[i].schedID
                });
            }
        }
    };
    
    $scope.lockAll = function(){
        var tempList = $scope.schedList;
        for(var i=0; i< tempList.length; i++){
            var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
            var isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == tempList[i].creator.collegeID);
        
            if((isSelectedItemMine && ((isSchedCreatedInMyCollege && tempList[i].status == "submitted") || tempList[i].status == "approved")) || (!isSelectedItemMine && isSchedCreatedInMyCollege && tempList[i].status == "submitted")){
                $scope.validateSchedule({
                    action: "validate",
                    newStatus: "locked",
                    schedId: tempList[i].schedID
                });
            }
        }
    };
    
    $scope.returnAll = function(){
        var tempList = $scope.schedList;
        var newStatus;
        for(var i=0; i< tempList.length; i++){
            var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
            var isSchedCreatedByMe = ($scope.currentUser.userID == tempList[i].creator.userID);
            var isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == tempList[i].creator.collegeID);
        
            if (tempList[i].creator.userType == "SCHEDULER") newStatus = "unsubmitted";
            else newStatus = "submitted";
            
            if((isSelectedItemMine && ((isSchedCreatedInMyCollege && !isSchedCreatedByMe && tempList[i].status == "submitted") || tempList[i].status == "locked"  || tempList[i].status == "approved" )) || (!isSelectedItemMine && isSchedCreatedInMyCollege && ((!isSchedCreatedByMe && tempList[i].status == "submitted") || tempList[i].status == "locked"))){
                $scope.validateSchedule({
                    action: "validate",
                    newStatus: newStatus,
                    schedId: tempList[i].schedID
                });
            }
        }
    };
    
    $scope.validateSchedule = function (result) {
      $http({
            method: 'POST',
            url: 'SchedTable',
            contentType: 'application/json',
            data: JSON.stringify(result)
        }).success(function (data, status, headers, config) {
//            $scope.getSchedTable();
            console.log("SUCCESS IN VALIDATION")
//            for(var i=0; i < $scope.schedList.length; i++){
//                console.log("comparing... : " + $scope.schedList[i].schedID + " " + result.schedId);
//                if($scope.schedList[i].schedID == result.schedId){
//                    console.log("found! changing to " + result.newStatus);
//                    $scope.schedList[i].status = result.newStatus;
//                }
//            }
//            $scope.update($scope.schedList);
//            toCheck = false;
//            getIfChanged();
        }).error(function (data, status, headers, config) {
            console.log("ERROR IN Validation");
        });  
    };
    
    $scope.getCurrentUser = function(){
        console.log("getting currentUser");
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "getCurrentUser"}
        }).success(function (data, status, headers, config) {
            $scope.currentUser = data;
            console.log("got currentUser");
            console.log(data);
        }).error(function (data, status, headers, config) {
        });
    };
    
    initialize();
//    $scope.selectionListTest = [
//    { name: 'foo', id: 1, groupName: 'CEIT_DIT_BSCS', isAssigned: true },
//    { name: 'bar', id: 2, groupName: 'CEIT_DIT_BSCS', isAssigned: false },
//    { name: 'test', id: 3, groupName: 'CEIT_DIT_BSIT', isAssigned: true }
//    ];
});

app.directive('optionsClass', function ($parse) {
  return {  
    require: 'select',
    link: function(scope, elem, attrs, ngSelect) {
      // get the source for the items array that populates the select.
      var optionsSourceStr = attrs.ngOptions.split(' ').pop(),
      // use $parse to get a function from the options-class attribute
      // that you can use to evaluate later.
          getOptionsClass = $parse(attrs.optionsClass);
          
      scope.$watch(optionsSourceStr, function(selectionList) {
        // when the options source changes loop through its items.
        angular.forEach(selectionList, function(item, index) {
          // evaluate against the item to get a mapping object for
          // for your classes.
          var classes = getOptionsClass(item),
          // also get the option you're going to need. This can be found
          // by looking for the option with the appropriate index in the
          // value attribute.
              option = elem.find('option[value=' + index + ']');
              
          // now loop through the key/value pairs in the mapping object
          // and apply the classes that evaluated to be truthy.
          angular.forEach(classes, function(add, className) {
            if(add) {
              angular.element(option).addClass(className);
            }
          });
        });
      });
    }
  };
});

/* ******************************************************************************************************
 * SHEDULE MODAL CONTROLLER     *************************************************************************
 * ******************************************************************************************************
 */
app.controller('schedModalCtrl', function ($scope, $http, $uibModalInstance, data, schedFactory, $interval) {
    console.log("started schedModalCtrl, data: ")
    console.log(data);
    $scope.schedule = schedFactory.getSchedule();
    $scope.schedEntry = data.cell.schedEntry;
    $scope.index = data.cell.index;
    $scope.span = data.cell.span;
    $scope.subjectList = data.subjectList;
    $scope.instructorList = [];
    $scope.roomList = [];
    $scope.historyList = [];
    $scope.selectedView = data.selectedView;
    $scope.selectedItemId = data.selectedItemId;
    $scope.changeCounter = data.changeCounter;
    
    $scope.isReturnBtnDisabled = false;
    $scope.isLockBtnDisabled = false;
    $scope.isApproveBtnDisabled = false;
    $scope.isSubmittedBtnDisabled = false;
    $scope.isUnsubmittedBtnDisabled = false;
    $scope.lbl1IsVisible = false;
    $scope.lbl2IsVisible = false;
    $scope.lbl3IsVisible = false;
    $scope.lbl4IsVisible = false;
    $scope.lbl5IsVisible = false;
    $scope.lbl6IsVisible = false;
    
    $scope.cannotEdit = false;
    $scope.canOnlyEditInstructorRoom = false;
    
    $scope.currentUser = data.currentUser;
    $scope.selectedItem = data.selectedItem;
    
    $scope.comment={
        text:""
    };
    $scope.commentList = [];


    $scope.selectedSubject = null;
    $scope.selectedRoom = null;
    $scope.selectedInstructor = null;
    $scope.selectedHistory = null;

    $scope.dayList = [
        {id: 0, text: 'Monday'},
        {id: 1, text: 'Tuesday'},
        {id: 2, text: 'Wednesday'},
        {id: 3, text: 'Thursday'},
        {id: 4, text: 'Friday'}
    ];

    $scope.startList = [
        {id: 0, text: '7:00 AM', isDisabled: false},
        {id: 1, text: '7:30 AM', isDisabled: false},
        {id: 2, text: '8:00 AM', isDisabled: false},
        {id: 3, text: '8:30 AM', isDisabled: false},
        {id: 4, text: '9:00 AM', isDisabled: false},
        {id: 5, text: '9:30 AM', isDisabled: false},
        {id: 6, text: '10:00 AM', isDisabled: false},
        {id: 7, text: '10:30 AM', isDisabled: false},
        {id: 8, text: '11:00 AM', isDisabled: false},
        {id: 9, text: '11:30 AM', isDisabled: false},
        {id: 10, text: '12:00 PM', isDisabled: false},
        {id: 11, text: '12:30 PM', isDisabled: false},
        {id: 12, text: '1:00 PM', isDisabled: false},
        {id: 13, text: '1:30 PM', isDisabled: false},
        {id: 14, text: '2:00 PM', isDisabled: false},
        {id: 15, text: '2:30 PM', isDisabled: false},
        {id: 16, text: '3:00 PM', isDisabled: false},
        {id: 17, text: '3:30 PM', isDisabled: false},
        {id: 18, text: '4:00 PM', isDisabled: false},
        {id: 19, text: '4:30 PM', isDisabled: false},
        {id: 20, text: '5:00 PM', isDisabled: false},
        {id: 21, text: '5:30 PM', isDisabled: false},
        {id: 22, text: '6:00 PM', isDisabled: false},
        {id: 23, text: '6:30 PM', isDisabled: false},
    ];

    $scope.endList = [
        {id: 1, text: '7:30 AM', isDisabled: false},
        {id: 2, text: '8:00 AM', isDisabled: false},
        {id: 3, text: '8:30 AM', isDisabled: false},
        {id: 4, text: '9:00 AM', isDisabled: false},
        {id: 5, text: '9:30 AM', isDisabled: false},
        {id: 6, text: '10:00 AM', isDisabled: false},
        {id: 7, text: '10:30 AM', isDisabled: false},
        {id: 8, text: '11:00 AM', isDisabled: false},
        {id: 9, text: '11:30 AM', isDisabled: false},
        {id: 10, text: '12:00 PM', isDisabled: false},
        {id: 11, text: '12:30 PM', isDisabled: false},
        {id: 12, text: '1:00 PM', isDisabled: false},
        {id: 13, text: '1:30 PM', isDisabled: false},
        {id: 14, text: '2:00 PM', isDisabled: false},
        {id: 15, text: '2:30 PM', isDisabled: false},
        {id: 16, text: '3:00 PM', isDisabled: false},
        {id: 17, text: '3:30 PM', isDisabled: false},
        {id: 18, text: '4:00 PM', isDisabled: false},
        {id: 19, text: '4:30 PM', isDisabled: false},
        {id: 20, text: '5:00 PM', isDisabled: false},
        {id: 21, text: '5:30 PM', isDisabled: false},
        {id: 22, text: '6:00 PM', isDisabled: false},
        {id: 23, text: '6:30 PM', isDisabled: false},
        {id: 24, text: '7:00 PM', isDisabled: false}
    ];
    
    $scope.status = {
        isCustomHeaderOpen: false,
        isFirstOpen: true,
        isFirstDisabled: false
      };

    $scope.initialize = function () {
        if($scope.currentUser.userType == "COLLEGE_REGISTRAR" && $scope.schedEntry != null){
            var isSchedCreatedByMe = ($scope.currentUser.userID == $scope.schedEntry.creator.userID);
            var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
            var isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == $scope.schedEntry.creator.collegeID);

            if($scope.schedEntry.status === "approved") $scope.isApproveBtnDisabled = true;
            else if($scope.schedEntry.status === "locked") $scope.isLockBtnDisabled = true;
            else if($scope.schedEntry.status === "unsubmitted"){
                $scope.isReturnBtnDisabled = true;
                $scope.isLockBtnDisabled = true;
                $scope.isApproveBtnDisabled = true;
            }

            if(isSchedCreatedByMe && $scope.schedEntry.status === "submitted"){
                $scope.isReturnBtnDisabled = true;
            }

            if(isSelectedItemMine){
                if(!isSchedCreatedInMyCollege){
                    if($scope.schedEntry.status === "submitted"){
                        $scope.isReturnBtnDisabled = true;
                        $scope.isLockBtnDisabled = true;
                        $scope.isApproveBtnDisabled = true;
                    }
                }
            }else{
                $scope.isApproveBtnDisabled = true;
                if(!isSchedCreatedInMyCollege){
                    $scope.isReturnBtnDisabled = true;
                    $scope.isLockBtnDisabled = true;
                }
                if($scope.schedEntry.status === "approved"){
                    $scope.isReturnBtnDisabled = true;
                    $scope.isLockBtnDisabled = true;
                    $scope.isApproveBtnDisabled = true;
                    $scope.cannotEdit = true;
                }
            }
        
//            var isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == $scope.schedEntry.creator.collegeID);
            if(!isSchedCreatedInMyCollege){
                $scope.cannotEdit = true;
            }
            
            if($scope.cannotEdit==true && $scope.selectedItem.isAssigned && isSchedCreatedInMyCollege && ($scope.schedEntry.status=="locked" || $scope.schedEntry.status=="approved")){
                $scope.canOnlyEditInstructorRoom = true;
            }
            
        }else if($scope.currentUser.userType == "COLLEGE_REGISTRAR"){
            $scope.isReturnBtnDisabled = true;
            $scope.isLockBtnDisabled = true;
            $scope.isApproveBtnDisabled = true;
        }else if($scope.currentUser.userType == "SCHEDULER" && $scope.schedEntry != null){
            var isSchedCreatedByMe = ($scope.currentUser.userID == $scope.schedEntry.creator.userID);
            if(!isSchedCreatedByMe){
                $scope.cannotEdit = true;
            }
            if(isSchedCreatedByMe && $scope.schedEntry.status === "submitted"){
                $scope.isSubmittedBtnDisabled = true;
                $scope.cannotEdit = true;
            }
            else if(isSchedCreatedByMe && $scope.schedEntry.status === "unsubmitted") $scope.isUnsubmittedBtnDisabled = true;
            else{
                $scope.isSubmittedBtnDisabled = true;
                $scope.isUnsubmittedBtnDisabled = true;
            }
        }else if($scope.currentUser.userType == "SCHEDULER"){
                $scope.isSubmittedBtnDisabled = true;
                $scope.isUnsubmittedBtnDisabled = true;
        }
        
        $scope.selectedDay = Math.floor($scope.index / 24);
        $scope.selectedStart = $scope.index % 24;
        if ($scope.schedEntry != null) {
//            $scope.selectedEnd = $scope.schedEntry.end % 24;
            $scope.selectedEnd = ($scope.schedEntry.end % 24 ==0) ? 24 : $scope.schedEntry.end % 24;
            var arrayLength = $scope.subjectList.length;

            for (var i = 0; i < arrayLength; i++) {
                console.log("looping... " + $scope.subjectList[i].subjectID + " : " + $scope.schedEntry.subject.subjectID);
                if ($scope.subjectList[i].subjectID == $scope.schedEntry.subject.subjectID) {
                    $scope.selectedSubject = $scope.subjectList[i];
                    console.log("FOUND SIMILAR!");
                    break;
                }
            }

            $scope.originalDay = Math.floor($scope.index / 24);
            $scope.originalStart = $scope.schedEntry.start;
            $scope.originalEnd = $scope.schedEntry.end;
            $scope.originalSubject = $scope.selectedSubject;
            $scope.originalInstructor = $scope.schedEntry.instructor != null ? $scope.schedEntry.instructor.instructorID : -1;
            // $scope.originalRoom = $scope.schedEntry.room != null ?  $scope.schedEntry.room.roomID : null;
            $scope.originalRoom = $scope.schedEntry.room != null ? $scope.schedEntry.room.roomID : -1;
        } else {
            $scope.selectedEnd = $scope.index % 24 + 1;
            $scope.originalStart = -1;
            $scope.originalEnd = -1;
            $scope.originalInstructor = -1;
            $scope.originalRoom = -1;
        }
        
        $scope.getFreeRooms();
        $scope.getFreeInstructors();
        $scope.setAvailableSubjects();
        $scope.setAvailableTime();
        $scope.getHistory();
        getSchedTable();
        $scope.getComments();
    };
    
    $scope.return = function () {
        clearInterval(schedInterval);
        var newStatus;
        if ($scope.schedEntry.creator.userType == "SCHEDULER") newStatus = "unsubmitted";
        else newStatus = "submitted";
        $uibModalInstance.close({
            action: "validate",
            newStatus: newStatus,
            schedId: $scope.schedEntry.schedID,
            isReturn: true
        });
    };
    
    $scope.requestReturn = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "requestReturn",
            schedId: $scope.schedEntry.schedID
        });
    }
    
    $scope.denyReturn = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "denyReturn",
            schedId: $scope.schedEntry.schedID
        });
    }
    
    $scope.lock = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "validate",
            newStatus: "locked",
            schedId: $scope.schedEntry.schedID
        });
    };
    
    $scope.approve = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "validate",
            newStatus: "approved",
            schedId: $scope.schedEntry.schedID
        });
    };
    
    $scope.submit = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "validate",
            newStatus: "submitted",
            schedId: $scope.schedEntry.schedID
        });
    };
    
    $scope.unsubmit = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "validate",
            newStatus: "unsubmitted",
            schedId: $scope.schedEntry.schedID
        });
    };

    $scope.setRoomSelection = function () {
        var arrayLength = $scope.roomList.length;for (var i = 0; i < arrayLength; i++) {
            if ($scope.roomList[i].id == $scope.originalRoom) {
                $scope.selectedRoom = $scope.roomList[i];
                break;
            }
        }
    };

    $scope.setInstructorSelection = function () {
        var arrayLength = $scope.instructorList.length;
        for (var i = 0; i < arrayLength; i++) {
            if ($scope.instructorList[i].id == $scope.originalInstructor) {
                $scope.selectedInstructor = $scope.instructorList[i];
                break;
            }
        }
    };

    $scope.getFreeRooms = function (toSelect) {
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {
                function: "getFreeList",
                type: 'room',
                start: $scope.selectedStart + $scope.selectedDay * 24,
                end: $scope.selectedEnd + $scope.selectedDay * 24,
                origStart: $scope.originalStart,
                origEnd: $scope.originalEnd,
                origItem: $scope.originalRoom
            }
        }).success(function (data, status, headers, config) {
            console.log("getting free rooms... " + data);
            $scope.roomList = data;
            if($scope.cannotEdit){
                $scope.roomList.push({
                    id: $scope.schedEntry.room.roomID,
                    name: $scope.schedEntry.room.name,
                    departmentId: $scope.schedEntry.room.department.departmentID,
                    collegeCode: $scope.schedEntry.room.department.college.code,
                    departmentCode: $scope.schedEntry.room.department.code
                });
            }
            if($scope.schedEntry!=null && toSelect == null)$scope.setRoomSelection();
            else if(toSelect != null){
                var isFound = false;
                for (var i = 0; i < $scope.roomList.length; i++) {
                    if ($scope.roomList[i].id == toSelect) {
                        $scope.selectedRoom = $scope.roomList[i];
                        isFound = true;
                        break;
                    }
                }
                if(!isFound){
                    $scope.lbl3IsVisible = true;
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("error getting rooms!");
        });
    };
    
    $scope.getFreeRoomsForHistory = function (selectedHistory) {
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {
                function: "getFreeList",
                type: 'room',
                start: $scope.selectedStart + $scope.selectedDay * 24,
                end: $scope.selectedEnd + $scope.selectedDay * 24,
                origStart: $scope.originalStart,
                origEnd: $scope.originalEnd,
                origItem: $scope.originalRoom
            }
        }).success(function (data, status, headers, config) {
            $scope.roomList = data;
            $scope.setRoomSelection();
        
            if(selectedHistory.room != null){
                console.log("selectedHistory.room is not null")
                var found=false;
                for(i=0; i<$scope.roomList.length; i++){
                    console.log("comparing...")
                    console.log($scope.roomList[i]);
                    console.log(selectedHistory.room.roomID)
                    if($scope.roomList[i].id == selectedHistory.room.roomID){
                        console.log("selecting room from history");
                        $scope.selectedRoom = $scope.roomList[i];
                        found = true;
                        break;
                    }
                }
                if(!found){
                    $scope.selectedRoom = null;
                    $scope.lbl3IsVisible = true;
                }
            } else $scope.selectedRoom = null;
        }).error(function (data, status, headers, config) {
            console.log("error getting rooms!");
        });
    };

    $scope.getFreeInstructors = function (toSelect) {
        console.log("GETTING FREE INSTRUCTORS... ");
        console.log($scope.selectedStart);
        console.log($scope.selectedEnd);
        console.log($scope.originalStart);
        console.log($scope.originalEnd);
        console.log($scope.originalInstructor);
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {
                function: "getFreeList",
                type: 'instructor',
                start: $scope.selectedStart + $scope.selectedDay * 24,
                end: $scope.selectedEnd + $scope.selectedDay * 24,
                origStart: $scope.originalStart,
                origEnd: $scope.originalEnd,
                origItem: $scope.originalInstructor
            }
        }).success(function (data, status, headers, config) {
            $scope.instructorList = data;
            console.log("getting free instructors... " + $scope.instructorList);
            console.log("data: ")
            console.log(data);
            if($scope.cannotEdit){
                $scope.instructorList.push({
                    id: $scope.schedEntry.instructor.instructorID,
                    name: $scope.schedEntry.instructor.name,
                    departmentId: $scope.schedEntry.instructor.department.departmentID,
                    collegeCode: $scope.schedEntry.instructor.department.college.code,
                    departmentCode: $scope.schedEntry.instructor.department.code
                });
            }
             if($scope.schedEntry!=null && toSelect == null) $scope.setInstructorSelection();
             else if(toSelect != null){
                var isFound = false;
                for (var i = 0; i < $scope.instructorList.length; i++) {
                    if ($scope.instructorList[i].id == toSelect) {
                        $scope.selectedInstructor = $scope.instructorList[i];
                        isFound = true;
                        break;
                    }
                }
                if(!isFound){
                    $scope.lbl2IsVisible = true;
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("error getting instructors!");
        });
    };
    
    $scope.getFreeInstructorsForHistory = function (selectedHistory) {
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {
                function: "getFreeList",
                type: 'instructor',
                start: $scope.selectedStart + $scope.selectedDay * 24,
                end: $scope.selectedEnd + $scope.selectedDay * 24,
                origStart: $scope.originalStart,
                origEnd: $scope.originalEnd,
                origItem: $scope.originalInstructor
            }
        }).success(function (data, status, headers, config) {
            $scope.instructorList = data;
            console.log("getting free instructors... " + $scope.instructorList);
            $scope.setInstructorSelection();
            
            if(selectedHistory.instructor!= null){
                console.log("selectedHistory.instructor is not null")
                var found=false;
                for(i=0; i<$scope.instructorList.length; i++){
                    if($scope.instructorList[i].id == selectedHistory.instructor.instructorID){
                        console.log("selecting instructor from history");
                        $scope.selectedInstructor = $scope.instructorList[i];
                        found = true;
                        break;
                    }
                }
                if(!found){
                    $scope.selectedInstructor = null;
                    $scope.lbl2IsVisible = true;
                }
            } else $scope.selectedInstructor = null;
        }).error(function (data, status, headers, config) {
            console.log("error getting instructors!");
        });
    };
    
    $scope.getHistory = function () {
        var day = $scope.selectedDay;
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {
                function: "getHistory",
                start: $scope.selectedStart + $scope.selectedDay * 24,
                end: $scope.selectedEnd + $scope.selectedDay * 24,
                type: $scope.selectedView,
                itemId: $scope.selectedItemId
            }
        }).success(function (data, status, headers, config) {
            console.log("getting history ... " + data);
            $scope.historyList = data;
            console.log($scope.historyList);
            $scope.generateHistorySelection(day);
        }).error(function (data, status, headers, config) {
            console.log("error getting history!");
        });
    };
    
    
    
    $scope.generateHistorySelection = function (day) {
        for(i = 0; i < $scope.historyList.length; i++){
            $scope.historyList[i].isDisabled = true;
            for(j = 0; j < $scope.subjectList.length; j++){
                if($scope.historyList[i].subject.subjectID == $scope.subjectList[j].subjectID && !$scope.subjectList[j].isDisabled){
//                    var timespan = ($scope.historyList[i].end - $scope.historyList[i].start)/2;
                    var subjRemainingHours = $scope.subjectList[j].hours - $scope.subjectList[j].hoursPlotted;
//                    if (timespan <= subjRemainingHours) $scope.historyList[i].isDisabled = false;
                    if(subjRemainingHours != 0 || $scope.subjectList[j].subjectID == $scope.schedEntry.subject.subjectID) $scope.historyList[i].isDisabled = false;
                    break;
                }
            }
        }
        console.log("CONTENTS OF SCHEDULE.CONTENTS: ");
        console.log($scope.schedule);
        for(i = 0; i < $scope.historyList.length; i++){
            if(!$scope.historyList[i].isDisabled){
//                console.log("i: " + i)
//                console.log("$scope.historyList[i].start: " + $scope.historyList[i].start)
//                console.log("$scope.historyList[i].start % 24: " + $scope.historyList[i].start % 24)
//                console.log("$scope.historyList[i].end: " + $scope.historyList[i].end)
//                console.log("$scope.historyList[i].end % 24: " + $scope.historyList[i].end % 24)
                var start = $scope.historyList[i].start % 24;
                var end = ($scope.historyList[i].end % 24 == 0) ? 24 : $scope.historyList[i].end % 24;
                for(j = start; j < end; j++){
//                    console.log("day: " + day + ", j: " + j)
                    if ($scope.schedule.contents[day][j].schedEntry != null && $scope.schedule.contents[day][j].schedEntry != $scope.schedEntry){
                        $scope.historyList[i].isDisabled = true;
                        break;
                    }
                }
            }
            
        }
        
        console.log("displaying historyList after generate");
        console.log($scope.historyList);
    };

    $scope.setAvailableSubjects = function (prevSubjId) {
        for (i = 0; i < $scope.subjectList.length; i++) {
            console.log("subject looping... " + i);
            var hasRemaining = $scope.subjectList[i].hours != $scope.subjectList[i].hoursPlotted;
            if ($scope.subjectList[i] == $scope.originalSubject || hasRemaining) {
                $scope.subjectList[i].isDisabled = false;
                console.log("turned false");
            } else {
                $scope.subjectList[i].isDisabled = true;
                console.log("turned true");
            }
            
            if ($scope.subjectList[i].department.college.collegeID != $scope.currentUser.collegeID && !$scope.cannotEdit){
                $scope.subjectList[i].isDisabled = true;
                console.log("turned true");
            }
            
            if(prevSubjId!=null){
                if($scope.subjectList[i].subjectID == prevSubjId && $scope.subjectList[i].isDisabled){
                    $scope.selectedSubject = null;
                    $scope.lbl4IsVisible = true;
                }else if($scope.subjectList[i].subjectID == prevSubjId && !$scope.subjectList[i].isDisabled){
                    $scope.selectedSubject = $scope.subjectList[i];
                }
            }
        }
        console.log("showing isdisableds: ");
        for (i = 0; i < $scope.subjectList.length; i++) {
            console.log($scope.subjectList[i]);
        }
    };

    $scope.setAvailableTime = function () {
        var day = $scope.selectedDay;
        for (i = 0; i < 24; i++) {
            $scope.startList[i].isDisabled = false;
            $scope.endList[i].isDisabled = false;
        }

        for (i = 0; i < $scope.schedule.contents[day].length; i++) {
            if ($scope.schedule.contents[day][i].schedEntry != null && $scope.schedule.contents[day][i].schedEntry != $scope.schedEntry) {
                var content = $scope.schedule.contents[day][i];
                var start = content.index % 24;
                for (j = 0; j < content.span; j++) {
                    $scope.startList[start + j].isDisabled = true;
                    $scope.endList[start + j].isDisabled = true;
                }

                if (start > $scope.selectedStart) {
                    for (j = start + 1; j < 24; j++) {
                        $scope.endList[j].isDisabled = true;
                    }
                }
            }
        }

        if ($scope.selectedStart != null) {
            if ($scope.startList[$scope.selectedStart].isDisabled){
                $scope.selectedStart = null;
                $scope.lbl5IsVisible = true;
            }
        }

        var j;
        if ($scope.selectedStart != null) {
            j = $scope.selectedStart;
            if ($scope.selectedSubject != null && $scope.selectedSubject != $scope.originalSubject) {
                var remaining = $scope.selectedSubject.hours - $scope.selectedSubject.hoursPlotted;
                for (i = $scope.selectedStart + remaining * 2; i < 24; i++)
                    $scope.endList[i].isDisabled = true;
            } else if ($scope.selectedSubject != null && $scope.selectedSubject == $scope.originalSubject) {
                var remaining = $scope.selectedSubject.hours;
                for (i = $scope.selectedStart + remaining * 2; i < 24; i++)
                    $scope.endList[i].isDisabled = true;
            }
        } else
            j = 24;

        for (i = 0; i < j; i++) {
            $scope.endList[i].isDisabled = true;
        }

        if ($scope.selectedEnd != null) {
            if ($scope.endList[$scope.selectedEnd - 1].isDisabled){
                $scope.selectedEnd = null;
                $scope.lbl6IsVisible = true;
            }
        }
    };

    $scope.onDayChange = function (previousSelection) {
        $scope.clearLabels();
        $scope.setAvailableTime(previousSelection);
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
            $scope.getHistory();
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    };

    $scope.onStartChange = function () {
        $scope.clearLabels();
        $scope.setAvailableEnd();
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
            $scope.getHistory();
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    };

    $scope.onEndChange = function () {
        $scope.clearLabels();
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
            $scope.getHistory();
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    }
    
    $scope.onHistorySelect = function(selectedHistory){
        $scope.clearLabels();
        for(i=0; i<$scope.subjectList.length; i++){
            if($scope.subjectList[i].subjectID == selectedHistory.subject.subjectID){
                $scope.selectedSubject = $scope.subjectList[i];
                break;
            }
        }
        $scope.selectedStart = selectedHistory.start % 24;
        $scope.setAvailableEnd();
        var historyEnd = (selectedHistory.end % 24 == 0) ? 24 : selectedHistory.end % 24;
        for(j = 0; j < $scope.subjectList.length; j++){
            if($scope.selectedHistory.subject.subjectID == $scope.subjectList[j].subjectID){
                var timespan = ($scope.selectedHistory.end - $scope.selectedHistory.start)/2;
                var subjRemainingHours = $scope.subjectList[j].hours - $scope.subjectList[j].hoursPlotted;
                if($scope.schedEntry!=null && $scope.selectedHistory.subject.subjectID == $scope.schedEntry.subject.subjectID) subjRemainingHours = subjRemainingHours + (($scope.schedEntry.end - $scope.schedEntry.start)/2);
                if (timespan > subjRemainingHours){
                    historyEnd = $scope.selectedStart + (subjRemainingHours*2);
                    $scope.lbl1IsVisible = true;
                }
                break;
            }
        }
//        $scope.selectedEnd = (selectedHistory.end % 24 == 0) ? 24 : selectedHistory.end % 24;
        $scope.selectedEnd = historyEnd;
        $scope.getFreeRoomsForHistory(selectedHistory);
        $scope.getFreeInstructorsForHistory(selectedHistory); 
    }

    $scope.onSubjectChange = function () {
        $scope.clearLabels();
        $scope.setAvailableEnd();
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    }

    $scope.setAvailableEnd = function () {
        var day = $scope.selectedDay;
        for (i = 0; i < 24; i++) {
            $scope.endList[i].isDisabled = false;
        }

        for (i = 0; i < $scope.schedule.contents[day].length; i++) {
            if ($scope.schedule.contents[day][i].schedEntry != null && $scope.schedule.contents[day][i].schedEntry != $scope.schedEntry) {
                var content = $scope.schedule.contents[day][i];
                var start = content.index % 24;
                for (j = 0; j < content.span; j++) {
                    $scope.endList[start + j].isDisabled = true;
                }

                if (start > $scope.selectedStart) {
                    for (j = start + 1; j < 24; j++) {
                        $scope.endList[j].isDisabled = true;
                    }
                }
            }
        }

        var j;
        if ($scope.selectedStart != null) {
            j = $scope.selectedStart;
            if ($scope.selectedSubject != null && $scope.selectedSubject != $scope.originalSubject) {
                var remaining = $scope.selectedSubject.hours - $scope.selectedSubject.hoursPlotted;
                for (i = $scope.selectedStart + remaining * 2; i < 24; i++)
                    $scope.endList[i].isDisabled = true;
            } else if ($scope.selectedSubject != null && $scope.selectedSubject == $scope.originalSubject) {
                var remaining = $scope.selectedSubject.hours;
                for (i = $scope.selectedStart + remaining * 2; i < 24; i++)
                    $scope.endList[i].isDisabled = true;
            }
        } else
            j = 24;

        for (i = 0; i < j; i++) {
            $scope.endList[i].isDisabled = true;
        }

        if ($scope.selectedEnd != null) {
            if ($scope.endList[$scope.selectedEnd - 1].isDisabled)
                $scope.selectedEnd = null;
        }
    };
    
    $scope.clearLabels = function(){
        $scope.lbl1IsVisible = false;
        if($scope.selectedInstructor != null) $scope.lbl2IsVisible = false;
        if($scope.selectedRoom != null) $scope.lbl3IsVisible = false;
        if($scope.selectedSubject != null) $scope.lbl4IsVisible = false;
        if($scope.selectedStart != null) $scope.lbl5IsVisible = false;
        if($scope.selectedEnd != null) $scope.lbl6IsVisible = false;
    };
    
     var getSchedTable = function () {
//        $location.path($scope.selectedView);
//        $scope.getSched();
//        $interval(function(){
//            if(toCheck) getIfChanged();
//            getIfChanged();
//        },1000)
        schedInterval = setInterval(function() { getIfChanged(); }, 1000);
    };
    
    var getIfChanged = function(){
       $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "getChangeCounter"}
        }).success(function (data, status, headers, config) {
            var newCounter = data;
            console.log("IN MODAL: got new changeCounter: " + newCounter);
            if(newCounter !== $scope.changeCounter){
                $scope.changeCounter = newCounter;
                getSched();
            }
        }).error(function (data, status, headers, config) {
        }); 
    };
    
    var getSched = function () {
//      $location.path($scope.selectedView);
      $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "getSched", type: $scope.selectedView, itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
        }).success(function (data, status, headers, config) {
            $scope.schedList = data;
            var previousSelection = {
                start: $scope.selectedStart,
                end: $scope.selectedEnd,
                instructorId: $scope.selectedInstructor!=null ? $scope.selectedInstructor.id : null,
                roomId: $scope.selectedRoom!=null ? $scope.selectedRoom.id : null,
                subjectId: $scope.selectedSubject != null ? $scope.selectedSubject.subjectID : null
            };
            if($scope.schedEntry != null){
                var isFound;
                var day = Math.floor($scope.schedEntry.start/24);
                for(var i=0; i< $scope.schedule.contents[day].length; i++){
                    if($scope.schedule.contents[day][i].schedEntry != null && $scope.schedule.contents[day][i].schedEntry.schedID == $scope.schedEntry.schedID){
                        isFound=true;
                    }
                }
                if(!isFound){
                    alert("The existing schedule has been modified/deleted by another user.");
                    $scope.cancel();
                }
            }else{
                $http({
                    method: 'GET',
                    url: 'SchedTable',
                    params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
                }).success(function (data, status, headers, config) {
                    $scope.clearLabels();
                    $scope.subjectList = data;
                    $scope.setAvailableSubjects(previousSelection.subjectId);
                    $scope.setAvailableTime();
                    if ($scope.selectedStart != null && $scope.selectedEnd != null){
                        $scope.getFreeInstructors(previousSelection.instructorId);
                        $scope.getFreeRooms(previousSelection.roomId);
                        $scope.getHistory();
                    }
                }).error(function (data, status, headers, config) {
                });
            }
        }).error(function (data, status, headers, config) {
        });
    };
    
    $scope.getComments = function(){
        if($scope.schedEntry != null){
            $http({
                method: 'GET',
                url: 'SchedTable',
                params: {function: "getComments", schedId: $scope.schedEntry.schedID}
            }).success(function (data, status, headers, config) {
                $scope.commentList = data;
                console.log("success in getting comments");
            }).error(function (data, status, headers, config) {
                console.log("error in getting comments");
            }); 
        }
    };
    
    $scope.addComment = function(){
        if($scope.comment.text != ""){
            $http({
                method: 'POST',
                url: 'SchedTable',
                contentType: 'application/json',
                data: JSON.stringify({action: "addComment", schedId: $scope.schedEntry.schedID, text: $scope.comment.text})
            }).success(function (data, status, headers, config) {
                $scope.comment.text = "";
                console.log("success in adding comment");
                $scope.getComments();
            }).error(function (data, status, headers, config) {
            console.log("error in adding comment");
            }); 
        }
    };

    $scope.ok = function () {
        clearInterval(schedInterval);
        if($scope.selectedSubject != null && $scope.selectedStart != null && $scope.selectedEnd != null){
            $uibModalInstance.close({
        //            day: $scope.selectedDay,
                action: "add",
                start: $scope.selectedStart + $scope.selectedDay * 24,
                end: $scope.selectedEnd + $scope.selectedDay * 24,
        //            subject: $scope.selectedSubject,
                subject: $scope.selectedSubject != null ? $scope.selectedSubject.subjectID : -1,
                instructor: $scope.selectedInstructor != null ? $scope.selectedInstructor.id : -1,
                room: $scope.selectedRoom != null ? $scope.selectedRoom.id : -1,
        //            instructor: $scope.selectedInstructor,
        //            room: $scope.selectedRoom,
                origSched: $scope.schedEntry

        //            origSched: $scope.schedEntry != null ? $scope.schedEntry.schedID : null
            });
        }else alert("Please fill the required fields.");
    };

    $scope.delete = function () {
        clearInterval(schedInterval);
        $uibModalInstance.close({
            action: "delete",
            origSched: $scope.schedEntry
        });
    };

    $scope.cancel = function () {
        clearInterval(schedInterval);
        $uibModalInstance.dismiss('wawCancel');
    };

//    $scope.getSubjects = function () {
//
//    };
    $scope.initialize();
});