package com.iuriioapps.rxandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class RxLifecycleActivity extends RxAppCompatActivity {
    private static final String TAG = RxLifecycleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @SuppressLint("RxSubscribeOnError")
    @OnClick(R.id.btn)
    protected void onBtnClick() {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.PAUSE))
                .doOnUnsubscribe(() -> Log.i(TAG, "Unsubscribing"))
                .subscribe((num) -> Log.i(TAG, "Running until onPause(): " + num));
    }
}
