

cordova.define("cordova-plugin-startapp.CoocaaOS_StartApp", function(require, exports, module) {

	var exec = require("cordova/exec");

	module.exports = {

	    check: function(message, completeCallback, errorCallback) {
		    exec(completeCallback, errorCallback, "startApp", "check", [message]);
	    },

	    start: function(message, completeCallback, errorCallback) {
		    exec(completeCallback, errorCallback, "startApp", "start", (typeof message === 'string') ? [message] : message);
	    },


	    startLocalMedia: function(success,error){
	        var obj = this;
            obj.check("com.tianci.localmedia", function(message) {
                obj.start([["com.tianci.localmedia", "com.tianci.localmedia.MainActivity"]], function(message) {
                },error);},
            error);
        }

	};

});