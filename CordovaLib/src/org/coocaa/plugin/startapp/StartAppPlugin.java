package org.coocaa.plugin.startapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;

import java.util.Iterator;

/**
 * Created by fanyanbo on 2017/8/25.
 */

public class StartAppPlugin extends CordovaPlugin {

    private static final String Tag = "fyb";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        Log.i(Tag,"StartAppPlugin action = " + action);
        if (action.equals("start")) {
            this.start(args, callbackContext);
        }
        else if(action.equals("check")) {
            Log.i(Tag,"StartAppPlugin args = " + args.getString(0));
            this.check(args.getString(0), callbackContext);
        }

        return true;
    }

    public void start(JSONArray args, CallbackContext callback) {

        Intent LaunchIntent;

        String com_name = null;

        String activity = null;
        String spackage = null;
        String intetype = null;
        String intenuri = null;

        try {
            if (args.get(0) instanceof JSONArray) {
                com_name = args.getJSONArray(0).getString(0);
                activity = args.getJSONArray(0).getString(1);

                if(args.getJSONArray(0).length() > 2) {
                    spackage = args.getJSONArray(0).getString(2);
                }

                if(args.getJSONArray(0).length() > 3) {
                    intetype = args.getJSONArray(0).getString(3);
                }

                if(args.getJSONArray(0).length() > 4) {
                    intenuri = args.getJSONArray(0).getString(4);
                }
            }
            else {
                com_name = args.getString(0);
            }

            /**
             * call activity
             */
            if(activity != null) {
                if(com_name.equals("action")) {
                    /**
                     * . < 0: VIEW
                     * . >= 0: android.intent.action.VIEW
                     */
                    if(activity.indexOf(".") < 0) {
                        activity = "android.intent.action." + activity;
                    }

                    // if uri exists
                    if(intenuri != null) {
                        LaunchIntent = new Intent(activity, Uri.parse(intenuri));
                    }
                    else {
                        LaunchIntent = new Intent(activity);
                    }
                }
                else {
                    LaunchIntent = new Intent();
                    LaunchIntent.setComponent(new ComponentName(com_name, activity));
                }
            }
            else {
                LaunchIntent = this.cordova.getActivity().getPackageManager().getLaunchIntentForPackage(com_name);
            }

            /**
             * setPackage, http://developer.android.com/intl/ru/reference/android/content/Intent.html#setPackage(java.lang.String)
             */
            if(spackage != null) {
                LaunchIntent.setPackage(spackage);
            }

            /**
             * setType, http://developer.android.com/intl/ru/reference/android/content/Intent.html#setType(java.lang.String)
             */
            if(intetype != null) {
                LaunchIntent.setType(intetype);
            }

            /**
             * put arguments
             */
            if(args.length() > 1) {
                JSONArray params = args.getJSONArray(1);
                String key;
                Object value;

                for(int i = 0; i < params.length(); i++) {
                    if (params.get(i) instanceof JSONObject) {
                        Iterator<String> iter = params.getJSONObject(i).keys();

                        while (iter.hasNext()) {
                            key = iter.next();
                            try {
                                JSONObject obj = params.getJSONObject(i);
                                if(obj.optBoolean(key))
                                {
                                    value = obj.getBoolean(key);
                                    LaunchIntent.putExtra(key, (Boolean)value);
                                }
                                else
                                {
                                    value = obj.getString(key);
                                    LaunchIntent.putExtra(key, (String)value);
                                }
                            } catch (JSONException e) {
                                callback.error("json params: " + e.toString());
                            }
                        }
                    }
                    else {
                        LaunchIntent.setData(Uri.parse(params.getString(i)));
                    }
                }
            }
            /**
             * start activity
             */
            this.cordova.getActivity().startActivity(LaunchIntent);
            callback.success();

        } catch (JSONException e) {
            callback.error("json: " + e.toString());
        } catch (Exception e) {
            callback.error("intent: " + e.toString());
        }
    }

    public void check(String component, CallbackContext callback) {
        PackageManager pm = this.cordova.getActivity().getApplicationContext().getPackageManager();
        try {
            /**
             * get package info
             */
            PackageInfo PackInfo = pm.getPackageInfo(component, PackageManager.GET_ACTIVITIES);

            /**
             * create json object
             */
            JSONObject info = new JSONObject();

            info.put("versionName", PackInfo.versionName);
            info.put("packageName", PackInfo.packageName);
            info.put("versionCode", PackInfo.versionCode);
            info.put("applicationInfo", PackInfo.applicationInfo);

            Log.i(Tag,"StartAppPlugin check info = " + info.toString());

            callback.success(info);
        } catch (Exception e) {
            callback.error(e.toString());
        }
    }
}
