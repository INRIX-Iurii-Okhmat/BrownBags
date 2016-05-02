package com.iuriioapps.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;

import rx.Observable;
import rx.Subscription;

public class RxSideeffectMethodsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_sideeffect_methods);

        Observable<String> o = Observable
                .from(Arrays.asList(new Integer[]{2, 3, 5, 7, 11}))
                .doOnNext(s -> Log.d("LOG1", s.toString()))
                .filter(prime -> prime % 2 != 0)
                .doOnNext(s -> Log.d("LOG2", s.toString()))
                .count()
                .doOnNext(s -> Log.d("LOG3", s.toString()))
                .map(number -> String.format("Contains %d elements", number));

        Subscription subscription = o.subscribe(
                System.out::println,
                System.out::println,
                () -> System.out.println("Completed!"));

        subscription.unsubscribe();
    }
}
