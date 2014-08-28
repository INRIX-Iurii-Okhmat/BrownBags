package com.iuriio.apps.reactive.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;


public class CreatingObservablesActivity extends Activity {
    @InjectView(R.id.output)
    TextView outputText;

    // <editor-fold desc="Boilerplate">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_observables);

        ButterKnife.inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creating_observables, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.outputText.setText("");

        int id = item.getItemId();
        switch (id) {
            case R.id.action_create_observable_from:
                this.observe(this.testFromObservable());
                return true;
            case R.id.action_create_observable_create:
                this.observe(this.testCreateObservable());
                return true;
            case R.id.action_create_observable_defer:
                this.observe(this.testDeferObservable());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // </editor-fold>

    private Observable<String> testFromObservable() {
        final String[] items = new String[]{"item1", "item2", "item3"};
        return Observable.from(items);
    }

    private Observable<Long> testCreateObservable() {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            private Timer timer = new Timer();

            @Override
            public void call(final Subscriber<? super Long> subscriber) {
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }

                        subscriber.onNext(System.currentTimeMillis());
                    }
                }, 1000, 1000);
            }
        });
    }

    private Observable<Integer> testDeferObservable() {
        return Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.from(new Integer[] {1, 2, 3, 4, 5});
            }
        });
    }

    //<editor-fold desc="Observe">
    private Subscription subscriber;

    private void observe(Observable<?> observable) {
        if(this.subscriber != null && !this.subscriber.isUnsubscribed()) {
            this.subscriber.unsubscribe();
        }

        this.subscriber = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        outputText.setText(outputText.getText() + o.toString() + "\n");
                    }
                });
    }
    //</editor-fold>
}
