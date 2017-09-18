package com.example.fanyanbo.cordova_crosswalk_hybridapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.*;

public class MainActivity extends CordovaActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
//        launchUrl = "http://beta.webapp.skysrt.com/appstore/ad2/index.html";
//        launchUrl = "http://beta.webapp.skysrt.com/lxw/H5/video.html";
//        launchUrl = "http://beta.webapp.skysrt.com/lxw/NFC/index.html";

        int core = extras.getInt("core", 0);

        Log.i("fyb","Tag 7 launchUrl = " + launchUrl + ", core = " + core);

        setCore(core);
        loadUrl(launchUrl);

    }
}
