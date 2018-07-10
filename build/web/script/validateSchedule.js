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

app.controller('SchedCtrl', function ($scope, $http, $location, $uibModal, schedFactory) {
    $scope.schedule = schedFactory.getSchedule();
    $scope.views = ["section", "room", "instructor"];
    $scope.selectedView = "section";
    $scope.selectionList = [];
    $scope.currentUser;
    $scope.schedList;

    $scope.update = function () {
        var counter = 0;
        var schedule = [];
        var heightPerCell = Math.floor($scope.schedule.height / $scope.schedule.rows);
        var status;
        var bgColor="White";
        var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
        var isSchedCreatedInMyCollege;

        if ($scope.schedList!=null && $scope.schedList[counter] != null) {
            var day = Math.floor($scope.schedList[counter].start / 24);
            var timeStart = $scope.schedList[counter].start % 24;
            var timeEnd = ($scope.schedList[counter].end % 24 == 0) ? 24 : $scope.schedList[counter].end % 24;
            for (var i = 0; i < $scope.schedule.cols; i++) {
                var a = [];
                for (var j = 0; j < $scope.schedule.rows; j++) {
                    if (day === i && timeStart === j) {
                        status = $scope.schedList[counter].status;
                        isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == $scope.schedList[counter].creator.collegeID);
                        console.log("determining color: " + $scope.currentUser.userType + " " + $scope.schedList[counter].creator.userID + " " + $scope.currentUser.userID + " " + isSchedCreatedInMyCollege);
                        if (($scope.currentUser.userType == "SCHEDULER" && $scope.schedList[counter].creator.userID != $scope.currentUser.userID) || (!isSchedCreatedInMyCollege && !isSelectedItemMine) || (!isSchedCreatedInMyCollege && $scope.schedList[counter].status !== "locked" && $scope.schedList[counter].status !== "approved")) bgColor="#B7B8B6";
                        else if(status == "unsubmitted") bgColor="Ivory";
                        else if(status == "submitted") bgColor="#89DA59";
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
        console.log("AFTER UPDATE, schedList: ");
        console.log($scope.schedList);
        return $scope.schedule.contents;
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
        console.log("changing view: " + $scope.selectedView);
        $location.path($scope.selectedView);
        console.log("selected item: " + $scope.selectedItem.id + $scope.selectedItem.departmentId + $scope.selectedItem.collegeId);
        $http({
            method: 'GET',
            url: 'SchedTable',
            params: {function: "getSched", type: $scope.selectedView, itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
        }).success(function (data, status, headers, config) {
            console.log("GETTING SCHED:!: type: " + $scope.selectedView + " item: " + $scope.selectedItem.name + $scope.selectedItem.id + $scope.selectedItem.departmentId + $scope.selectedItem.collegeId);
            console.log("testing 1");
            console.log(data);
            console.log("testing 2");
            $scope.schedList = data;
            console.log($scope.schedList);
            console.log("testing 3");
            $scope.update($scope.schedList);
        }).error(function (data, status, headers, config) {
            console.log("ERROR GETTING SCHEDULES");
        });
        if ($scope.selectedView == "section") {
            $http({
                method: 'GET',
                url: 'SchedTable',
                params: {function: "getSubjectList", itemId: $scope.selectedItem.id, departmentId: $scope.selectedItem.departmentId, collegeId: $scope.selectedItem.collegeId}
            }).success(function (data, status, headers, config) {
                $scope.subjectList = data;
                console.log("AFTER GETTING SUBJECTLIST, schedList: ");
            console.log($scope.schedList);
            }).error(function (data, status, headers, config) {
                console.log("ERROR GETTING SUBJECTLIST");
            });
        }
    };

    
    
    $scope.openSchedulerValidationModal = function (selectedCell) {
        if(selectedCell.schedEntry != null && selectedCell.schedEntry.creator.userID == $scope.currentUser.userID){
            var modalInstance = $uibModal.open({
                templateUrl: "partials/schedulerValidationModal.html",
                controller: "schedValidationModalCtrl",
                size: "lg",
                resolve: {
                    data: {cell: selectedCell}
                }
            });

            modalInstance.result.then(function (result) {
                $scope.validateSchedule(result);
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        }
    };
    
    $scope.openColregValidationModal = function (selectedCell) {
        if(selectedCell.schedEntry != null){
            var modalInstance = $uibModal.open({
                templateUrl: "partials/colregValidationModal.html",
                controller: "colregValidationModalCtrl",
                size: "lg",
                resolve: {
                    data: {cell: selectedCell,
                           currentUser: $scope.currentUser,
                           selectedItem: $scope.selectedItem}
                }
            });

            modalInstance.result.then(function (result) {
                $scope.validateSchedule(result);
            }, function () {
                console.log('Modal dismissed at: ' + new Date());
            });
        }
    };
    
    $scope.submitAll = function(){
        console.log("STARTING SUBMITALL");
        console.log($scope.schedList);
        var tempList = $scope.schedList;
//        for(var i=0; i< $scope.schedList.length; i++){
//            console.log("AT submitAll, comparing: " + i + " " + $scope.schedList[i].creatorId + " " + $scope.currentUser.userID + " " + $scope.schedList[i].status + " ");
//            console.log($scope.schedList[i]);
//            if($scope.schedList[i].creatorId == $scope.currentUser.userID && $scope.schedList[i].status == "unsubmitted"){
//                console.log("true, validating");
//                $scope.validateSchedule({
//                    action: "validate",
//                    newStatus: "submitted",
//                    schedId: $scope.schedList[i].schedID
//                });
//            }
//        }
        for(var i=0; i< tempList.length; i++){
            console.log("AT submitAll, comparing: " + i + " " + tempList[i].creator + " " + $scope.currentUser.userID + " " + tempList[i].status + " ");
            console.log(tempList[i]);
            if(tempList[i].creator.userID == $scope.currentUser.userID && tempList[i].status == "unsubmitted"){
//        if(tempList[i].creator.userID == $scope.currentUser.userID){
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
//        for(var i=0; i< $scope.schedList.length; i++){
//            if($scope.schedList[i].creatorId == $scope.currentUser.userID && $scope.schedList[i].status == "submitted"){
//                console.log("true, validating");
//                $scope.validateSchedule({
//                    action: "validate",
//                    newStatus: "unsubmitted",
//                    schedId: $scope.schedList[i].schedID
//                });
//            }
//        }
        for(var i=0; i< tempList.length; i++){
            console.log("AT UN-submitAll, comparing: " + i + " " + tempList[i].creator.userID + " " + $scope.currentUser.userID + " " + tempList[i].status + " ");
            console.log(tempList[i]);
            if(tempList[i].creator.userID == $scope.currentUser.userID && tempList[i].status == "submitted"){
//        if(tempList[i].creator.userID == $scope.currentUser.userID){
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
            for(var i=0; i < $scope.schedList.length; i++){
                console.log("comparing... : " + $scope.schedList[i].schedID + " " + result.schedId);
                if($scope.schedList[i].schedID == result.schedId){
                    console.log("found! changing to " + result.newStatus);
                    $scope.schedList[i].status = result.newStatus;
                }
            }
            $scope.update($scope.schedList);
        }).error(function (data, status, headers, config) {
            console.log("ERROR IN Validation");
        });  
    };
    
    $scope.getCurrentUser = function(){
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
    
    $scope.getCurrentUser();
    $scope.onViewChange($scope.selectedView);
});

app.controller('schedValidationModalCtrl', function ($scope, $http, $uibModalInstance, data, schedFactory) {
    $scope.schedEntry = data.cell.schedEntry;
    $scope.index = data.cell.index;
    $scope.span = data.cell.span;
    $scope.isSubmittedBtnDisabled = false;
    $scope.isUnsubmittedBtnDisabled = false;
    
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
    
    $scope.initialize = function () {
        if($scope.schedEntry.status === "submitted") $scope.isSubmittedBtnDisabled = true;
        else if($scope.schedEntry.status === "unsubmitted") $scope.isUnsubmittedBtnDisabled = true;
        else{
            $scope.isSubmittedBtnDisabled = true;
            $scope.isUnsubmittedBtnDisabled = true;
        }
        
        console.log("buttons disabled? : " + $scope.isSubmittedBtnDisabled + $scope.isUnsubmittedBtnDisabled);
    };
    
    $scope.submit = function () {
        $uibModalInstance.close({
            action: "validate",
            newStatus: "submitted",
            schedId: $scope.schedEntry.schedID
        });
    };
    
    $scope.unsubmit = function () {
        $uibModalInstance.close({
            action: "validate",
            newStatus: "unsubmitted",
            schedId: $scope.schedEntry.schedID
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('scheduler validation modal dismissed');
    };
    
    $scope.initialize();
});

app.controller('colregValidationModalCtrl', function ($scope, $http, $uibModalInstance, data, schedFactory) {
    $scope.schedEntry = data.cell.schedEntry;
    $scope.index = data.cell.index;
    $scope.span = data.cell.span;
    $scope.isReturnBtnDisabled = false;
    $scope.isLockBtnDisabled = false;
    $scope.isApproveBtnDisabled = false;
    $scope.currentUser = data.currentUser;
    $scope.selectedItem = data.selectedItem;
    
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
    
    $scope.initialize = function () {
        var isSelectedItemMine = ($scope.selectedItem.collegeId == $scope.currentUser.collegeID);
        var isSchedCreatedByMe = ($scope.currentUser.userID == $scope.schedEntry.creator.userID);
        var isSchedCreatedInMyCollege = ($scope.currentUser.collegeID == $scope.schedEntry.creator.collegeID);
        
//        ======
        if($scope.schedEntry.status === "approved") $scope.isApproveBtnDisabled = true;
        else if($scope.schedEntry.status === "locked") $scope.isLockBtnDisabled = true;
        else if($scope.schedEntry.status === "unsubmitted"){
            $scope.isReturnBtnDisabled = true;
            $scope.isLockBtnDisabled = true;
            $scope.isApproveBtnDisabled = true;
        }
//        =====
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
            }
        }
    };
    
    $scope.return = function () {
        var newStatus;
        if ($scope.schedEntry.creator.userType == "SCHEDULER") newStatus = "unsubmitted";
        else newStatus = "submitted";
        $uibModalInstance.close({
            action: "return",
            newStatus: newStatus,
            schedId: $scope.schedEntry.schedID
        });
    };
    
    $scope.lock = function () {
        $uibModalInstance.close({
            action: "validate",
            newStatus: "locked",
            schedId: $scope.schedEntry.schedID
        });
    };
    
    $scope.approve = function () {
        $uibModalInstance.close({
            action: "validate",
            newStatus: "approved",
            schedId: $scope.schedEntry.schedID
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('scheduler validation modal dismissed');
    };
    
    $scope.initialize();
});