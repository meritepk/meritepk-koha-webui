(function() {
  'use strict';

  function WebAppConfig($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!');
    $routeProvider.when('/login', {
      templateUrl : 'app/signin/signin.html',
      controller : 'LoginController as $ctrl'
    }).when('/signout', {
      templateUrl : 'app/signing/signout.html',
      controller : 'LogoutController as $ctrl'
    }).when('/bookshelf', {
      templateUrl : 'app/bookshelf/bookshelf.html',
      controller : 'BookshelfController as $ctrl'
    }).otherwise('/login');
  }

  angular.module('WebApp', [ 'ngRoute', 'ngResource', 'ng' ])

  .config([ '$locationProvider', '$routeProvider', WebAppConfig ]);

}());

(function() {
  'use strict';

  function LoginController($http, $location, $rootScope) {
    var self = this;
    self.user = {};

    self.login = function(formInvalid) {
      self.user.error = null;
      if (formInvalid) {
        return;
      }
      $http.post('login',
          'username=' + self.user.name + '&password=' + self.user.password + '&remember-me=' + self.user.rememberme, {
            headers : {
              "Content-Type" : "application/x-www-form-urlencoded"
            }
          }).then(self.loginSuccess, self.loginFailure);
    }

    self.loginSuccess = function(response) {
      self.user.info = response.data;
      self.user.menu = [];
      for (var index = 0; index < self.user.info.authorities.length; index++) {
        if (self.user.info.authorities[index].authority == 'ADMIN') {
          self.user.menu.push({
            'title' : 'Bookshelf',
            'url' : '#!/bookshelf'
          });
        }
      }
      self.user.menu.push({
        'title' : 'Signout',
        'url' : '#!/signout'
      });
      $rootScope.user = self.user;
      $location.path('/bookshelf');
    }

    self.loginFailure = function(response) {
      self.user.error = response.data.message;
      alert(response.data.message);
    }
  }

  angular.module('WebApp').controller('LoginController', [ '$http', '$location', '$rootScope', LoginController ]);

}());

(function() {
  'use strict';

  function LogoutController($http, $location, $rootScope) {
    var self = this;

    self.init = function() {
      if ($rootScope.user.name) {
        $http.post('logout').then(self.logoutSuccess, self.logoutFailure);
      }
    }

    self.logoutSuccess = function(response) {
      $rootScope.user = {
        'menu' : [ {
          'title' : 'Signin',
          'url' : '#!/signin'
        } ]
      };
    }
    self.logoutFailure = function(response) {
      self.user.error = response.data.message;
      alert(response.data.message);
    }

    self.init();
  }

  angular.module('WebApp').controller('LogoutController', [ '$http', '$location', '$rootScope', LogoutController ]);

}());

(function() {
  'use strict';

  function BookshelfController($http, $location) {
    var self = this;

    self.init = function() {
      self.callNo = $location.search().callNo;
      if (self.callNo && self.callNo.length > 0) {
        self.search(false);
      }
      self.bookshelves = [ {
        callNo : 'call_no',
        bookshelf : 'bookshelf'
      } ];
    }

    self.search = function(formInvalid) {
      if (formInvalid) {
        return;
      }
      $http.get('webservices/bookshelves?callNo=' + self.callNo).then(function(response) {
        self.bookshelves = response.data.result;
      }, function() {
      });
    }

    self.init();
  }

  angular.module('WebApp').controller('BookshelfController', [ '$http', '$location', BookshelfController ]);

}());
