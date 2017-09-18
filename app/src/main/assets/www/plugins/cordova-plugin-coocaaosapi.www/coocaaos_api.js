
cordova.define("cordova-plugin-coocaaosapi.CoocaaOS_Api", function(require, exports, module) {
	//android 原生toast插件
	var exec = require("cordova/exec");

//	function CoocaaOSApi(){
//         console.log("fyb,coocaaosapi.js CoocaaOSApi");
//
//         var thiz = this;
//         channel.onCordovaReady.subscribe(function(){
//            console.log('fyb,coocaaosapi.js channel.onCordovaReady.subscribe');
//            thiz.waitForCoocaaOSInitReady(
//                function(message){
//                    console.log('fyb,coocaaosapi.js success CoocaaOSInitReady ' +message);
//                    channel.onCoocaaOsInitReady.fire();
//                },
//                function(message){
//                    console.log('error : ' + message);
//                }
//            );
//         });
//    }
//
//    CoocaaOSApi.prototype.waitForCoocaaOSInitReady = function(success,error){
//         argscheck.checkArgs('ff','CoocaaOSApi.waitForCoocaaOSInitReady',arguments);
//         exec(success,error,'CoocaaOSApi','waitForOSReady',[]);
//    }
//
//    CoocaaOSApi.prototype.startLocalMedia = function(success,error){
//        argscheck.checkArgs('ff','CoocaaOSApi.startLocalMedia',arguments);
//        startapp.check("com.tianci.localmedia", function(message) { /* success */
//           startapp.start([["com.tianci.localmedia", "com.tianci.localmedia.MainActivity"]], success,error);
//        },
//        error);
//    }
//
//	module.exports = new CoocaaOSApi();

	module.exports = {
		callJavaFunc: function(content, type) {
		    console.log("fyb,enter callJavaFunc");
			exec(null, null, "CoocaaApiPlugin", "test0918", [content, type]);
		}
	};
});