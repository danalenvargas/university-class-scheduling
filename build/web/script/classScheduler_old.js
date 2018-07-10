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
                    controller: 'SchedCtrl'
                })

                .when('/instructor', {
                    templateUrl: 'instructorsTable.html',
                    controller: 'SchedCtrl'
                })

                .when('/room', {
                    templateUrl: 'roomsTable.html',
                    controller: 'SchedCtrl'
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

app.controller('SchedCtrl', function ($scope, $http, $location, $uibModal, schedFactory, $interval) {
    $scope.schedule = schedFactory.getSchedule();
    $scope.views = ["section", "room", "instructor"];
    $scope.selectedView = "section";
    $scope.selectionList = [];
    $scope.currentUserId;
    $scope.changeCounter = 0;

    $scope.update = function () {
        var counter = 0;
        var schedule = [];
        var heightPerCell = Math.floor($scope.schedule.height / $scope.schedule.rows);
        var status;
        var bgColor="White";

        if ($scope.schedList!=null && $scope.schedList[counter] != null) {
            var day = Math.floor($scope.schedList[counter].start / 24);
            var timeStart = $scope.schedList[counter].start % 24;
//            timeEnd = $scope.schedList[counter].end % 24;
            var timeEnd = ($scope.schedList[counter].end % 24 == 0) ? 24 : $scope.schedList[counter].end % 24;
            //var index = 0;
            for (var i = 0; i < $scope.schedule.cols; i++) {
                var a = [];
                for (var j = 0; j < $scope.schedule.rows; j++) {
                    if (day === i && timeStart === j) {
                        status = $scope.schedList[counter].status;
                        if(status == "unsubmitted") bgColor="Ivory";
                        else if(status == "submitted") bgColor="LawnGreen";
                        else if(status == "locked") bgColor="DarkOliveGreen";
                        else if(status == "approved") bgColor="DodgerBlue";
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
//                            timeEnd = $scope.schedList[counter].end % 24;
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

    $scope.adjust = function (schedEntry, origSched) {
        var schedule = [];
        var heightPerCell = Math.floor($scope.schedule.height / $scope.schedule.rows);

//        if ($scope.schedList[counter] != null) {
        var day = Math.floor(schedEntry.start / 24);
        var timeStart = schedEntry.start % 24;
        var timeEnd = schedEntry % 24;
        //var index = 0;
        for (i = 0; i < $scope.schedule.contents.length; i++) {
            for (j = 0; j < $scope.schedule.contents[i].length; j++) {
                if ($scope.schedule.contents[i][j].schedEntry == origSched) {
                    for (x = $scope.schedule.contents[i][j].index; x < origSched.end % 24; x++) {

                    }
                }
            }
        }

        for (var i = 0; i < $scope.schedule.cols; i++) {
            var a = [];
            for (var j = 0; j < $scope.schedule.rows; j++) {

                if (day === i && timeStart === j) {
                    a.push({
                        schedEntry: $scope.schedList[counter],
                        index: (i * 24) + j,
                        span: timeEnd - timeStart,
                        height: heightPerCell * (timeEnd - timeStart)
                    });
                    counter++;
                    j = timeEnd - 1;
                    if ($scope.schedList[counter] != null) {
                        day = Math.floor($scope.schedList[counter].start / 24);
                        timeStart = $scope.schedList[counter].start % 24;
                        timeEnd = $scope.schedList[counter].end % 24;
                    }

                } else {
                    a.push({
                        schedEntry: null,
                        index: (i * 24) + j,
                        height: heightPerCell,
                        span: 1,
                    });
                }
            }
            schedule.push(a);
        }
        
        console.log("AFTER UPDATE: " + $scope.schedule.contents);
        return $scope.schedule.contents = schedule;
    };

    $scope.onViewChange = function () {
        console.log("onViewChange");
        console.log($scope.selectedView);
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "changeView", type: $scope.selectedView}
        }).success(function (data, status, headers, config) {
            $scope.selectionList = data;
            console.log("CHANGED SELECTION");
            console.log($scope.selectionList);
            //$scope.schedList = null;
            //$scope.update($scope.schedList);
//            $scope.schedule.contents = [];
//            if($scope.selectedView == "room" || $scope.selectedView == "instructor") $scope.subjectList = [];
        }).error(function (data, status, headers, config) {
        });
    };
    
    $scope.changeView = function () {
        console.log("changeView function");
        $scope.schedule.contents = [];
        if($scope.selectedView == "room" || $scope.selectedView == "instructor") $scope.subjectList = [];
        $scope.onViewChange($scope.selectedView);
    };
    
    $scope.getSchedTable = function () {
        $location.path($scope.selectedView);
        getSched();
        $interval(function(){
            getIfChanged();
        },2000)
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
                getSched();
            }
        }).error(function (data, status, headers, config) {
        }); 
    };
    
    var getSched = function () {
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
        var modalInstance = $uibModal.open({
            templateUrl: "partials/scheduleModal.html",
            controller: "schedModalCtrl",
            size: "lg",
            resolve: {
                data: {cell: selectedCell,
                    subjectList: $scope.subjectList,
                    selectedView: $scope.selectedView,
                    selectedItemId: $scope.selectedItem.id}
            }
        });

        modalInstance.result.then(function (result) {
            result.section = $scope.selectedItem.id;
            if (result.action == "add") {
                $scope.addSchedule(result);
            } else if (result.action == "delete") {
                $scope.deleteSchedule(result);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.openSchedulerValidationModal = function (selectedCell) {
        if(selectedCell.schedEntry != null){
            var modalInstance = $uibModal.open({
                templateUrl: "partials/schedulerValidationModal.html",
                controller: "schedValidationCtrl",
                size: "lg",
                resolve: {
                    data: {cell: selectedCell}
                }
            });

            modalInstance.result.then(function (result) {
                result.section = $scope.selectedItem.id;
                if (result.action == "add") {
                    $scope.addSchedule(result);
                } else if (result.action == "delete") {
                    $scope.deleteSchedule(result);
                }
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        }
    };

    $scope.addSchedule = function (sched) {
        console.log("========= ADDING SECTIONS ========");
        console.log(sched.section);
//        console.log(data.day);
        console.log(sched.start);
        console.log(sched.end);
        console.log(sched.subject);
        console.log(sched.instructor);
        console.log(sched.room);
        console.log(sched.origSched);
//        var jsonData = JSON.stringify(data);
        $http({
            method: 'POST',
            url: 'SchedTable',
            contentType: 'application/json',
            data: JSON.stringify(sched)
//            headers: {'Content-Type': 'application/json'},
//            data: data
//            params: {section: result.section,
//                department: result.department,
//                college: result.college,
//                start: result.start + result.day*24,
//                end: result.end + result.day*24,
//                subject: result.subject,
//                instructor: result.instructor,
//                room: result.room,
//                origSched: result.origSched}
        }).success(function (data, status, headers, config) {
//            $scope.schedList.push(data, sched.origSched);
            console.log("--------------- ADDING... ");
            console.log($scope.schedList);
            console.log($scope.schedList.length);
            if (sched.origSched != null) {
                for (i = 0; i < $scope.schedList.length; i++) {
                    if ($scope.schedList[i].schedID == sched.origSched.schedID) {
                        $scope.schedList.splice(i, 1);
                    }
                }
            }
            for (i = 0; i <= $scope.schedList.length; i++) {
                if (i == $scope.schedList.length) {
                    $scope.schedList.push(data);
                    break;
//                    console.log("NO MORE ELEMENTS");
                }
//                else{
//                    console.log($scope.schedList[i]);
//                }
                else if ($scope.schedList[i].start > data.start) {
                    $scope.schedList.splice(i, 0, data);
                    break;
                }
            }
            console.log("SUCCESSFULLY ADDED! ");
            console.log($scope.schedList);
            if ($scope.selectedView == "section") {
                $http({
                    method: 'GET',
                    url: 'SchedTable',
                    params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
                }).success(function (data, status, headers, config) {
                    //console.log("GETTING SCHED:!: type: " + $scope.selectedView + " item: " + $scope.selectedItem.name + $scope.selectedItem.id + $scope.selectedItem.departmentId + $scope.selectedItem.collegeId);
                    //console.log("testing 1");
                    console.log("//*** SUBJECT LIST: ");
                    console.log(data);
                    $scope.subjectList = data;
                }).error(function (data, status, headers, config) {
                });
            }
            $scope.update();
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
    
    $scope.getCurrentUserId = function(){
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "getCurrentUserId"}
        }).success(function (data, status, headers, config) {
            $scope.currentUserId = data;
        }).error(function (data, status, headers, config) {
        });
    };
    
    $scope.onViewChange($scope.selectedView);
    $scope.getCurrentUserId();
    
//    window.onload = function() {
//        $scope.onViewChange($scope.selectedView);
//    };
    //$scope.onViewChange($scope.selectedView);
//            $scope.getSchedTable();
});

/* ******************************************************************************************************
 * SHEDULE MODAL CONTROLLER     *************************************************************************
 * ******************************************************************************************************
 */
app.controller('schedModalCtrl', function ($scope, $http, $uibModalInstance, data, schedFactory) {
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

    $scope.initialize = function () {
//        //**DEBUG CODE
//        for (i = 0; i < $scope.schedule.contents.length; i++) {
//            console.log("SCHEDULE CONTENTS (" + i + ") ");
//            console.log($scope.schedule.contents[i]);
//        }
//        //**DEBUG CODE
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
        $scope.setAvailableTime($scope.selectedDay);
        $scope.getHistory($scope.selectedDay);
    };

    $scope.setRoomSelection = function () {
        var arrayLength = $scope.roomList.length;
        for (var i = 0; i < arrayLength; i++) {
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

    $scope.getFreeRooms = function () {
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
            $scope.setRoomSelection();
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
                for(i=0; i<$scope.roomList.length; i++){
                    console.log("comparing...")
                    console.log($scope.roomList[i]);
                    console.log(selectedHistory.room.roomID)
                    if($scope.roomList[i].id == selectedHistory.room.roomID){
                        console.log("selecting room from history");
                        $scope.selectedRoom = $scope.roomList[i];
                        break;
                    }
                }
            }  
        }).error(function (data, status, headers, config) {
            console.log("error getting rooms!");
        });
    };

    $scope.getFreeInstructors = function () {
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
            $scope.setInstructorSelection();
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
                for(i=0; i<$scope.instructorList.length; i++){
                    if($scope.instructorList[i].id == selectedHistory.instructor.instructorID){
                        console.log("selecting instructor from history");
                        $scope.selectedInstructor = $scope.instructorList[i];
                        break;
                    }
                }
            }
        }).error(function (data, status, headers, config) {
            console.log("error getting instructors!");
        });
    };
    
    $scope.getHistory = function (day) {
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
                if($scope.historyList[i].subject.subjectID == $scope.subjectList[j].subjectID){
                    var timespan = ($scope.historyList[i].end - $scope.historyList[i].start)/2;
                    var subjRemainingHours = $scope.subjectList[j].hours - $scope.subjectList[j].hoursPlotted;
                    if (timespan <= subjRemainingHours) $scope.historyList[i].isDisabled = false;
                    break;
                }
            }
        }
        for(i = 0; i < $scope.historyList.length; i++){
            if(!$scope.historyList[i].isDisabled){
                for(j = $scope.historyList[i].start % 24; j < ($scope.historyList[i].end % 24 == 0) ? 24 : $scope.historyList[i].end % 24; j++){
                    if ($scope.schedule.contents[day][j].schedEntry != null){
                        $scope.historyList[i].isDisabled = true;
                        break;
                    }
                }
            }
            
        }
        
        console.log("displaying historyList after generate");
        console.log($scope.historyList);
    };

    $scope.setAvailableSubjects = function () {
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
        }
        console.log("showing isdisableds: ");
        for (i = 0; i < $scope.subjectList.length; i++) {
            console.log($scope.subjectList[i]);
        }
    };

    $scope.setAvailableTime = function (day) {
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
            if ($scope.startList[$scope.selectedStart].isDisabled)
                $scope.selectedStart = null;
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

    $scope.onDayChange = function () {
        $scope.setAvailableTime($scope.selectedDay);
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
            $scope.getHistory($scope.selectedDay);
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    };

    $scope.onStartChange = function () {
        $scope.setAvailableEnd();
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
            $scope.getHistory($scope.selectedDay);
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    };

    $scope.onEndChange = function () {
        if ($scope.selectedStart != null && $scope.selectedEnd != null) {
            $scope.getFreeRooms();
            $scope.getFreeInstructors();
            $scope.getHistory($scope.selectedDay);
        } else {
            $scope.roomList = [];
            $scope.instructorList = [];
        }
    }
    
    $scope.onHistorySelect = function(selectedHistory){
        for(i=0; i<$scope.subjectList.length; i++){
            if($scope.subjectList[i].subjectID == selectedHistory.subject.subjectID){
                $scope.selectedSubject = $scope.subjectList[i];
                break;
            }
        }
        $scope.selectedStart = selectedHistory.start % 24;
        $scope.setAvailableEnd();
//        $scope.selectedEnd = selectedHistory.end % 24;
        $scope.selectedEnd = (selectedHistory.end % 24 == 0) ? 24 : selectedHistory.end % 24;
        $scope.getFreeRoomsForHistory(selectedHistory);
        $scope.getFreeInstructorsForHistory(selectedHistory); 
    }

    $scope.onSubjectChange = function () {
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

    $scope.ok = function () {
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
    };

    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            origSched: $scope.schedEntry
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('wawCancel');
    };

    $scope.getSubjects = function () {

    };
    $scope.initialize();
});

app.controller('schedValidationCtrl', function ($scope, $http, $uibModalInstance, data, schedFactory) {
    $scope.schedEntry = data.cell.schedEntry;
    $scope.index = data.cell.index;
    $scope.span = data.cell.span;
    
    $scope.dayList = [
        {id: 0, text: 'Monday'},
        {id: 1, text: 'Tuesday'},
        {id: 2, text: 'Wednesday'},
        {id: 3, text: 'Thursday'},
        {id: 4, text: 'Friday'}
    ];
    
    $scope.timeList = [
        {id: 0, text: '7:00 AM'},
        {id: 1, text: '7:30 AM'},
        {id: 2, text: '8:00 AM'},
        {id: 3, text: '8:30 AM'},
        {id: 4, text: '9:00 AM'},
        {id: 5, text: '9:30 AM'},
        {id: 6, text: '10:00 AM'},
        {id: 7, text: '10:30 AM'},
        {id: 8, text: '11:00 AM'},
        {id: 9, text: '11:30 AM'},
        {id: 10, text: '12:00 PM'},
        {id: 11, text: '12:30 PM'},
        {id: 12, text: '1:00 PM'},
        {id: 13, text: '1:30 PM'},
        {id: 14, text: '2:00 PM'},
        {id: 15, text: '2:30 PM'},
        {id: 16, text: '3:00 PM'},
        {id: 17, text: '3:30 PM'},
        {id: 18, text: '4:00 PM'},
        {id: 19, text: '4:30 PM'},
        {id: 20, text: '5:00 PM'},
        {id: 21, text: '5:30 PM'},
        {id: 22, text: '6:00 PM'},
        {id: 23, text: '6:30 PM'},
        {id: 24, text: '7:00 PM'}
    ];
    
    $scope.day = Math.floor($scope.index / 24);
    $scope.start = $scope.schedEntry.start % 24;
    $scope.end = ($scope.schedEntry.end % 24 == 0) ? 24 : $scope.schedEntry.end % 24;
    
    $scope.submitt = function () {
        $uibModalInstance.close({
            action: "submit",
            schedId: $scope.schedEntry.schedID
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('scheduler validation modal dismissed');
    };
});