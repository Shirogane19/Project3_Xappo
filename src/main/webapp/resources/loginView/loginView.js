'use strict';

angular.module('myApp.loginView', ['ngRoute','ngStorage'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'resources/loginView/loginView.html',
    controller: 'LoginViewCtrl'
  });
}])

.controller('LoginViewCtrl', ['$scope','$http','$window','$localStorage',function($scope,$http,$window,$localStorage) {
  
  $scope.user = {};


  angular.element(document).ready(function () {
         OneUI.init('uiForms');
         BasePagesLogin.init();
  });




  $scope.checkLogin = function(){

    $http.post('rest/login/checkuser/',$scope.user).success(function (loginResponse) {

      if(loginResponse.code == 200){
        $scope.user = {"userId":loginResponse.userId,"firstName":loginResponse.firstName,"lastName":loginResponse.lastName};
        $scope.save($scope.user);
        var path = "/lyra/app#/home";
        window.location.href = path;
     //   $state.go('home');

      }else{
          alert("invalido");
      }

    });

  };


  $scope.save = function(u) {
    console.log(u);
    $localStorage.user = u;
  };

      
}]);
