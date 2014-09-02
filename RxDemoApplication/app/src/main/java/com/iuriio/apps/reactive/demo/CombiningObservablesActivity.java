package com.iuriio.apps.reactive.demo;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;

public class CombiningObservablesActivity extends BaseActivity {
    private final Observable<Integer> oddNumberObservables =
            Observable.from(new Integer[] {  1, 3, 5, 7, 9 });

    private final Observable<Integer> evenNumberObservables =
            Observable.from(new Integer[] {  2, 4, 6, 8, 10 });

    private final Observable.OnSubscribe observer = new Observable.OnSubscribe() {
        @Override
        public void call(Object o) {
            outputText.append(o.toString() + System.getProperty("line.separator"));
        }
    };

    //<editor-fold desc="Boilerplate">

    @Override
    protected int getMenuResource() {
        return R.menu.combining_observables;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        switch (id) {
            case R.id.action_merge:
                this.testMerge();
                return true;
            case R.id.action_zip:
                this.testZip();
                return true;
        }

        return false;
    }

    //</editor-fold>

    private void testMerge() {
        Observable
                .merge(oddNumberObservables, evenNumberObservables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.observer);

        // Another alternative:
        // oddNumberObservables.mergeWith(evenNumberObservables)
    }

    private void testZip() {
        Observable
                .zip(oddNumberObservables, evenNumberObservables, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this.observer);
    }
}
