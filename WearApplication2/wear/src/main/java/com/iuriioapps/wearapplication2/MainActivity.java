package com.iuriioapps.wearapplication2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.widget.TextView;

import com.iuriioapps.wearcommon.WearConnector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends WearableActivity implements
        WearConnector.IWearDataChangedListener,
        WearConnector.IWearAssetChangedListener,
        WearConnector.IWearMessageReceivedListener {
    @Bind(android.R.id.widget_frame)
    protected BoxInsetLayout frame;

    @Bind(R.id.counter)
    protected TextView textCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        WearConnector.INSTANCE.addDataChangeListener("/counter", this);
        WearConnector.INSTANCE.addAssetChangeListener("/bg", "bg_2", this);
        WearConnector.INSTANCE.addMessageListener("/color", this);
        WearConnector.INSTANCE.connect(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        WearConnector.INSTANCE.removeDataChangeListener("/counter", this);
        WearConnector.INSTANCE.removeAssetChangeListener("/bg", "bg_2", this);
        WearConnector.INSTANCE.removeMessageListener("/color", this);
        WearConnector.INSTANCE.disconnect();
    }

    @Override
    public void onWearDataChanged(final String path, final Bundle data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textCounter.setText(String.valueOf(data.getInt("counter")));
                textCounter.invalidate();
            }
        });
    }

    @Override
    public void onAssetChanged(String path, String name, final Bitmap asset) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final BitmapDrawable background = new BitmapDrawable(getResources(), asset);
                frame.setBackground(background);
            }
        });
    }

    @Override
    public void onMessageReceived(String path, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int textColor = Color.parseColor(message);
                textCounter.setTextColor(textColor);
            }
        });
    }
}
