package com.iuriioapps.wearapplication1;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class TestAmbientActivity extends WearableActivity {
    private TextView labelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_ambient);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                labelText = (TextView) stub.findViewById(R.id.text);
                enterActiveState();
            }
        });

        this.setAmbientEnabled();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        this.enterInactiveState();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        this.enterActiveState();
    }

    private void enterInactiveState() {
        //noinspection deprecation
        this.labelText.setTextColor(this.getResources().getColor(R.color.white));
        this.labelText.getPaint().setAntiAlias(false);
    }

    private void enterActiveState() {
        //noinspection deprecation
        this.labelText.setTextColor(this.getResources().getColor(android.R.color.holo_orange_light));
        this.labelText.getPaint().setAntiAlias(true);
    }
}
