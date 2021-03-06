/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import org.coocaa.util.BrowserVirtualInput;
import org.coocaa.util.UIUtil;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.skyworth.ui.api.SkyWithBGLoadingView;

import dalvik.system.PathClassLoader;

/**
 * This class is the main Android activity that represents the Cordova
 * application. It should be extended by the user to load the specific
 * html file that contains the application.
 *
 * As an example:
 *
 * <pre>
 *     package org.apache.cordova.examples;
 *
 *     import android.os.Bundle;
 *     import org.apache.cordova.*;
 *
 *     public class Example extends CordovaActivity {
 *       &#64;Override
 *       public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         super.init();
 *         // Load your application
 *         loadUrl(launchUrl);
 *       }
 *     }
 * </pre>
 *
 * Cordova xml configuration: Cordova uses a configuration file at
 * res/xml/config.xml to specify its settings. See "The config.xml File"
 * guide in cordova-docs at http://cordova.apache.org/docs for the documentation
 * for the configuration. The use of the set*Property() methods is
 * deprecated in favor of the config.xml file.
 *
 */
public class CordovaActivity extends Activity {
    public static String TAG = "CordovaActivity";

    // The webview for our app
    protected CordovaWebView appView;

    private static int ACTIVITY_STARTING = 0;
    private static int ACTIVITY_RUNNING = 1;
    private static int ACTIVITY_EXITING = 2;

    private static String IE9_USERAGENT = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
    private int userAgentMode = 0;

    // Keep app running when pause is received. (default = true)
    // If true, then the JavaScript and native code continue to run in the background
    // when another application (activity) is started.
    protected boolean keepRunning = true;

    // Flag to keep immersive mode if set to fullscreen
    protected boolean immersiveMode;

    // Read from config.xml:
    protected CordovaPreferences preferences;
    protected String launchUrl;
    protected ArrayList<PluginEntry> pluginEntries;
    protected CordovaInterfaceImpl cordovaInterface;

    //add by fyb
    protected FrameLayout mainLayout;
    protected SkyWithBGLoadingView mLoadingView;
    protected boolean mIsLoading = false;

    private long mStartLoadingTime = 0, mEndLoadingTime = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        initNativeDirectory(getApplication());
        // need to activate preferences before super.onCreate to avoid "requestFeature() must be called before adding content" exception
        loadConfig();

        String logLevel = preferences.getString("loglevel", "ERROR");
        LOG.setLogLevel(logLevel);

        LOG.i(TAG, "Apache Cordova native platform version " + CordovaWebView.CORDOVA_VERSION + " is starting");
        LOG.d(TAG, "CordovaActivity.onCreate()");

        if (!preferences.getBoolean("ShowTitle", false)) {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        if (preferences.getBoolean("SetFullscreen", false)) {
            LOG.d(TAG, "The SetFullscreen configuration is deprecated in favor of Fullscreen, and will be removed in a future version.");
            preferences.set("Fullscreen", true);
        }
        if (preferences.getBoolean("Fullscreen", false)) {
            // NOTE: use the FullscreenNotImmersive configuration key to set the activity in a REAL full screen
            // (as was the case in previous cordova versions)
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) && !preferences.getBoolean("FullscreenNotImmersive", false)) {
                immersiveMode = true;
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);

//        UIUtil.setDpiDiv_Resolution(this.getApplicationContext());
//        initScrollEdge();

        cordovaInterface = makeCordovaInterface();
        if (savedInstanceState != null) {
            cordovaInterface.restoreInstanceState(savedInstanceState);
        }

        cordovaInterface.setCordovaInterfaceListener(new CordovaInterfaceImpl.CordovaInterfaceListener() {
            @Override
            public void onPageStarted(String url) {
                Log.i("fyb","onPageStarted url = " + url);

                mStartLoadingTime = SystemClock.uptimeMillis();
                Log.i("fyb","onPageStarted mStartLoadingTime = " + mStartLoadingTime);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoadingView();
                        appView.getView().setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onPageLoadingFinished(String url) {
                Log.i("fyb","onPageLoadingFinished url = " + url);

                mEndLoadingTime = SystemClock.uptimeMillis();
                Log.i("fyb","onPageLoadingFinished (mEndLoadingTime-mStartLoadingTime) = " + (mEndLoadingTime-mStartLoadingTime));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingView();
                        appView.getView().setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onReceivedError(int errorCode, String description, String failingUrl) {
                Log.i("fyb","onReceivedError description = " + description);
            }

            @Override
            public void doUpdateVisitedHistory(String url, boolean isReload) {

            }

            @Override
            public boolean shouldOverrideUrlLoading(String url) {
                return false;
            }

            @Override
            public void onReceivedTitle(String title) {
                Log.i("fyb","onReceivedTitle title = " + title);
            }

            @Override
            public void onReceivedIcon(Bitmap icon) {
            }

            @Override
            public void onProgressChanged(int process) {
                Log.i("fyb","onProgressChanged process = " + process);
            }
        });
    }

    //add by fyb
    protected void showLoadingView() {
        if(mLoadingView != null) {
            mLoadingView.showLoading();
            mIsLoading = true;
        }
    }

    protected void hideLoadingView() {
        if(mLoadingView != null) {
            mLoadingView.dismissLoading();
            mIsLoading = false;
        }
    }

    protected boolean isLoading() {
        return mIsLoading;
    }

    protected void init() {
        appView = makeWebView();
        createViews();
        if (!appView.isInitialized()) {
            appView.init(cordovaInterface, pluginEntries, preferences);
            if(userAgentMode == 1)
                appView.setUserAgentString(IE9_USERAGENT);
        }
        cordovaInterface.onCordovaInit(appView.getPluginManager());

        // Wire the hardware volume controls to control media if desired.
        String volumePref = preferences.getString("DefaultVolumeStream", "");
        if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    @SuppressWarnings("deprecation")
    protected void loadConfig() {
        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        preferences = parser.getPreferences();
        preferences.setPreferencesBundle(getIntent().getExtras());
        launchUrl = parser.getLaunchUrl();
        pluginEntries = parser.getPluginEntries();
        Config.parser = parser;
    }

    //Suppressing warnings in AndroidStudio
    @SuppressWarnings({"deprecation", "ResourceType"})
    protected void createViews() {
        //Why are we setting a constant as the ID? This should be investigated
        appView.getView().setId(100);
        appView.getView().setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        appView.getView().setVisibility(View.INVISIBLE);
        mainLayout = new FrameLayout(this);
        mainLayout.addView(appView.getView());

        mLoadingView = new SkyWithBGLoadingView(this);
        FrameLayout.LayoutParams loading_p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        loading_p.gravity = Gravity.CENTER;
        mainLayout.addView(mLoadingView, loading_p);

        setContentView(mainLayout);

//        setContentView(appView.getView());

        if (preferences.contains("BackgroundColor")) {
            try {
                int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
                // Background of activity:
                appView.getView().setBackgroundColor(backgroundColor);
            }
            catch (NumberFormatException e){
                e.printStackTrace();
            }
        }

        appView.getView().requestFocusFromTouch();
    }

    /**
     * Construct the default web view object.
     * <p/>
     * Override this to customize the webview that is used.
     */
    protected CordovaWebView makeWebView() {
        return new CordovaWebViewImpl(makeWebViewEngine());
    }

    protected CordovaWebViewEngine makeWebViewEngine() {
        return CordovaWebViewImpl.createEngine(this, preferences);
    }

    protected CordovaInterfaceImpl makeCordovaInterface() {
        return new CordovaInterfaceImpl(this);
//        {
//            @Override
//            public Object onMessage(String id, Object data) {
//                // Plumb this to CordovaActivity.onMessage for backwards compatibility
//                return CordovaActivity.this.onMessage(id, data);
//            }
//        };
    }

    //add by fyb
    protected void setCore(int core) {
        preferences.set("webview",((core == 0) ? "org.apache.cordova.engine.SystemWebViewEngine" : "org.crosswalk.engine.XWalkWebViewEngine"));
    }

    protected void setUserAgentMode(int mode) {
        userAgentMode = mode;
    }

    /**
     * Load the url into the webview.
     */
    public void loadUrl(String url) {
        if (appView == null) {
            init();
        }

        // If keepRunning
        this.keepRunning = preferences.getBoolean("KeepRunning", true);

        appView.loadUrlIntoView(url, true);
    }

    /**
     * Called when the system is about to start resuming a previous activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        LOG.d(TAG, "Paused the activity.");

        if (this.appView != null) {
            // CB-9382 If there is an activity that started for result and main activity is waiting for callback
            // result, we shoudn't stop WebView Javascript timers, as activity for result might be using them
            boolean keepRunning = this.keepRunning || this.cordovaInterface.activityResultCallback != null;
            this.appView.handlePause(keepRunning);
        }
    }

    /**
     * Called when the activity receives a new intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Forward to plugins
        if (this.appView != null)
            this.appView.onNewIntent(intent);
    }

    /**
     * Called when the activity will start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LOG.d(TAG, "Resumed the activity.");

        if (this.appView == null) {
            return;
        }
        // Force window to have focus, so application always
        // receive user input. Workaround for some devices (Samsung Galaxy Note 3 at least)
        this.getWindow().getDecorView().requestFocus();

        this.appView.handleResume(this.keepRunning);
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        LOG.d(TAG, "Stopped the activity.");

        if (this.appView == null) {
            return;
        }
        this.appView.handleStop();
    }

    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        LOG.d(TAG, "Started the activity.");

        if (this.appView == null) {
            return;
        }
        this.appView.handleStart();
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    public void onDestroy() {
        LOG.d(TAG, "CordovaActivity.onDestroy()");
        super.onDestroy();

        if (this.appView != null) {
            appView.handleDestroy();
        }
    }

    /**
     * Called when view focus is changed
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && immersiveMode) {
            final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        // Capture requestCode here so that it is captured in the setActivityResultCallback() case.
        cordovaInterface.setActivityResultRequestCode(requestCode);
        super.startActivityForResult(intent, requestCode, options);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
        super.onActivityResult(requestCode, resultCode, intent);
        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable).
     * The errorCode parameter corresponds to one of the ERROR_* constants.
     *
     * @param errorCode   The error code corresponding to an ERROR_* value.
     * @param description A String describing the error.
     * @param failingUrl  The url that failed to load.
     */
    public void onReceivedError(final int errorCode, final String description, final String failingUrl) {
        final CordovaActivity me = this;

        // If errorUrl specified, then load it
        final String errorUrl = preferences.getString("errorUrl", null);
        if ((errorUrl != null) && (!failingUrl.equals(errorUrl)) && (appView != null)) {
            // Load URL on UI thread
            me.runOnUiThread(new Runnable() {
                public void run() {
                    me.appView.showWebPage(errorUrl, false, true, null);
                }
            });
        }
        // If not, then display error dialog
        else {
            final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
            me.runOnUiThread(new Runnable() {
                public void run() {
                    if (exit) {
                        me.appView.getView().setVisibility(View.GONE);
                        me.displayError("Application Error", description + " (" + failingUrl + ")", "OK", exit);
                    }
                }
            });
        }
    }

    /**
     * Display an error dialog and optionally exit application.
     */
    public void displayError(final String title, final String message, final String button, final boolean exit) {
        final CordovaActivity me = this;
        me.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(me);
                    dlg.setMessage(message);
                    dlg.setTitle(title);
                    dlg.setCancelable(false);
                    dlg.setPositiveButton(button,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (exit) {
                                        finish();
                                    }
                                }
                            });
                    dlg.create();
                    dlg.show();
                } catch (Exception e) {
                    finish();
                }
            }
        });
    }

    /*
     * Hook in Cordova for menu plugins
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (appView != null) {
            appView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (appView != null) {
            appView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (appView != null) {
            appView.getPluginManager().postMessage("onOptionsItemSelected", item);
        }
        return true;
    }

    /**
     * Called when a message is sent to plugin.
     *
     * @param id   The message id
     * @param data The message data
     * @return Object or null
     */
    public Object onMessage(String id, Object data) {
        if ("onReceivedError".equals(id)) {
            JSONObject d = (JSONObject) data;
            try {
                this.onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if ("exit".equals(id)) {
            finish();
        }
        return null;
    }

    protected void onSaveInstanceState(Bundle outState) {
        cordovaInterface.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * Called by the system when the device configuration changes while your activity is running.
     *
     * @param newConfig The new device configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.appView == null) {
            return;
        }
        PluginManager pm = this.appView.getPluginManager();
        if (pm != null) {
            pm.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Called by the system when the user grants permissions
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        try
        {
            cordovaInterface.onRequestPermissionResult(requestCode, permissions, grantResults);
        }
        catch (JSONException e)
        {
            LOG.d(TAG, "JSONException: Parameters fed into the method are not valid");
            e.printStackTrace();
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        final int keyCode = event.getKeyCode();
        if (false)
        {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        return BrowserVirtualInput.getInstance().clickCenter();
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        Log.i("fyb", "flay_y = " + flag_y + ", down_y = " + down_y);

                        if (flag_y > down_y)
                        {
                            if (getTopView().canScrollVertically(SCROLL_DEFAULT_V))
                            {
                                Log.i("fyb","222222222");
                                getTopView().scrollBy(0, SCROLL_DEFAULT_V);
                            }else{
                                Log.i("fyb","2222222");
                            }
                        }

                        return BrowserVirtualInput.getInstance().moveDown(keyCode);
                    case KeyEvent.KEYCODE_DPAD_LEFT:

                        if (flag_X < left_x)
                        {
                            scrollWebView(-SCROLL_DEFAULT_H);
                        }

                        return BrowserVirtualInput.getInstance().moveLeft(keyCode);
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (flag_X > right_x)
                        {
                            scrollWebView(SCROLL_DEFAULT_H);
                        }
                        return BrowserVirtualInput.getInstance().moveRight(keyCode);
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (flag_y < up_y)
                        {
                            if (getTopView().canScrollVertically(-SCROLL_DEFAULT_V))
                            {
                                getTopView().scrollBy(0, -SCROLL_DEFAULT_V);
                            }
                        }
                        return BrowserVirtualInput.getInstance().moveUp(keyCode);
                    // TODO 长按确认键
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_MENU:
                    default:
                        break;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev)
    {
        if (true)
        {
            flag_X = ev.getX();
            flag_y = ev.getY();
        }
        return super.dispatchGenericMotionEvent(ev);
    }

    private final int SCROLL_DEFAULT_H = 50;
    private final int SCROLL_DEFAULT_V = 100;

    private float flag_X = 0.0f;
    private float flag_y = 0.0f;

    // 根据不同分辨率电视，选择不同的上，下，左，右标准边界
    private static float up_y = 0.0f;
    private static float down_y = 0.0f;
    private static float right_x = 0.0f;
    private static float left_x = 0.0f;

    private void initScrollEdge()
    {
        left_x = UIUtil.getResolutionValue(150);
        right_x = UIUtil.getResolutionValue(1770);
        up_y = UIUtil.getResolutionValue(150);
        down_y = UIUtil.getResolutionValue(930);
    }

    private void scrollWebView(int value)
    {
       appView.loadUrl("javascript:window.scrollBy(" + (value) + "," + 0 + ")");
    }

    private View getTopView()
    {
        return appView.getView();
    }

    public static void initNativeDirectory(Application application) {
        if (hasDexClassLoader()) {
            try {
                createNewNativeDir(application);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void createNewNativeDir(Context context) throws Exception{
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object pathList = getPathList(pathClassLoader);
        //获取当前类的属性
        Object nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
        ((Field) nativeLibraryDirectories).setAccessible(true);
        //获取 DEXPATHList中的属性值
        File[] files1 = (File[])((Field) nativeLibraryDirectories).get(pathList);
        Object filesss = Array.newInstance(File.class, files1.length + 1);
        //添加自定义.so路径
        Array.set(filesss, 0, new File("/data/data/skyworth.crosswalklibrary/lib/"));
        for(int i = 1;i<files1.length+1;i++){
            Array.set(filesss,i,files1[i-1]);
        }
        ((Field) nativeLibraryDirectories).set(pathList, filesss);
    }

    private static Object getPathList(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(obj, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class cls, String str) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    /**
     *  仅对4.0以上做支持
     * @return
     */
    private static boolean hasDexClassLoader() {
        try {
            Class.forName("dalvik.system.BaseDexClassLoader");
            return true;
        } catch (ClassNotFoundException var1) {
            return false;
        }
    }

}
