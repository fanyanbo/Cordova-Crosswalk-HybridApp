
cordova.define("cordova-plugin-coocaaosapi.CoocaaOS_Api", function(require, exports, module) {

	var argscheck = require('cordova/argscheck'),
        channel = require('cordova/channel'),
        exec = require('cordova/exec'),
        cordova = require('cordova'),
        startapp = require('cordova-plugin-startapp.CoocaaOS_StartApp');

        channel.createSticky('onCoocaaOsInitReady');
        channel.waitForInitialization('onCoocaaOsInitReady');

	function CoocaaOSApi(){

         var thiz = this;
         channel.onCordovaReady.subscribe(function(){
            console.log('fyb,coocaaosapi.js 1111');
            thiz.waitForCoocaaOSInitReady(
                function(message){
                    console.log('fyb,coocaaosapi.js success CoocaaOSInitReady ' +message);
                    channel.onCoocaaOsInitReady.fire();
                },
                function(message){
                    console.log('error : ' + message);
                }
            );
         });
    }

    CoocaaOSApi.prototype.waitForCoocaaOSInitReady = function(success,error){
         console.log('fyb,coocaaosapi.js 2222');
         argscheck.checkArgs('ff','CoocaaOSApi.waitForCoocaaOSInitReady',arguments);
         exec(success,error,'CoocaaOSApi','waitForOSReady',[]);
    }

    CoocaaOSApi.prototype.startLocalMedia = function(success,error){
        argscheck.checkArgs('ff','CoocaaOSApi.startLocalMedia',arguments);
        startapp.check("com.tianci.localmedia", function(message) { /* success */
           startapp.start([["com.tianci.localmedia", "com.tianci.localmedia.MainActivity"]], success,error);
        },
        error);
    }

	module.exports = new CoocaaOSApi();

//	module.exports = {
//		callJavaFunc: function(content, type) {
//		    console.log("fyb,enter callJavaFunc");
//			exec(null, null, "CoocaaApiPlugin", "test0918", [content, type]);
//		}
//	};
});