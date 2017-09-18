package com.example.fanyanbo.cordova_crosswalk_hybridapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyworth.framework.skysdk.ipc.SkyActivity;
import com.skyworth.framework.skysdk.util.StreamGobbler;
import com.tianci.system.api.TCSystemService;
import com.tianci.system.data.TCInfoSetData;
import com.tianci.system.data.TCSetData;
import com.tianci.system.define.TCEnvKey;

/**
 * Created by fanyanbo on 2017/9/13.
 */

public class CoocaaActivity extends SkyActivity {

    private static final String Tag = "fyb";
    private int core = 0;
    private TCSystemService mSystemApi;
    private TextView mTextView;
    private FrameLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mMainLayout = new FrameLayout(this);
//
//        mTextView = new TextView(this);
//        mTextView.setText("test");
//
//        FrameLayout.LayoutParams textview_p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
//        textview_p.gravity = Gravity.CENTER;
//        mMainLayout.addView(mTextView, textview_p);

        LinearLayout activity_coocaa = (LinearLayout) getLayoutInflater().inflate( R.layout.activity_coocaa, null);

        mTextView = (TextView) activity_coocaa.findViewById(R.id.textView);
        mTextView.setText("hi,man!");

        setContentView(activity_coocaa);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.i(Tag,"onKeyDown keyCode = " + keyCode);
        if(KeyEvent.KEYCODE_1 == event.getKeyCode()){
//            TCSetData typeSetData = mSystemApi.getSetData(TCEnvKey.SKY_SYSTEM_ENV_TYPE  );
//            String typeString = "";
//            if(typeSetData!=null)
//            {
//                TCInfoSetData typeInfoData = (TCInfoSetData) typeSetData;
//                typeString = typeInfoData.getCurrent();
//            }
//            Log.i(Tag,"typeString = " + typeString);

            Intent mIntent = new Intent("android.intent.action.crosswalk");
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.putExtra("core",core);
            startActivity(mIntent);
        }else if (KeyEvent.KEYCODE_2 == event.getKeyCode()){
            if(core == 0){
                core = 1;
            }else{
                core = 0;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText((core == 0) ? "System Webview" : "Crosswalk WebView");
                }
            });
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCmdConnectorInit() {
        Log.i(Tag,"CoocaaActivity onCmdConnectorInit");
        mSystemApi = new TCSystemService(this);
    }

    @Override
    public byte[] onHandler(String fromtarget, String cmd, byte[] body) {
        return new byte[0];
    }

    @Override
    public void onResult(String fromtarget, String cmd, byte[] body) {

    }

    @Override
    public byte[] requestPause(String fromtarget, String cmd, byte[] body) {
        return new byte[0];
    }

    @Override
    public byte[] requestResume(String fromtarget, String cmd, byte[] body) {
        return new byte[0];
    }

    @Override
    public byte[] requestRelease(String fromtarget, String cmd, byte[] body) {
        return new byte[0];
    }

    @Override
    public byte[] requestStartToVisible(String fromtarget, String cmd, byte[] body) {
        return new byte[0];
    }

    @Override
    public byte[] requestStartToForground(String fromtarget, String cmd, byte[] body) {
        return new byte[0];
    }
}
