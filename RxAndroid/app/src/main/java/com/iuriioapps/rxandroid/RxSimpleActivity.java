package com.iuriioapps.rxandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class RxSimpleActivity extends AppCompatActivity {
    @BindView(R.id.btn)
    protected Button btn;

    @BindView(R.id.text)
    protected TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.btn.setOnClickListener((view) -> simpleObservable());
    }

    @SuppressLint("RxSubscribeOnError")
    private void simpleObservable() {
        final String[] words = {"Lorem", "ipsum", "dolor", "sit", "amet"};

        Observable.just(words)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .reduce((s, s1) -> String.format("%s %s", s, s1))
                .subscribe(text::setText);
    }
}
