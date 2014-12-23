package com.iuriio.apps.reactive.demo;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UtilityMethodsObservableActivity extends BaseActivity {
    private final Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            for (int index = 0; index < 100; index++) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }

                System.out.printf("%s - %s\n", Thread.currentThread().getName(), index);
                subscriber.onNext(index);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }
    });

    private final Observable.OnSubscribe observer = new Observable.OnSubscribe() {
        @Override
        public void call(Object o) {
            System.out.printf("%s - %s\n", Thread.currentThread().getName(), o.toString());
            outputText.append(o.toString() + "\n");
        }
    };

    //<editor-fold desc="Boilerplate">

    @Override
    protected int getMenuResource() {
        return R.menu.utility_methods_observable;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        switch (id) {
            case R.id.action_subscribeOn_observeOn:
                this.testSubscribeOnObserveOn();
                break;
        }

        return false;
    }

    //</editor-fold>

    private void testSubscribeOnObserveOn() {
        observable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
