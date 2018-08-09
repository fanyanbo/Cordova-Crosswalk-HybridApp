## Cordova-Crosswalk-HybridApp
1.切换至builds/master分支，执行gradlew ccinit拉取工程代码

2.按遥控器2键，可以来回切换浏览器内核（Android原生内核和XWalk内核），按遥控器1键加载Demo网页

3.如果用XWalk内核加载网页，需要先安装CrosswalkLibraryPlugin（同名分支下），或是从官网下载libxwalk.so和libdummy.so放置/system/lib下

4.无论是原生内核还是XWalk内核，都已对接了cordova框架

5.Web前端Demo代码在assets中
