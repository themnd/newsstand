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

        function ajaxJson(method, ajaxUrl, options) {
          api.log('ajaxjson m:' + method + ' u:' + ajaxUrl);
          return $.ajax($.extend({
            type: method,
            url: ajaxUrl,
            dataType: 'json'
          }, options));
        }

        function ajaxPost(ajaxUrl, options) {
          return ajax('POST', ajaxUrl, options);
        }

        function ajaxGet(ajaxUrl, options) {
          return ajax('GET', ajaxUrl, options);
        }

        function ajaxJPost(ajaxUrl, options) {
          return ajaxJson('POST', ajaxUrl, options);
        }

        function ajaxJGet(ajaxUrl, options) {
          return ajaxJson('GET', ajaxUrl, options);
        }

        function login(catalog, issueCode) {
          api.log('login c:' + catalog + ' i:' + issueCode);
          var url = '/newsstandws/issue/' + issueCode + '/login?catalog=' + encodeURIComponent(catalog);
          return ajaxPost(url);
        };

        function getCatalog(catalog) {
          api.log('catalog c:' + catalog);
          var url = '/newsstandws/catalog/' + catalog;
          return ajaxJGet(url);
        };

        function getPublication(publicationId) {
          api.log('publication id:' + publicationId);
          var url = '/newsstandws/publication/' + publicationId;
          return ajaxJGet(url);
        };

        function getIssue(issueCode) {
          api.log('issue code:' + issueCode);
          var url = '/newsstandws/issue/' + issueCode;
          return ajaxJGet(url);
        };

        function getPublicationFromIssue(catalog, issueCode) {
          api.log('publication c:' + catalog + ' i:' + issueCode);
          var url = '/newsstandws/catalog/' + catalog + '/publication?issueCode=' + encodeURIComponent(issueCode);
          return ajaxJGet(url);
        };

        // Public interface
        return {
            login: login,
            getCatalog: getCatalog,
            getPublication: getPublication,
            getIssue: getIssue,
            getPublicationFromIssue: getPublicationFromIssue
        };
    }());
}(this));
