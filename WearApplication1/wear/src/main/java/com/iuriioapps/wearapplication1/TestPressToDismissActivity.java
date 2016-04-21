package com.iuriioapps.wearapplication1;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.view.GestureDetector;
import android.view.MotionEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestPressToDismissActivity extends Activity {
    @Bind(R.id.dismiss_overlay)
    protected DismissOverlayView dismissOverlayView;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_press_to_dismiss);

        ButterKnife.bind(this);

        this.dismissOverlayView.setIntroText("Long press into text");
        this.dismissOverlayView.showIntroIfNecessary();

        // Configure a gesture detector
        this.detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                dismissOverlayView.show();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.detector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }
}
