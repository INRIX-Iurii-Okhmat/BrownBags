package com.iuriioapps.rxandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

public class RxAndroidSchedulerActivity extends AppCompatActivity {
    @BindView(R.id.text)
    protected TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_android_scheduler);

        ButterKnife.bind(this);
    }

    // @OnClick(R.id.btn_start)
    protected void onStartClick() {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .map(String::valueOf)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        text -> this.text.setText(text),
                        e -> Log.e("ERROR", e.toString()));
    }

    @SuppressLint("RxSubscribeOnError")
    @OnClick(R.id.btn_start)
    protected void onStartClick2() {
        // Subscribes on computation scheduler by default.
        Async.start(() -> getRandom())
                .observeOn(AndroidSchedulers.mainThread())
                .map(String::valueOf)
                .subscribe(text::setText);
    }

    private int getRandom() {
        SystemClock.sleep(2000);
        return 4;
    }
}
