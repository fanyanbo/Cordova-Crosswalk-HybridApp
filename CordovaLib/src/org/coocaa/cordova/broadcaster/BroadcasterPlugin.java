package org.coocaa.cordova.broadcaster;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by fanyanbo on 2017/8/25.
 */

public class BroadcasterPlugin extends CordovaPlugin {

    private static final String Tag = "fyb";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.i(Tag,"BroadcasterPlugin action = " + action);

        return true;
    }
}
