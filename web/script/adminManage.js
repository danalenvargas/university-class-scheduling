/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('AdminManage', ['ui.bootstrap']);

app.controller('AdminManageCtrl', function ($scope, $http, $uibModal) {
    $scope.activeSem;
    
    $scope.getActiveSem = function(){
      $http({
            method: 'GET',
            url: 'AdminManage',
            params: {action: "getActiveSem"}
        }).success(function (data, status, headers, config) {
            $scope.activeSem = data;
            if($scope.activeSem.sem == 1) $scope.activeSem.semString = "1st Semester";
            else if($scope.activeSem.sem == 2) $scope.activeSem.semString = "2nd Semester";
            else if($scope.activeSem.sem == 3) $scope.activeSem.semString = "Summer";
            console.log("success in getting activeSem");
            console.log(data);
        }).error(function (data, status, headers, config) {
            console.log("error in getting activeSem");
        });  
    };
    
    $scope.openChangeSemModal = function(){
        var modalInstance = $uibModal.open({
            templateUrl: "partials/changeSemModal.html",
            controller: "ChangeSemCtrl",
            size: "md",
        });

        modalInstance.result.then(function (result) {
            $scope.changeSem(result);
        },function(){
            console.log('Modal dismissed at: ' + new Date());
        });  
    };
    
    $scope.changeSem = function(semId){
        $http({
            method: 'POST',
            url: 'AdminManage',
            params: {action: "changeActiveSem", semId: semId}
        }).success(function (data, status, headers, config) {
            $scope.getActiveSem();
            console.log("success in changing activeSem");
        }).error(function (data, status, headers, config) {
            console.log("error in changing activeSem");
        });
    };
    
    $scope.toggleIsFinalized = function(){
        $http({
            method: 'POST',
            url: 'AdminManage',
            params: {action: "toggleIsFinalized", semId: $scope.activeSem.semId, isFinalized: $scope.activeSem.isFinalized}
        }).success(function (data, status, headers, config) {
            $scope.getActiveSem();
            console.log("success in changing IsFinalized");
        }).error(function (data, status, headers, config) {
            console.log("error in changing IsFinalized");
        });
    };
    
    $scope.getActiveSem();
});

app.controller('ChangeSemCtrl', function ($scope, $http, $uibModalInstance, $uibModal){
    $scope.semList = [];
    $scope.selectedSem;
    
    $scope.getSemList = function(semIdToSet){
        $http({
            method: 'GET',
            url: 'AdminManage',
            params: {action: "getSemList"}
        }).success(function (data, status, headers, config) {
            $scope.semList = data;
            for(var i=0; i<$scope.semList.length; i++){
                if($scope.semList[i].sem == 1) $scope.semList[i].semString = "1st Semester";
                else if($scope.semList[i].sem == 2) $scope.semList[i].semString = "2nd Semester";
                else if($scope.semList[i].sem == 3) $scope.semList[i].semString = "Summer";
                if(semIdToSet != null && $scope.semList[i].semId == semIdToSet){
                    $scope.selectedSem = $scope.semList[i];
                }
            };
            console.log("success getting semList");
            console.log(data);
        }).error(function (data, status, headers, config) {
            console.log("error getting semList")
        });  
    }
    
    $scope.openAddSemModal = function(){
        var modalInstance = $uibModal.open({
            templateUrl: "partials/addSemModal.html",
            controller: "AddSemCtrl",
            size: "md",
        });

        modalInstance.result.then(function (result) {
            $scope.addSem(result);
        },function(){
            console.log('Modal dismissed at: ' + new Date());
        });  
    };
    
    $scope.addSem = function(result){
        $http({
            method: 'POST',
            url: 'AdminManage',
            params: {action: "addSem", semNum: result.semNum, schoolYear: result.schoolYear}
        }).success(function (data, status, headers, config) {
            var createdSemId = data;
            $scope.getSemList(createdSemId);
            console.log("success adding sem")
        }).error(function (data, status, headers, config) {
            console.log("error adding sem")
        });  
    };
    
    $scope.ok = function () {
        if($scope.selectedSem != null){
            $uibModalInstance.close($scope.selectedSem.semId);
        }else{
            alert("Please select a semester");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getSemList();
});

app.controller('AddSemCtrl', function ($scope, $http, $uibModalInstance){
    $scope.schoolYear;
    $scope.semNum;
    
    $scope.ok = function () {
        if($scope.schoolYear!=null && $scope.semNum!=null){
            $uibModalInstance.close({
                schoolYear: $scope.schoolYear,
                semNum: $scope.semNum
            });
        }else{
            alert("Please fill out all data");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
});