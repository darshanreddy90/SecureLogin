(function(){
var app=angular.module('loginDashboard',['ngResource','ngRoute']);

  app.factory('UserNameFactory', ['$resource',
    function($resource) {
      return $resource('/userNameAvailability.php', {}, {
        query: {
          method: 'GET'
        }
      });
    }
  ]);


  app.controller('passwordController',function($scope, UserNameFactory){
    $scope.password = "";
    $scope.showLengthError = false;
    $scope.showEmailError = false;
    $scope.emailEntered = "";

    $scope.showPassPowrdLength = function(){
      if($scope.password&&$scope.password.match(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[0-9a-zA-Z]{8,}$/)){
        $scope.showLengthError = false;
      }
      else{
        $scope.showLengthError = true;
      }
    }

    $scope.checkEmail = function(){
//        console.log($scope.emailEntered);
//        UserNameFactory.query({email:"'"+$scope.emailEntered+"'"}).$promise.then(function(data){
//          if(data[0] == "T"){
//            $scope.showEmailError = true;
//          }else{
//            $scope.showEmailError = false;
//          }
//        });
    }


  });
})();

window.onload = function(){
    console.log($("#screenHeight"));
  document.getElementById("screenHeight").value = window.screen.availHeight;
  $("#screenWidth").val(window.screen.availWidth);
//  var timezone = jstz.determine();  );

}