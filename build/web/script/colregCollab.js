/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var app = angular.module('colregCollab', ['ivh.treeview', 'ui.bootstrap']);

app.config(function (ivhTreeviewOptionsProvider) {
    ivhTreeviewOptionsProvider.set({
        defaultSelectedState: false,
        validate: true,
        expandToDepth: 1
    });
});

app.filter('range', function () {
    return function(input, min, max) {
        min = parseInt(min); //Make string input int
        max = parseInt(max);
        for (var i=min; i<max; i++)
          input.push(i);
        return input;
    };
});

app.controller('collabCtrl', function ($scope, $http, $uibModal, ivhTreeviewBfs, $interval) {
    $scope.sharedSectionsList = [];
    $scope.borrowedSectionsList = [];
    $scope.sharedRoomsList = [];
    $scope.borrowedRoomsList = [];
//    $scope.numOfDays = 10;
    $scope.notificationsList = [];
    
    $scope.chatListForDropdown = [];
    $scope.chatSessionList = [];
    $scope.messages = [];
    $scope.members = [];
    $scope.selectedDropdownChat;
    $scope.selectedChat;
//    $scope.txtMessage="";
    $scope.data = {numOfDays: 10};
    $scope.shouldCheck = false;
    
    
//    $(function () {
//        $('.nav-tabs a').click(function(){
//            $(this).tab('show');
//        })
//    });
    
    $scope.shareSections = function (result) {
        console.log("Sharing sections... selected sections:");
        console.log(result.selectedSections);
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "shareSection", otherUserId: result.selectedUser.id, selectedEntries: result.selectedSections}
        }).success(function (data, status, headers, config) {
            //$scope.sharedToList.push(user);
            $scope.getSharedSectionsList();
            console.log("Successfully shared sections");
        }).error(function (data, status, headers, config) {
            console.log("Error sharing sections");
        });
    };
    
    $scope.shareRooms = function (result) {
        console.log("Sharing rooms... selected rooms:");
        console.log(result.selectedSections);
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "shareRoom", otherUserId: result.selectedUser.id, selectedEntries: result.selectedRooms}
        }).success(function (data, status, headers, config) {
            $scope.getSharedRoomsList();
            console.log("Successfully shared rooms");
        }).error(function (data, status, headers, config) {
            console.log("Error sharing rooms");
        });
    };

    $scope.pullSections = function (result) {
        console.log("Pulling, selectedEntries:");
        console.log(result);
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "pullSection", selectedEntries: JSON.stringify(result)}
        }).success(function (data, status, headers, config) {
            $scope.getSharedSectionsList();
            console.log("Successfully pulled sections");
        }).error(function (data, status, headers, config) {
            console.log("Error pulling sections");
        });
    };
    
    $scope.pullRooms = function (result) {
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "pullRoom", selectedEntries: JSON.stringify(result)}
        }).success(function (data, status, headers, config) {
            $scope.getSharedRoomsList();
            console.log("Successfully pulled rooms");
        }).error(function (data, status, headers, config) {
            console.log("Error pulling rooms");
        });
    };

    $scope.returnSections = function (result) {
        console.log("Returning, selectedEntries:");
        console.log(result);
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "returnSection", selectedEntries: JSON.stringify(result)},
        }).success(function (data, status, headers, config) {
            $scope.getBorrowedSectionsList();
            console.log("Successfully returned sections");
        }).error(function (data, status, headers, config) {
            console.log("Error returning sections");
        });
    };
    
    $scope.returnRooms = function (result) {
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "returnRoom", selectedEntries: JSON.stringify(result)},
        }).success(function (data, status, headers, config) {
            $scope.getBorrowedRoomsList();
            console.log("Successfully returned rooms");
        }).error(function (data, status, headers, config) {
            console.log("Error returning rooms");
        });
    };

    $scope.getSelectedBoxes = function (tree) {
        var selectedNodes = [];
        ivhTreeviewBfs(tree, function (node) {
            if (node.selected && !node.children) {
                selectedNodes.push({
                    entryId: node.value.id,
                    otherUserId: node.value.otherUserId
                });
            }
        });
        console.log("selectedNodes");
        console.log(selectedNodes);
        return selectedNodes;
    };
    
    $scope.getNotifications = function(){
        console.log("inside getNotifications, numOfDays: " + $scope.data.numOfDays);
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getNotifications", days: $scope.data.numOfDays}
        }).success(function (data, status, headers, config) {
            $scope.notificationsList = data;
            for(i=0;i<$scope.notificationsList.length;i++){
                if($scope.notificationsList[i].action == "share"){
                    $scope.notificationsList[i].displayString = $scope.notificationsList[i].timestamp + " - " + $scope.notificationsList[i].fromUser.userName + " shared " + $scope.notificationsList[i].type + "s to you";
                }else if($scope.notificationsList[i].action == "pull"){
                    $scope.notificationsList[i].displayString = $scope.notificationsList[i].timestamp + " - " + $scope.notificationsList[i].fromUser.userName + " pulled " + $scope.notificationsList[i].type + "s from you";
                }else if($scope.notificationsList[i].action == "return"){
                    $scope.notificationsList[i].displayString = $scope.notificationsList[i].timestamp + " - " + $scope.notificationsList[i].fromUser.userName + " returned " + $scope.notificationsList[i].type + "s to you";
                }
            }
            console.log("success in getting notifications:");
            console.log($scope.notificationsList);
        }).error(function (data, status, headers, config) {
            console.log("error in getting notifications");
        });
    };
    
    $scope.openNotificationModal = function (notification) {
        var modalInstance = $uibModal.open({
//            templateUrl: "partials/notificationModal.html",
            templateUrl: "notificationModal.html",
            controller: "NotificationModalCtrl",
            size: "md",
            resolve:{
                data: notification
            }
        });

        modalInstance.result.then(function (result) {
            
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };

    $scope.getSharedSectionsList = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getSharedSections"}
        }).success(function (data, status, headers, config) {
            $scope.sharedSectionsList = data;
            console.log("GOT SHARED SECTIONS");
            console.log($scope.sharedSectionsList);
        }).error(function (data, status, headers, config) {
        });
    };

    $scope.getBorrowedSectionsList = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getBorrowedSections"}
        }).success(function (data, status, headers, config) {
            $scope.borrowedSectionsList = data;
        }).error(function (data, status, headers, config) {
        });
    };

    $scope.getSharedRoomsList = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getSharedRooms"}
        }).success(function (data, status, headers, config) {
            $scope.sharedRoomsList = data;
            console.log("GOT SHARED ROOMS");
            console.log($scope.sharedRoomsList);
        }).error(function (data, status, headers, config) {
        });
    };

    $scope.getBorrowedRoomsList = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getBorrowedRooms"}
        }).success(function (data, status, headers, config) {
            $scope.borrowedRoomsList = data;
        }).error(function (data, status, headers, config) {
        });
    };

    $scope.openShareSectionModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "shareSectionModal.html",
            controller: "shareSectionModalCtrl",
            size: "md"
        });

        modalInstance.result.then(function (result) {
            $scope.shareSections(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.openShareRoomModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "shareRoomModal.html",
            controller: "shareRoomModalCtrl",
            size: "md"
        });

        modalInstance.result.then(function (result) {
            $scope.shareRooms(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };

    $scope.openPullSectionModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "pullSectionModal.html",
            controller: "ModalCtrl",
            size: "sm"
        });

        modalInstance.result.then(function (result) {
            result = $scope.getSelectedBoxes($scope.sharedSectionsList);
            $scope.pullSections(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.openPullRoomModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "pullRoomModal.html",
            controller: "ModalCtrl",
            size: "sm"
        });

        modalInstance.result.then(function (result) {
            result = $scope.getSelectedBoxes($scope.sharedRoomsList);
            $scope.pullRooms(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };

    $scope.openReturnSectionModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "returnSectionModal.html",
            controller: "ModalCtrl",
            size: "sm"
        });

        modalInstance.result.then(function (result) {
            result = $scope.getSelectedBoxes($scope.borrowedSectionsList);
            $scope.returnSections(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.openReturnRoomModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "returnRoomModal.html",
            controller: "ModalCtrl",
            size: "sm"
        });

        modalInstance.result.then(function (result) {
            result = $scope.getSelectedBoxes($scope.borrowedRoomsList);
            $scope.returnRooms(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.openCreateGroupChatModal = function () {
        var modalInstance = $uibModal.open({
            templateUrl: "createGroupChatModal.html",
            controller: "createGroupChatModalCtrl",
            size: "sm"
        });

        modalInstance.result.then(function (result) {
            $scope.createGroupChat(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.openInviteModal = function(){
        var modalInstance = $uibModal.open({
            templateUrl: "partials/inviteModal.html",
            controller: "inviteModalCtrl",
            size: "md",
            resolve: {
                data:$scope.selectedChat.id
            }
        });
        modalInstance.result.then(function (result) {
            $scope.addGroupChatMember(result);
        }, function () {
            console.log('Modal dismissed at: ' + new Date());
        });
    };
    
    $scope.addGroupChatMember = function(userId){
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "addGroupChatMember", chatId: $scope.selectedChat.id, userId: userId}
        }).success(function (data, status, headers, config) {
            $scope.getMembers();
            console.log("success in addGroupChatMember");
        }).error(function (data, status, headers, config) {
            console.log("error in addGroupChatMember");
        });
    };
    
    $scope.getChatListForDropdown = function(){
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getChatListForDropdown"}
        }).success(function (data, status, headers, config) {
            $scope.chatListForDropdown = data;
            console.log("success in getting ChatListForDropdown");
        }).error(function (data, status, headers, config) {
            console.log("error in getting ChatListForDropdown");
        });
    };
    
    $scope.createGroupChat = function(name){
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "createGroupChat", name: name}
        }).success(function (data, status, headers, config) {
            $scope.getChatSessions(data);
            $scope.getChatListForDropdown();
            console.log("success in createGroupChat");
        }).error(function (data, status, headers, config) {
            console.log("error in createGroupChat");
        });
    };
    
    $scope.createIndividualChat = function(chatPartnerId){
        $http({
            method: 'POST',
            url: 'schedSharing',
            params: {action: "createIndividualChat", chatPartnerId: chatPartnerId}
        }).success(function (data, status, headers, config) {
            $scope.getChatSessions(data);
            console.log("success in createIndividualChat");
        }).error(function (data, status, headers, config) {
            console.log("error in createIndividualChat");
        });
    };
    
    $scope.getChatSessions = function(chatIdToActivate){
      $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getChatSessions"}
        }).success(function (data, status, headers, config) {
            $scope.chatSessionList = data;
            if(chatIdToActivate != null){
              for(var i=0; i<$scope.chatSessionList.length; i++){
                if($scope.chatSessionList[i].id == chatIdToActivate){
                    $scope.selectedChat = $scope.chatSessionList[i];
                    break;
                }
              }  
            };
            console.log("success in getting chatSessionList:");
            console.log($scope.chatSessionList);
        }).error(function (data, status, headers, config) {
            console.log("error in getting chatSessionList");
        });  
    };
    
    $scope.onSelectedDropdownChatChange = function(selectedDropdownChat){
        console.log("inside onSelectedDropdownChatChange:");
        console.log(selectedDropdownChat);
        if(selectedDropdownChat.type == "group"){
            for(var i=0; i<$scope.chatSessionList.length; i++){
                if($scope.chatSessionList[i].id == selectedDropdownChat.id){
                    $scope.selectedChat = $scope.chatSessionList[i];
                    break;
                }
            }
        }else if(selectedDropdownChat.type == "individual"){
            var found = false;
            if($scope.chatSessionList.length!=0){
                for(var i=0; i<$scope.chatSessionList.length; i++){
                    console.log("comparing... " + $scope.chatSessionList[i].chatPartnerId + " " + selectedDropdownChat.id)
                    if($scope.chatSessionList[i].chatPartnerId == selectedDropdownChat.id){
                        found = true;
                        $scope.selectedChat = $scope.chatSessionList[i];
                        $scope.getMessages();
                        console.log("found similar")
                        break;
                    }
                }
                if(!found){
                    $scope.createIndividualChat(selectedDropdownChat.id);
                }
            }else{
                $scope.createIndividualChat(selectedDropdownChat.id);
            }
        }
    };
    
    $scope.onSelectedChatChange = function(chat){
        if(chat != null){
            $scope.selectedChat = chat;
        }
        $scope.getMessages();
        $scope.getMembers();
    };
    
    $scope.getMessages = function(){
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getMessages", chatId: $scope.selectedChat.id}
        }).success(function (data, status, headers, config) {
            $scope.messages = data;
            $scope.shouldCheck = true;
            console.log("success in getting messages:");
        }).error(function (data, status, headers, config) {
            console.log("error in getting messages");
        });
    };
    
    $scope.getMembers = function(){
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getMembers", chatId: $scope.selectedChat.id}
        }).success(function (data, status, headers, config) {
            $scope.members = data;
            console.log("success in getting members:");
        }).error(function (data, status, headers, config) {
            console.log("error in getting members");
        });
    };
    
    $scope.sendMessage = function(messageToSend){
        console.log("message to send: " + messageToSend);
        if(messageToSend != "" && $scope.selectedChat != null){
            $http({
                method: 'POST',
                url: 'schedSharing',
                params: {action: "sendMessage", chatId: $scope.selectedChat.id, text: messageToSend}
            }).success(function (data, status, headers, config) {
                $scope.data.txtMessage = "";
                $scope.shouldCheck = false;
                $scope.getMessages();
                console.log("success in sending message");
            }).error(function (data, status, headers, config) {
                console.log("error in sending message");
            });
        }
    };
    
    $scope.checkForChange = function(){
        $interval(function(){
            if($scope.selectedChat!=null && $scope.shouldCheck){
                $http({
                    method: 'GET',
                    url: 'schedSharing',
                    params: {action: "getLastEdit", chatId: $scope.selectedChat.id}
                }).success(function (data, status, headers, config) {
                    var newLastEdit = data;
                    if ($scope.selectedChat.lastEdit != newLastEdit){
                        $scope.shouldCheck = false;
                        $scope.selectedChat.lastEdit = newLastEdit;
                        $scope.getMessages();
                    }
                    console.log("success in getting lastEdit");
                }).error(function (data, status, headers, config) {
                    console.log("error in getting lastEdit");
                });
            }
        },1000)
    };
    
    $scope.getSharedSectionsList();
    $scope.getBorrowedSectionsList();
    $scope.getSharedRoomsList();
    $scope.getBorrowedRoomsList();
    $scope.getNotifications();
    $scope.getChatListForDropdown();
    $scope.getChatSessions();
    $scope.checkForChange();
});

app.controller('shareSectionModalCtrl', function ($scope, $http, $uibModalInstance, ivhTreeviewBfs) {
    //get list of other users to whom current user can share sections to
    $scope.getUsers = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getUsers"}
        }).success(function (data, status, headers, config) {
            $scope.usersList = data;
            console.log("got users:");
            console.log($scope.usersList);
        }).error(function (data, status, headers, config) {
            console.log("error getting users");
        });
        return true;
    };

    // list of sections which current user can share (sections under current user's college)
    $scope.getMySections = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getMySections"}
        }).success(function (data, status, headers, config) {
            $scope.mySectionsList = data;
            console.log("got My Sections:");
            console.log($scope.mySectionsList);
        }).error(function (data, status, headers, config) {
            console.log("error getting mySections");
        });
        return true;
    };

    $scope.updateSectionTree = function (selectedUser) { //sectoin tree in the shareSectionModal
        $scope.shareableSectionsList = $scope.mySectionsList;
        var borrowedArrayLength = selectedUser.borrowedSections.length;

        for (var i = 0; i < $scope.shareableSectionsList.length; i++) { // departments
            for (j = 0; j < $scope.shareableSectionsList[i].children.length; j++) { // courses
                for (k = 0; k < $scope.shareableSectionsList[i].children[j].children.length; k++) { // sections
                    for (x = 0; x < borrowedArrayLength; x++) {
                        if ($scope.shareableSectionsList[i].children[j].children[k].value.id == selectedUser.borrowedSections[x]) {
                            $scope.shareableSectionsList[i].children[j].children.splice(k, 1);
                            k--;
                            break;
                        }
                    }
                }
            }
        }
    };

    $scope.ok = function () {
        var selectedSectionIds = [];
        ivhTreeviewBfs($scope.shareableSectionsList, function (node) {
            if (node.selected && !node.children) {
                selectedSectionIds.push(node.value.id);
            }
        });
        var result = {
            selectedUser: $scope.selectedUser,
            selectedSections: selectedSectionIds
        };
        $uibModalInstance.close(result);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.getUsers();
    $scope.getMySections();
});

app.controller('shareRoomModalCtrl', function ($scope, $http, $uibModalInstance, ivhTreeviewBfs) {
    //get list of other users to whom current user can share rooms to
    $scope.getUsers = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getUsers"}
        }).success(function (data, status, headers, config) {
            $scope.usersList = data;
            console.log("got users:");
            console.log($scope.usersList);
        }).error(function (data, status, headers, config) {
            console.log("error getting users");
        });
        return true;
    };

    // list of rooms which current user can share (rooms under current user's college)
    $scope.getMyRooms = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getMyRooms"}
        }).success(function (data, status, headers, config) {
            $scope.myRoomsList = data;
            console.log("got My Rooms:");
            console.log($scope.myRoomsList);
        }).error(function (data, status, headers, config) {
            console.log("error getting myRooms");
        });
        return true;
    };

    $scope.updateRoomTree = function (selectedUser) { //room tree in the shareRoomModal
        $scope.shareableRoomsList = $scope.myRoomsList;
        var borrowedArrayLength = selectedUser.borrowedRooms.length;

        for (var i = 0; i < $scope.shareableRoomsList.length; i++) { // departments
            for (j = 0; j < $scope.shareableRoomsList[i].children.length; j++) { // rooms
                for (x = 0; x < borrowedArrayLength; x++) {
                    if ($scope.shareableRoomsList[i].children[j].value.id == selectedUser.borrowedRooms[x]) {
                        $scope.shareableRoomsList[i].children.splice(j, 1);
                        j--;
                        break;
                    }
                }
            }
        }
    };

    $scope.ok = function () {
        var selectedRoomIds = [];
        ivhTreeviewBfs($scope.shareableRoomsList, function (node) {
            if (node.selected && !node.children) {
                selectedRoomIds.push(node.value.id);
            }
        });
        var result = {
            selectedUser: $scope.selectedUser,
            selectedRooms: selectedRoomIds
        };
        $uibModalInstance.close(result);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.getUsers();
    $scope.getMyRooms();
});

app.controller('ModalCtrl', function ($scope, $uibModalInstance) {
    $scope.ok = function () {
        $uibModalInstance.close();
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

app.controller('NotificationModalCtrl', function ($scope, $http, $uibModalInstance, data){
    $scope.notification = data;
    $scope.affectedItems = [];
    
    $scope.getAffectedItems = function(){
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getAffectedItems", type: $scope.notification.type, logId: $scope.notification.logId}
        }).success(function (data, status, headers, config) {
            $scope.affectedItems = data;
            console.log("success in getting affected items");
        }).error(function (data, status, headers, config) {
            console.log("error in getting affected items");
        });
    };
    
    $scope.ok = function () {
        $uibModalInstance.dismiss('modal cancelled');
    };
    
    $scope.getAffectedItems();
});

app.controller('createGroupChatModalCtrl', function ($scope, $uibModalInstance) {
    $scope.name = "";

    $scope.ok = function () {
        if($scope.name != "") $uibModalInstance.close($scope.name);
        else alert("Please give the group chat session a name.");
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

app.controller('inviteModalCtrl', function ($scope, data, $http, $uibModalInstance) {
    $scope.selectedChatId = data;
    $scope.usersList;
    $scope.selectedUser;
    $scope.getInvitees = function () {
        $http({
            method: 'GET',
            url: 'schedSharing',
            params: {action: "getInvitees", chatId: $scope.selectedChatId}
        }).success(function (data, status, headers, config) {
            $scope.usersList = data;
            console.log("got users");
        }).error(function (data, status, headers, config) {
            console.log("error getting users");
        });
        return true;
    };

    $scope.ok = function () {
        var result = $scope.selectedUser.chatPartnerId;
        $uibModalInstance.close(result);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.getInvitees();
});

