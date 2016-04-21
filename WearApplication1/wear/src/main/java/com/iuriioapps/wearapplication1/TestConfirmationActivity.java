package com.iuriioapps.wearapplication1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestConfirmationActivity extends Activity {
    @Bind(R.id.delayed_confirm)
    protected DelayedConfirmationView delayedConfirmationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_confirmation);

        ButterKnife.bind(this);

        this.delayedConfirmationView.setTotalTimeMs(2000);
        this.delayedConfirmationView.start();
        this.delayedConfirmationView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                Intent intent = new Intent(TestConfirmationActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Message was sent");
                startActivity(intent);
            }

            @Override
            public void onTimerSelected(View view) {
                Intent intent = new Intent(TestConfirmationActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Message canceled");
                startActivity(intent);
            }
        });
    }
}
