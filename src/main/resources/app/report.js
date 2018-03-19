 var app = angular.module('impact', []);
app.controller('analyser', function($scope, $http, $q) {
    $scope.testcases =[];
    $scope.cachetests = [];
    $scope.pageNames = [];
    $scope.fieldNames = [];
    var deferred = $q.defer();
    $http.get('report.json').then(function(response){
        response.data.forEach(function(d){
            if($scope.pageNames.indexOf(d.pageName) == -1){
                $scope.pageNames.push(d.pageName);
            }
            $scope.testcases.push(d);
        });
        $scope.cachetests = $scope.testcases;
    }).catch(function(error){
        console.log(error);
    });

    $scope.loadFields = function(pageName) {
        $scope.testcases = $scope.cachetests;
        $scope.pageFields = [];
        if(pageName !== 'Choose Page To Get Report') {
            var changedList = $scope.testcases;
            changedList.forEach(function(testcase) {
                if(testcase.pageName === pageName) {
                    let fiel = testcase.fieldName+':'+testcase.fieldClass;
                    if($scope.pageFields.indexOf(fiel) == -1) {
                        $scope.pageFields.push(fiel);
                    }
                }
            });
        }
    }

    $scope.loadPage = function(pageName, fieldName) {
        console.log(pageName);
        console.log(fieldName);
        $scope.testcases = $scope.cachetests;
        var tests = [];
        if(pageName !== 'Choose Page To Get Report') {
            var changedList = $scope.testcases;
            $scope.testcases = [];
            changedList.forEach(function(testcase) {
                if(testcase.pageName === pageName) {
                    $scope.testcases.push(testcase);
                    if(tests.indexOf(testcase.testMethod) == -1) {
                        tests.push(testcase.testMethod);
                    }
                }
            });
        }
        if(pageName !== 'Choose Page To Get Report' && fieldName != 'Choose Field To Get Report') {
            var changedList = $scope.testcases;
            $scope.testcases = [];
            changedList.forEach(function(testcase) {
             let fielddd = testcase.fieldName +':'+ testcase.fieldClass;
                if(testcase.pageName === pageName && fielddd === fieldName) {
                    $scope.testcases.push(testcase);
                    if(tests.indexOf(testcase.testMethod) == -1) {
                        tests.push(testcase.testMethod);
                    }
                }
            });
        }
        $scope.testCount = tests.length;
        $scope.displaySummary = true;
    }
});