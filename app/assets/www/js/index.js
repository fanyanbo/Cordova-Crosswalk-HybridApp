/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        console.log("fyb,onDeviceReady");
        this.receivedEvent('deviceready');
        this.triggleButton();
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {

        console.log('fyb,Received Event: ' + id);

        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelectorAll('.received');

        listeningElement.setAttribute('style', 'display:none;');
//        receivedElement.setAttribute('style', 'display:block;');
        for( var i = 0 , j = receivedElement.length ; i < j ; i++ ){
            receivedElement[i].setAttribute('style', 'display:block;');
        }

    },

    triggleButton:function(){

        cordova.require("cordova-plugin-myplugin.MyPlugin");
        cordova.require("cordova-plugin-startapp.COOCAAOS_StartApp");

        document.getElementById("test").addEventListener("click", function (){
            console.log("fyb,test,test,test");
            coocaaos_test.callJavaFunc("test",0);
            coocaaos_startapp.startLocalMedia(
                function(message) {
                    console.log(message);
                },
                function(error) {
                    console.log(error);
                }
            );
        },false);

    }



};

app.initialize();