package com.iuriioapps.rxandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;

public class RxRetryActivity extends AppCompatActivity {
    @BindView(R.id.text)
    protected TextView text;

    @SuppressLint("RxSubscribeOnError")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_retry);

        ButterKnife.bind(this);

        Observable
                .interval(1, TimeUnit.SECONDS)
                .map(Long::intValue)
                .lift(new ErrorOperator())
                .map(String::valueOf)
                .subscribe(text::setText);
    }

    public static class ErrorOperator implements Observable.Operator<Integer, Integer> {
        @Override
        public Subscriber<? super Integer> call(Subscriber<? super Integer> subscriber) {
            return new Subscriber<Integer>(subscriber) {
                @Override
                public void onCompleted() {
                    subscriber.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    subscriber.onError(e);
                }

                @Override
                public void onNext(Integer integer) {
                    subscriber.onNext(integer);
                }
            };
        }
    }
}
