package org.coocaa.cordova.api;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by fanyanbo on 2017/9/18.
 */

public class CoocaaApiPlugin extends CordovaPlugin{

    private static final String Tag = "fyb";
    private static final String WAIT_OS_READY = "waitForOSReady";//判断酷开系统是否bind成功。


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Log.i(Tag,"CoocaaApiPlugin action = " + action);
        if(WAIT_OS_READY.equals(action))
        {
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                 callbackContext.success();
                }
            });
            return true;
        }

        return true;
    }
}
