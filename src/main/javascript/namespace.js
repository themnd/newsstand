// based on http://appendto.com/2010/10/how-good-c-habits-can-encourage-bad-javascript-habits-part-1
// and http://www.kenneth-truyers.net/2013/04/27/javascript-namespaces-and-modules
//
// this module allow you to create a namespace function you can use like this:
//
// Atex.namespace('Atex.plugin.example')
//
// and this will create the Atex.plugin.example namespace
//
// @author mnova
(function(Atex) {
  "use strict";

    Atex.namespace = function(ns) {

        var nsparts = ns.split(".");
        var parent = Atex;

        // we want to be able to include or exclude the root namespace so we strip
        // it if it's in the namespace
        if (nsparts[0] === "Atex") {
            nsparts = nsparts.slice(1);
        }

        // loop through the parts and create a nested namespace if necessary
        for (var i = 0; i < nsparts.length; i++) {
            var partname = nsparts[i];
            // check if the current parent already has the namespace declared
            // if it isn't, then create it
            if (typeof parent[partname] === "undefined") {
                parent[partname] = {};
            }
            // get a reference to the deepest element in the hierarchy so far
            parent = parent[partname];
        }
        // the parent is now constructed with empty namespaces and can be used.
        // we return the outermost namespace
        return parent;

    }

}(window.Atex = window.Atex || {}));

