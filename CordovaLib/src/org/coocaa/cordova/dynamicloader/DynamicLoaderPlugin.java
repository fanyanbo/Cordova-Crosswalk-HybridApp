package org.coocaa.cordova.dynamicloader;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by fanyanbo on 2017/8/25.
 */

public class DynamicLoaderPlugin extends CordovaPlugin {

    private static final String Tag = "fyb";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.i(Tag,"DynamicLoaderPlugin action = " + action);
        callbackContext.success();
        return true;
    }

}
