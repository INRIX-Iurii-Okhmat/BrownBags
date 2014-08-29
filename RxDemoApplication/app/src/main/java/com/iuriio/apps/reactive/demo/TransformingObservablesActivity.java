package com.iuriio.apps.reactive.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;


public class TransformingObservablesActivity extends Activity {
    private final Observable<Integer> integerObservable = Observable.range(1, 10);

    private final Observable.OnSubscribe observer = new Observable.OnSubscribe() {
        @Override
        public void call(Object o) {
            outputText.append(o.toString() + System.getProperty("line.separator"));
        }
    };

    @InjectView(R.id.output)
    TextView outputText;

    //<editor-fold desc="Boilerplate">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transforming_observables);

        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transforming_observables, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        outputText.setText("");

        switch (item.getItemId()) {
            case R.id.action_transform_map:
                this.trasformMap(integerObservable.observeOn(AndroidSchedulers.mainThread()));
                return true;
            case R.id.action_transform_flatmap:
                this.transformFlatMap(integerObservable.observeOn(AndroidSchedulers.mainThread()));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    private void trasformMap(Observable<Integer> observable) {
        observable.map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer o) {
                return (o * o);
            }
        }).subscribe(this.observer);
    }

    private void transformFlatMap(Observable<Integer> observable) {
        observable.concatMap(new Func1<Integer, Observable<Number>>() {
            @Override
            public Observable<Number> call(Integer value) {
                return Observable.from(
                        new Number[]{
                                Math.log10(value),
                                Math.pow(10, value)
                        }
                );
            }
        }).subscribe(this.observer);
    }
}
