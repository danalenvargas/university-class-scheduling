/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('colregSetup', ['ngRoute', 'ui.bootstrap']);

app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider

                .when('/section', {
                    templateUrl: 'partials/setupSections.html',
                    controller: 'SetupCtrl'
                })

                .when('/instructor', {
                    templateUrl: 'partials/setupInstructors.html',
                    controller: 'SetupCtrl'
                })

                .when('/room', {
                    templateUrl: 'partials/setupRooms.html',
                    controller: 'SetupCtrl'
                })
                
                .when('/curriculum', {
                    templateUrl: 'partials/setupCurriculum.html',
                    controller: 'SetupCtrl'
                })
                
                .when('/course', {
                    templateUrl: 'partials/setupCourses.html',
                    controller: 'SetupCtrl'
                })
                
                .when('/subject', {
                    templateUrl: 'partials/setupSubjects.html',
                    controller: 'SetupCtrl'
                })

                .otherwise({
                    redirectTo: '/section'
                });
    }]);

app.controller('SetupCtrl', function ($scope, $http, $uibModal) {
   $scope.sectionList = [];
   $scope.roomList = [];
   $scope.instructorList = [];
   $scope.subjectList = [];
   $scope.courseList = [];
//   $scope.curriculum = [[[]]];
   $scope.subjectListForCurriculum = [];
   $scope.curriculum;
   $scope.selectedCourse;
   
   // =====================================  SETUP SECTION  =====================================
   
   $scope.getSectionList = function(){
     $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getSectionList"}
        }).success(function (data, status, headers, config) {
            $scope.sectionList = data;
            console.log("success getting sections")
        }).error(function (data, status, headers, config) {
            console.log("error getting sections")
        });  
   };
   
   $scope.openSetupSectionModal = function(selectedSection){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/setupSectionModal.html",
            controller: "SetupSectionCtrl",
            size: "lg",
            resolve: {
                data: selectedSection
            }
        });

        modalInstance.result.then(function (result) {
            if(result.action == "edit"){
                $scope.editSection(result);
            } else if(result.action == "delete"){
                $scope.deleteSection(result.item);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.openAddSectionModal = function(){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/addSectionModal.html",
            controller: "AddSectionCtrl",
            size: "lg",
        });

        modalInstance.result.then(function (result) {
            $scope.addSection(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.addSection = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "addSection", courseId: result.courseId, year: result.year, number: result.number, departmentId: result.departmentId, collegeId: result.collegeId}
        }).success(function (data, status, headers, config) {
            $scope.getSectionList();
            console.log("success in adding section");
        }).error(function (data, status, headers, config) {
            console.log("error in adding section");
        });
   }
   
   $scope.deleteSection = function(section){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "deleteSection", sectionId: section.sectionID, departmentId: section.course.department.departmentID, collegeId: section.course.department.college.collegeID}
        }).success(function (data, status, headers, config) {
            $scope.getSectionList();
            console.log("success in deleting section");
        }).error(function (data, status, headers, config) {
            console.log("error in deleting section");
        });
   }
   
   $scope.editSection = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "editSection", sectionId: result.item.sectionID, newNumber: result.newNumber}
        }).success(function (data, status, headers, config) {
            $scope.getSectionList();
            console.log("success in editing section");
        }).error(function (data, status, headers, config) {
            console.log("error in editing section");
        });
   }
   
   // =====================================  SETUP ROOMS  =====================================
   
    $scope.getRoomList = function(){
     $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getRoomList"}
        }).success(function (data, status, headers, config) {
            $scope.roomList = data;
            console.log("success getting rooms")
        }).error(function (data, status, headers, config) {
            console.log("error getting rooms")
        });  
   };
   
   $scope.openSetupRoomModal = function(selectedRoom){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/setupRoomModal.html",
            controller: "SetupRoomCtrl",
            size: "lg",
            resolve: {
                data: selectedRoom
            }
        });

        modalInstance.result.then(function (result) {
            if(result.action == "edit"){
                $scope.editRoom(result);
            } else if(result.action == "delete"){
                $scope.deleteRoom(result.item);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.openAddRoomModal = function(){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/addRoomModal.html",
            controller: "AddRoomCtrl",
            size: "lg",
        });

        modalInstance.result.then(function (result) {
            $scope.addRoom(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.addRoom = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "addRoom", name: result.roomName, departmentId: result.departmentId, collegeId: result.collegeId}
        }).success(function (data, status, headers, config) {
            $scope.getRoomList();
            console.log("success in adding room");
        }).error(function (data, status, headers, config) {
            console.log("error in adding room");
        });
   }
   
   $scope.deleteRoom = function(room){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "deleteRoom", roomId: room.roomID, departmentId: room.department.departmentID, collegeId: room.department.college.collegeID}
        }).success(function (data, status, headers, config) {
            $scope.getRoomList();
            console.log("success in deleting room");
        }).error(function (data, status, headers, config) {
            console.log("error in deleting room");
        });
   }
   
   $scope.editRoom = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "editRoom", roomId: result.item.roomID, newName: result.newName}
        }).success(function (data, status, headers, config) {
            $scope.getRoomList();
            console.log("success in editing room");
        }).error(function (data, status, headers, config) {
            console.log("error in editing room");
        });
   }
   
   // =====================================  SETUP INSTRUCTORS  =====================================
   
    $scope.getInstructorList = function(){
     $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getInstructorList"}
        }).success(function (data, status, headers, config) {
            $scope.instructorList = data;
            console.log("success getting instructors")
        }).error(function (data, status, headers, config) {
            console.log("error getting instructors")
        });  
   };
   
   $scope.openAddInstructorModal = function(){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/addInstructorModal.html",
            controller: "AddInstructorCtrl",
            size: "lg",
        });

        modalInstance.result.then(function (result) {
            $scope.addInstructor(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.openSetupInstructorModal = function(selectedInstructor){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/setupInstructorModal.html",
            controller: "SetupInstructorCtrl",
            size: "lg",
            resolve: {
                data: selectedInstructor
            }
        });

        modalInstance.result.then(function (result) {
            if(result.action == "edit"){
                $scope.editInstructor(result);
            } else if(result.action == "delete"){
                $scope.deleteInstructor(result.item);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.addInstructor = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "addInstructor", firstName: result.firstName, middleInitial: result.middleInitial, lastName: result.lastName, departmentId: result.departmentId, collegeId: result.collegeId}
        }).success(function (data, status, headers, config) {
            $scope.getInstructorList();
            console.log("success in adding instructor");
        }).error(function (data, status, headers, config) {
            console.log("error in adding instructor");
        });
   }
   
   $scope.deleteInstructor = function(instructor){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "deleteInstructor", instructorId: instructor.instructorID, departmentId: instructor.department.departmentID, collegeId: instructor.department.college.collegeID}
        }).success(function (data, status, headers, config) {
            $scope.getInstructorList();
            console.log("success in deleting instructor");
        }).error(function (data, status, headers, config) {
            console.log("error in deleting instructor");
        });
   }
   
   $scope.editInstructor = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "editInstructor", instructorId: result.item.instructorID, newFirstName: result.newFirstName, newMiddleInitial: result.newMiddleInitial, newLastName: result.newLastName}
        }).success(function (data, status, headers, config) {
            $scope.getInstructorList();
            console.log("success in editing instructor");
        }).error(function (data, status, headers, config) {
            console.log("error in editing instructor");
        });
   }
   
   // =====================================  SETUP SUBJECTS  =====================================
   
    $scope.getSubjectList = function(){
     $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getSubjectList"}
        }).success(function (data, status, headers, config) {
            $scope.subjectList = data;
            console.log("success getting subjects")
        }).error(function (data, status, headers, config) {
            console.log("error getting subjects")
        });  
   };
   
   $scope.openAddSubjectModal = function(){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/addSubjectModal.html",
            controller: "AddSubjectCtrl",
            size: "lg",
        });

        modalInstance.result.then(function (result) {
            $scope.addSubject(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.openSetupSubjectModal = function(selectedSubject){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/setupSubjectModal.html",
            controller: "SetupSubjectCtrl",
            size: "lg",
            resolve: {
                data: selectedSubject
            }
        });

        modalInstance.result.then(function (result) {
            if(result.action == "edit"){
                $scope.editSubject(result);
            } else if(result.action == "delete"){
                $scope.deleteSubject(result.item);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.addSubject = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "addSubject", title: result.title, code: result.code, type: result.type, hours: result.hours, units: result.units , departmentId: result.departmentId}
        }).success(function (data, status, headers, config) {
            $scope.getSubjectList();
            console.log("success in adding subject");
        }).error(function (data, status, headers, config) {
            console.log("error in adding subject");
        });
   }
   
   $scope.deleteSubject = function(subject){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "deleteSubject", subjectId: subject.subjectID}
        }).success(function (data, status, headers, config) {
            $scope.getSubjectList();
            console.log("success in deleting subject");
        }).error(function (data, status, headers, config) {
            console.log("error in deleting subject");
        });
   }
   
   $scope.editSubject = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "editSubject", subjectId: result.item.subjectID, newTitle: result.newTitle, newCode: result.newCode, newType: result.newType, newHours: result.newHours, newUnits: result.newUnits}
        }).success(function (data, status, headers, config) {
            $scope.getSubjectList();
            console.log("success in editing subject");
        }).error(function (data, status, headers, config) {
            console.log("error in editing subject");
        });
   }
   
   // =====================================  SETUP COURSES  =====================================
   
    $scope.getCourseList = function(){
     $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getCourseList"}
        }).success(function (data, status, headers, config) {
            $scope.courseList = data;
            console.log("success getting courses")
        }).error(function (data, status, headers, config) {
            console.log("error getting courses")
        });  
   }; 
   
   $scope.openAddCourseModal = function(){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/addCourseModal.html",
            controller: "AddCourseCtrl",
            size: "lg",
        });

        modalInstance.result.then(function (result) {
            $scope.addCourse(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.openSetupCourseModal = function(selectedCourse){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/setupCourseModal.html",
            controller: "SetupCourseCtrl",
            size: "lg",
            resolve: {
                data: selectedCourse
            }
        });

        modalInstance.result.then(function (result) {
            if(result.action == "edit"){
                $scope.editCourse(result);
            } else if(result.action == "delete"){
                $scope.deleteCourse(result.item);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.addCourse = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "addCourse", name: result.name, code: result.code, departmentId: result.departmentId}
        }).success(function (data, status, headers, config) {
            $scope.getCourseList();
            console.log("success in adding course");
        }).error(function (data, status, headers, config) {
            console.log("error in adding course");
        });
   }
   
   $scope.deleteCourse = function(course){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "deleteCourse", courseId: course.courseID}
        }).success(function (data, status, headers, config) {
            $scope.getCourseList();
            console.log("success in deleting course");
        }).error(function (data, status, headers, config) {
            console.log("error in deleting course");
        });
   }
   
   $scope.editCourse = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "editCourse", courseId: result.item.courseID, newName: result.newName, newCode: result.newCode}
        }).success(function (data, status, headers, config) {
            $scope.getCourseList();
            console.log("success in editing course");
        }).error(function (data, status, headers, config) {
            console.log("error in editing course");
        });
   }
   
   // =====================================  SETUP CURRICULUM  =====================================
   
    $scope.getCurriculum = function(){
     $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getCurriculum", courseId: $scope.selectedCourse.courseID}
        }).success(function (data, status, headers, config) {
            console.log("got curriculum, data:");
            console.log(data);
            $scope.subjectListForCurriculum = data;
            var maxSem = 0;
            for (var i=0;i<data.length;i++){
                if(data[i].sem > maxSem) maxSem = data[i].sem;
            }
            var maxYear = Math.floor((maxSem + 1)/2);
            $scope.curriculum = new Array(maxYear);
            for(var i=0;i<$scope.curriculum.length;i++){
                $scope.curriculum[i] = [3];
                $scope.curriculum[i][0] = [];
                $scope.curriculum[i][1] = [];
                $scope.curriculum[i][2] = [];
            }
            
            for(var i=0;i<data.length;i++){
                var year = Math.ceil((data[i].sem/2));
                if(data[i].sem % 1 != 0) year = year - 1;
                var sem;
                if(data[i].sem % 2 == 0) sem = 2;
                else if(data[i].sem % 2 == 1) sem = 1;
                else sem = 3;
                $scope.curriculum[year-1][sem-1].push(data[i]);
            }
            console.log("curriculum:");
            console.log($scope.curriculum);
            
            console.log("success getting curriculum")
        }).error(function (data, status, headers, config) {
            console.log("error getting curriculum")
        });  
   };
   
   $scope.addYear = function(){
       $scope.curriculum.push([[],[],[]]);
   };
   
   $scope.openAddToCurriculumModal = function(year, sem){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/addToCurriculumModal.html",
            controller: "AddToCurriculumCtrl",
            size: "md",
            resolve:{
                data: {
                    year: year,
                    sem: sem,
                    curriculum: $scope.subjectListForCurriculum
                }
            }
        });

        modalInstance.result.then(function (result) {
            $scope.addToCurriculum(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.openSetupCurriculumModal = function(selectedCurriculumEntry, year, sem){
     var modalInstance = $uibModal.open({
            templateUrl: "partials/setupCurriculumModal.html",
            controller: "SetupCurriculumCtrl",
            size: "md",
            resolve: {
                data: {
                    selectedCurriculumEntry: selectedCurriculumEntry,
                    year: year,
                    sem: sem
                }
            }
        });

        modalInstance.result.then(function (result) {
            if(result.action == "delete"){
                $scope.deleteFromCurriculum(result.item);
            }
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });  
   };
   
   $scope.addToCurriculum = function(result){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "addToCurriculum", subjectId: result.subjectId, sem: result.sem, courseId: $scope.selectedCourse.courseID}
        }).success(function (data, status, headers, config) {
            $scope.getCurriculum();
            console.log("success in adding to curriculum");
        }).error(function (data, status, headers, config) {
            console.log("error in adding to curriculum");
        });
   }
   
   $scope.deleteFromCurriculum = function(curriculumEntry){
       $http({
            method: 'POST',
            url: 'setup',
            params: {action: "deleteFromCurriculum", subjectId: curriculumEntry.subject.subjectID, curriculumId: curriculumEntry.curriculumId}
        }).success(function (data, status, headers, config) {
            $scope.getCurriculum();
            console.log("success in deleting from curriculum");
        }).error(function (data, status, headers, config) {
            console.log("error in deleting from curriculum");
        });
   }
   
   $scope.getSectionList();
   $scope.getRoomList();
   $scope.getInstructorList();
   $scope.getSubjectList();
   $scope.getCourseList();
});

app.controller('SetupSectionCtrl', function ($scope, $http, $uibModalInstance, data){
    console.log(data);
    $scope.selectedSection = data;
    $scope.newSectionNumber = $scope.selectedSection.number;
    $scope.numberList = [
        {id: 1, isDisabled: false},
        {id: 2, isDisabled: false},
        {id: 3, isDisabled: false},
        {id: 4, isDisabled: false},
        {id: 5, isDisabled: false},
        {id: 6, isDisabled: false},
        {id: 7, isDisabled: false},
        {id: 8, isDisabled: false},
        {id: 9, isDisabled: false},
        {id: 10, isDisabled: false},
        {id: 11, isDisabled: false},
        {id: 12, isDisabled: false},
        {id: 13, isDisabled: false},
        {id: 14, isDisabled: false},
        {id: 15, isDisabled: false},
        {id: 16, isDisabled: false},
        {id: 17, isDisabled: false},
        {id: 18, isDisabled: false},
        {id: 19, isDisabled: false},
        {id: 20, isDisabled: false},
        {id: 21, isDisabled: false},
        {id: 22, isDisabled: false},
        {id: 23, isDisabled: false},
        {id: 24, isDisabled: false},
        {id: 25, isDisabled: false},
        {id: 26, isDisabled: false},
        {id: 27, isDisabled: false},
        {id: 28, isDisabled: false},
        {id: 29, isDisabled: false},
        {id: 30, isDisabled: false},
    ];
    
    $scope.ok = function () {
        $uibModalInstance.close({
            action: "edit",
            type: "section",
            item: $scope.selectedSection,
            newNumber: $scope.newSectionNumber
        });
    };
    
    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            type: "section",
            item: $scope.selectedSection
        });
    };
    
    $scope.setNumberList = function(){
        $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getTakenNumbersList", courseId: $scope.selectedSection.course.courseID, year: $scope.selectedSection.year}
        }).success(function (data, status, headers, config) {
           console.log("got takenNumberlist:");
           console.log(data);
            for(i=0; i < data.length; i++){
                for(j=0; j < $scope.numberList.length; j++){
                    if(data[i] == $scope.numberList[j].id && $scope.numberList[j].id != $scope.selectedSection.number){
                        $scope.numberList[j].isDisabled = true;
                    }
                }
            }
            console.log("success getting numbersList")
        }).error(function (data, status, headers, config) {
            console.log("error getting numbersList")
        });  
    }

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.setNumberList();
});

app.controller('AddSectionCtrl', function ($scope, $http, $uibModalInstance){
    $scope.selectedCourse;
    $scope.selectedYear;
    $scope.selectedNumber;
    $scope.courseList = [];
    $scope.numberList = [
        {id: 1, isDisabled: false},
        {id: 2, isDisabled: false},
        {id: 3, isDisabled: false},
        {id: 4, isDisabled: false},
        {id: 5, isDisabled: false},
        {id: 6, isDisabled: false},
        {id: 7, isDisabled: false},
        {id: 8, isDisabled: false},
        {id: 9, isDisabled: false},
        {id: 10, isDisabled: false},
        {id: 11, isDisabled: false},
        {id: 12, isDisabled: false},
        {id: 13, isDisabled: false},
        {id: 14, isDisabled: false},
        {id: 15, isDisabled: false},
        {id: 16, isDisabled: false},
        {id: 17, isDisabled: false},
        {id: 18, isDisabled: false},
        {id: 19, isDisabled: false},
        {id: 20, isDisabled: false},
        {id: 21, isDisabled: false},
        {id: 22, isDisabled: false},
        {id: 23, isDisabled: false},
        {id: 24, isDisabled: false},
        {id: 25, isDisabled: false},
        {id: 26, isDisabled: false},
        {id: 27, isDisabled: false},
        {id: 28, isDisabled: false},
        {id: 29, isDisabled: false},
        {id: 30, isDisabled: false},
    ];
    
    $scope.getCourseList = function(){
        $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getCourseList"}
        }).success(function (data, status, headers, config) {
            $scope.courseList = data;
            console.log("success getting courses")
            console.log(data);
        }).error(function (data, status, headers, config) {
            console.log("error getting courses")
        });  
    }
    
    $scope.setNumberList = function(){
        if($scope.selectedYear != null && $scope.selectedCourse != null){
            $http({
                method: 'GET',
                url: 'setup',
                params: {action: "getTakenNumbersList", courseId: $scope.selectedCourse.courseID, year: $scope.selectedYear}
            }).success(function (data, status, headers, config) {
               console.log("got takenNumberlist:");
               console.log(data);
                for(i=0; i < data.length; i++){
                    for(j=0; j < $scope.numberList.length; j++){
                        if(data[i] == $scope.numberList[j].id){
                            $scope.numberList[j].isDisabled = true;
                        }
                    }
                }
                console.log("success getting numbersList")
            }).error(function (data, status, headers, config) {
                console.log("error getting numbersList")
            });  
        }
    }
    
    $scope.ok = function () {
        if($scope.selectedCourse != null && $scope.selectedYear != null && $scope.selectedNumber != null){
            $uibModalInstance.close({
                action: "add",
                type: "section",
                courseId: $scope.selectedCourse.courseID,
                year: $scope.selectedYear,
                number: $scope.selectedNumber,
                departmentId: $scope.selectedCourse.department.departmentID,
                collegeId: $scope.selectedCourse.department.college.collegeID
            });
        }else{
            alert("Please fill out all the data.");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getCourseList();
});

app.controller('AddRoomCtrl', function ($scope, $http, $uibModalInstance){
    $scope.departmentList = [];
    $scope.selectedDepartment;
    $scope.roomName = "";
    
    $scope.getDepartmentList = function(){
        $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getDepartmentList"}
        }).success(function (data, status, headers, config) {
            $scope.departmentList = data;
            console.log("success getting departments")
            console.log(data);
        }).error(function (data, status, headers, config) {
            console.log("error getting departments")
        });  
    }
    
    $scope.ok = function () {
        if($scope.selectedDepartment != null && $scope.roomName != ""){
            $uibModalInstance.close({
                action: "add",
                roomName: $scope.roomName,
                departmentId: $scope.selectedDepartment.departmentID,
                collegeId: $scope.selectedDepartment.college.collegeID
            });
        }else{
            alert("Please fill out all the data.");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getDepartmentList();
});

app.controller('SetupRoomCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.selectedRoom = data;
    $scope.newRoomName = $scope.selectedRoom.name;
    
    $scope.ok = function () {
        $uibModalInstance.close({
            action: "edit",
            item: $scope.selectedRoom,
            newName: $scope.newRoomName
        });
    };
    
    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            type: "room",
            item: $scope.selectedRoom
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
});

app.controller('AddInstructorCtrl', function ($scope, $http, $uibModalInstance){
    $scope.departmentList = [];
    $scope.selectedDepartment;
    $scope.firstName = "";
    $scope.middleInitial = "";
    $scope.lastName = "";
    
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
    }
    
    $scope.ok = function () {
        if($scope.selectedDepartment != null && $scope.firstName != "" && $scope.middleInitial != "" && $scope.lastName != ""){
            $uibModalInstance.close({
                action: "add",
                firstName: $scope.firstName,
                middleInitial: $scope.middleInitial,
                lastName: $scope.lastName,
                departmentId: $scope.selectedDepartment.departmentID,
                collegeId: $scope.selectedDepartment.college.collegeID
            });
        }else{
            alert("Please fill out all the data.");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getDepartmentList();
});

app.controller('SetupInstructorCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.selectedInstructor = data;
    $scope.newFirstName = $scope.selectedInstructor.firstName;
    $scope.newMiddleInitial = $scope.selectedInstructor.middleInitial;
    $scope.newLastName = $scope.selectedInstructor.lastName;
    
    $scope.ok = function () {
        $uibModalInstance.close({
            action: "edit",
            item: $scope.selectedInstructor,
            newFirstName: $scope.newFirstName,
            newMiddleInitial: $scope.newMiddleInitial,
            newLastName: $scope.newLastName
        });
    };
    
    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            type: "instructor",
            item: $scope.selectedInstructor
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
});

app.controller('AddSubjectCtrl', function ($scope, $http, $uibModalInstance){
    $scope.departmentList = [];
    $scope.selectedDepartment;
    $scope.title = "";
    $scope.code = "";
    $scope.selectedType;
    $scope.hours;
    $scope.units;
    
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
    }
    
    $scope.ok = function () {
        if($scope.selectedDepartment != null && $scope.hours != null && $scope.units != null && $scope.selectedType != null && $scope.title != "" && $scope.code != ""){
            $uibModalInstance.close({
                action: "add",
                title: $scope.title,
                code: $scope.code,
                type: $scope.selectedType,
                hours: $scope.hours,
                units: $scope.units,
                departmentId: $scope.selectedDepartment.departmentID,
                collegeId: $scope.selectedDepartment.college.collegeID
            });
        }else{
            alert("Please fill out all the data.");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getDepartmentList();
});

app.controller('SetupSubjectCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.selectedSubject = data;
    $scope.newTitle =  $scope.selectedSubject.title;
    $scope.newCode = $scope.selectedSubject.code;
    $scope.newType = $scope.selectedSubject.type;
    $scope.newHours = $scope.selectedSubject.hours;
    $scope.newUnits = $scope.selectedSubject.units;
    
    $scope.ok = function () {
        $uibModalInstance.close({
            action: "edit",
            item: $scope.selectedSubject,
            newTitle: $scope.newTitle,
            newCode: $scope.newCode,
            newType: $scope.newType,
            newHours: $scope.newHours,
            newUnits: $scope.newUnits,
        });
    };
    
    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            type: "subject",
            item: $scope.selectedSubject
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
});

app.controller('AddCourseCtrl', function ($scope, $http, $uibModalInstance){
    $scope.departmentList = [];
    $scope.selectedDepartment;
    $scope.name = "";
    $scope.code = "";
    
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
    }
    
    $scope.ok = function () {
        if($scope.selectedDepartment != null && $scope.name != "" && $scope.code != ""){
            $uibModalInstance.close({
                action: "add",
                name: $scope.name,
                code: $scope.code,
                departmentId: $scope.selectedDepartment.departmentID
            });
        }else{
            alert("Please fill out all the data.");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getDepartmentList();
});

app.controller('SetupCourseCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.selectedCourse = data;
    $scope.newName =  $scope.selectedCourse.name;
    $scope.newCode = $scope.selectedCourse.code;
    
    $scope.ok = function () {
        $uibModalInstance.close({
            action: "edit",
            item: $scope.selectedCourse,
            newName: $scope.newName,
            newCode: $scope.newCode,
        });
    };
    
    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            type: "course",
            item: $scope.selectedCourse
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
});

app.controller('AddToCurriculumCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.year = data.year;
    $scope.sem = data.sem;
    $scope.curriculum = data.curriculum;
    $scope.semString = "";
    $scope.subjectList = [];
    $scope.selectedSuject;
//    console.log("at modal, data: ");
//    console.log(data);
    
    if($scope.sem == 1) $scope.semString = "First Semester";
    else if($scope.sem == 2) $scope.semString = "Second Semester";
    else if($scope.sem == 3) $scope.semString = "Summer";
    
    $scope.getAllSubjects = function(){
        $http({
            method: 'GET',
            url: 'setup',
            params: {action: "getAllSubjects"}
        }).success(function (data, status, headers, config) {
            //$scope.subjectList = data;
            var taken = false;
            for(i=0;i<data.length;i++){
                taken = false;
                for(j=0;j<$scope.curriculum.length;j++){
//                    console.log("comparing......");
//                    console.log(data[i].subjectID)
//                    console.log($scope.curriculum[j].subjectID)
                    if(data[i].subjectID == $scope.curriculum[j].subject.subjectID){
                        taken = true;
                        break;
                    }
                }
                if(taken == false) $scope.subjectList.push(data[i]);
            }
            console.log("success getting subjects")
        }).error(function (data, status, headers, config) {
            console.log("error getting subjects")
        });  
    }
    
    $scope.ok = function () {
        var trueSem = $scope.year*2;  // sem value to be stored in database
        if($scope.sem == 1) trueSem = trueSem - 1;
        if($scope.sem == 3) trueSem = trueSem + 0.5;
        if($scope.selectedSubject != null){
            $uibModalInstance.close({
                action: "add",
                subjectId: $scope.selectedSubject.subjectID,
                sem: trueSem
            });
        }else{
            alert("Please choose a subject.");
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getAllSubjects();
});

app.controller('SetupCurriculumCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.year = data.year;
    $scope.sem = data.sem;
    $scope.curriculumEntry = data.selectedCurriculumEntry;
    $scope.selectedSubject = $scope.curriculumEntry.subject;
    $scope.semString = "";
    
    if($scope.sem == 1) $scope.semString = "First Semester";
    else if($scope.sem == 2) $scope.semString = "Second Semester";
    else if($scope.sem == 3) $scope.semString = "Summer";
    
//    $scope.ok = function () {
//        $uibModalInstance.close({
//            action: "edit",
//            item: $scope.selectedCourse,
//            newName: $scope.newName,
//            newCode: $scope.newCode,
//        });
//    };
    
    $scope.delete = function () {
        $uibModalInstance.close({
            action: "delete",
            type: "curriculumEntry",
            item: $scope.curriculumEntry
        });
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
});