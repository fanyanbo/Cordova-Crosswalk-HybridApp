package org.fyb.myplugin;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by fanyanbo on 2017/8/24.
 */

public class MyPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if("test".equals(action)){
            Log.i("fyb","fyb,oh my god!!");
        }
        callbackContext.success();
        return true;
    }
}
