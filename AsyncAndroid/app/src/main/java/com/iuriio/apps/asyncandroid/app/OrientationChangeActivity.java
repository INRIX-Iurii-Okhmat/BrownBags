package com.iuriio.apps.asyncandroid.app;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class OrientationChangeActivity extends ActionBarActivity {
    private static final String TAG = OrientationChangeActivity.class.getSimpleName();
    private final MainLooperSpy mainLooperSpy = new MainLooperSpy();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");
        if (savedInstanceState == null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "Posted before requesting orientation change");
                }
            });
            Log.d(TAG, "Requesting orientation change");
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            handler.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "Posted after requesting orientation change");
                }
            });
            mainLooperSpy.dumpQueue();
        }
    }
}
