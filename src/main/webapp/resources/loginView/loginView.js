// 'use strict';

// angular.module('myApp.loginView', ['ngRoute'])

// .config(['$routeProvider', function($routeProvider) {
//   $routeProvider.when('/login', {
//     templateUrl: 'resources/loginView/loginView.html',
//     controller: 'LoginFormController'
//   });
// }])

// .controller('LoginFormController', ['$scope','$http',function($scope,$http) {
// 	$scope.user = {email:"jcorellam@ucenfotec.ac.cr",password:"1234"};
	
// 	$scope.checkLogin = function(){
// 		$scope.authError = null;

//         // Try to login 

//     	$http.post('rest/login/checkuser/',$scope.user).success(function (loginResponse) {

//       .then(function(response) {
//     		if(loginResponse.code == 200){
//     			var usuario = {"idUser":loginResponse.idUsuario,"firstName":loginResponse.firstName,"lastName":loginResponse.lastName};
//     			var path = "/lyra/app#/view1";
//     			window.location.href = path;
//     		}else{
//     			$scope.authError = 'Email or Password not right';
//     		}
//          }, function(x) {
//                  $scope.authError = 'Server Error';
//                 });
//     	});
    	
//     };
// }]);




'use strict';

angular.module('myApp.loginView', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'resources/loginView/loginView.html',
    controller: 'LoginViewCtrl'
  });
}])

.controller('LoginViewCtrl', ['$scope','$http',function($scope,$http) {
  
<<<<<<< HEAD
  angular.element(document).ready(function () {
         OneUI.init('uiForms');
         BasePagesLogin.init();
  });

 
  $scope.user = {email:"jean@maradiaga.com",password:"12345"};
  
=======
  $scope.checkLogin = function(){
    
>>>>>>> 472a8998a7461e0196ad84dabcfd15e40eaeb8c0
      $http.post('rest/login/checkuser/',$scope.user).success(function (loginResponse) {

        if(loginResponse.code == 200){
          var usuario = {"userId":loginResponse.userId,"firstName":loginResponse.firstName,"lastName":loginResponse.lastName};
          console.log(usuario);
       //   var path = "/lyra/app#/view1";
       //   window.location.href = path;
        }else{
          alert("invalido");
        }
      });
      
}]);
