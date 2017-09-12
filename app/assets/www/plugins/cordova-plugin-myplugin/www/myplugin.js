cordova.define("cordova-plugin-myplugin.MyPlugin", function(require, exports, module) {
	//android 原生toast插件
	var exec = require("cordova/exec");

	module.exports = {
		callJavaFunc: function(content, type) {
		    console.log("fyb,enter callJavaFunc");
			exec(null, null, "MyPlugin", "test", [content, type]);
			//exec方法参数解释：
			//第一个参数：成功回调函数，当在java中调用callbackcontext.success()时触发
			//第二个参数：失败回调参数，当在java中调用callbackcontext.error()时触发
			//第三个参数:对应的res/xml/config.xml中配置插件的name，
			//第四个参数：方法名
			//第五个参数：需要传到java中的参数
		}
	};
});