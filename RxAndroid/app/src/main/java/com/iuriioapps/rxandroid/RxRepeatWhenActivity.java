package com.iuriioapps.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxRepeatWhenActivity extends AppCompatActivity {
    private static final String TAG = "RxRepeatWhen";

    private Subscription subscription;

    @BindView(R.id.text)
    protected TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_repeat_when);

        ButterKnife.bind(this);

        Log.i(TAG, "Loading data");
        this.subscription = Observable.defer(this::loadData)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this.text::setText)
                .repeatWhen(completed -> completed.delay(10, TimeUnit.SECONDS))
                .subscribe(
                        __ -> Log.i(TAG, "Data loaded"),
                        e -> Log.i(TAG, "Error loading data: " + e),
                        () -> Log.i(TAG, "Loading data"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.subscription.unsubscribe();
    }

    @RxLogObservable
    private Observable<String> loadData() {
        return Observable.fromCallable(() -> new Date().toString());
    }
}
