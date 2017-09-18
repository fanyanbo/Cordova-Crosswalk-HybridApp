cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
            {
                "file": "plugins/cordova-plugin-myplugin/www/myplugin.js",
                "id": "cordova-plugin-myplugin.MyPlugin",
                "clobbers": [
                    "coocaaos_test"
                ]
            },
            {
                "file": "plugins/cordova-plugin-startapp/www/coocaaos_startapp.js",
                "id": "cordova-plugin-startapp.CoocaaOS_StartApp",
                "clobbers": [
                     "coocaaos_startapp"
                ]
            },
            {
                "file": "plugins/cordova-plugin-startapp/www/coocaaos_api.js",
                "id": "cordova-plugin-coocaaosapi.CoocaaOS_Api",
                "clobbers": [
                     "coocaaos_api"
                ]
            },
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.2",
    "cordova-plugin-crosswalk-webview": "2.3.0"
};
// BOTTOM OF METADATA
});