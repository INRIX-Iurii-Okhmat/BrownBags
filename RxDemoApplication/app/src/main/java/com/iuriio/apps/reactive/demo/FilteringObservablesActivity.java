package com.iuriio.apps.reactive.demo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class FilteringObservablesActivity extends BaseActivity {
    private final Observable<Integer> integerObservable =
            Observable.range(1, 20).observeOn(AndroidSchedulers.mainThread());

    private final Integer[] items = new Integer[] {  };

    private final Observable.OnSubscribe observer = new Observable.OnSubscribe() {
        @Override
        public void call(Object o) {
            outputText.append(o.toString() + System.getProperty("line.separator"));
        }
    };

    //<editor-fold desc="Boilerplate">
    @Override
    protected int getMenuResource() {
        return R.menu.filtering_observables;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        outputText.setText("");

        switch (id) {
            case R.id.action_filter:
                this.testFilter(this.integerObservable);
                return true;
            case R.id.action_skip:
                this.testSkip(this.integerObservable);
                return true;
            case R.id.action_take:
                this.testTake(this.integerObservable);
                return true;
            case R.id.action_skiptake:
                this.testSkipTake(this.integerObservable);
                return true;
            case R.id.action_takeskip:
                this.testTakeSkip(this.integerObservable);
                return true;
        }

        return false;
    }
    //</editor-fold>

    private void testFilter(final Observable<Integer> observable) {
        observable.filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer value) {
                return value % 2 == 0;
            }
        }).subscribe(observer);
    }

    private void testSkip(final Observable<Integer> observable) {
        observable.skip(5).subscribe(observer);
    }

    private void testTake(final Observable<Integer> observable) {
        observable.take(5).subscribe(observer);
    }

    private void testSkipTake(final Observable<Integer> observable) {
        observable.skip(2).take(5).subscribe(observer);
    }

    private void testTakeSkip(final Observable<Integer> observable) {
        observable.take(5).skip(2).subscribe(observer);
    }
}
