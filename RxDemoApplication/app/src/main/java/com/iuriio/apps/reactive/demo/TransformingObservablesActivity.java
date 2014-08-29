package com.iuriio.apps.reactive.demo;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

public class TransformingObservablesActivity extends BaseActivity {
    private final Observable<Integer> integerObservable = Observable.range(1, 10);

    private final Observable.OnSubscribe observer = new Observable.OnSubscribe() {
        @Override
        public void call(Object o) {
            outputText.append(o.toString() + System.getProperty("line.separator"));
        }
    };

    //<editor-fold desc="Boilerplate">
    @Override
    protected int getMenuResource() {
        return R.menu.transforming_observables;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        outputText.setText("");

        switch (id) {
            case R.id.action_transform_map:
                this.transformMap(integerObservable.observeOn(AndroidSchedulers.mainThread()));
                return true;
            case R.id.action_transform_flatmap:
                this.transformFlatMap(integerObservable.observeOn(AndroidSchedulers.mainThread()));
                return true;
            case R.id.action_transform_groupby:
                this.transformGroupBy(integerObservable.observeOn(AndroidSchedulers.mainThread()));
                return true;
            case R.id.action_transform_buffer:
                this.transformBuffer(integerObservable.observeOn(AndroidSchedulers.mainThread()));
                return true;
        }

        return true;
    }

    //</editor-fold>

    private void transformMap(Observable<Integer> observable) {
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

    private void transformGroupBy(final Observable<Integer> observable) {
        observable
                .groupBy(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer value) {
                        return value % 2 == 0;
                    }
                })
                .subscribe(new Action1<GroupedObservable<Boolean, Integer>>() {
                    @Override
                    public void call(final GroupedObservable<Boolean, Integer> observable1) {
                        observable1.map(new Func1<Integer, Object>() {
                            @Override
                            public Object call(final Integer integer) {
                                return observable1.getKey() + ": " + integer;
                            }
                        }).subscribe(observer);
                    }
                });
    }

    private void transformBuffer(Observable<Integer> observable) {
        observable.buffer(3).subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                outputText.append(Arrays.toString(integers.toArray()) + "\n");
            }
        });
    }
}
