Atex.namespace("Atex.plugin.newsstand");
(function(global) {
    "use strict";

    Atex.plugin.newsstand = (function () {

        var api = {
        };

        api.log = function(msg) {
          if (console.log) {
            console.log(msg);
          }
        };

        function ajax(method, ajaxUrl, options) {
          api.log('ajax m:' + method + ' u:' + ajaxUrl);
          return $.ajax($.extend({
            type: method,
            url: ajaxUrl
          }, options));
        }

        function ajaxPost(ajaxUrl, options) {
          return ajax('POST', ajaxUrl, options);
        }

        function ajaxGet(ajaxUrl, options) {
          return ajax('GET', ajaxUrl, options);
        }

        function login(catalog, issueCode) {
          api.log('login c:' + catalog + ' i:' + issueCode);
          var url = '/newsstandws/issue/' + issueCode + '/login?catalog=' + encodeURIComponent(catalog);
          return ajaxPost(url);
        };

        // Public interface
        return {
            login: login
        };
    }());
}(this));
