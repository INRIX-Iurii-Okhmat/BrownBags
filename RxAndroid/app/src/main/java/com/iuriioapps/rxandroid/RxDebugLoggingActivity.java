package com.iuriioapps.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RxDebugLoggingActivity extends AppCompatActivity {
    private final CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_debug_logging);

        this.subscription.add(this.getObservable().subscribe(
                l -> Log.d("Debug", String.valueOf(l)),
                e -> Log.e("Debug", "Error: " + e)
        ));
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.subscription.unsubscribe();
    }

    @RxLogObservable
    protected Observable<Long> getObservable() {
        return Observable.interval(1, TimeUnit.SECONDS);
    }
}
