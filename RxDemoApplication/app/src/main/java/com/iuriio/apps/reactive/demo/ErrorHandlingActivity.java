package com.iuriio.apps.reactive.demo;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ErrorHandlingActivity extends BaseActivity {
    private final Observable<String> observable1 =
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("Three");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("Two");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("One");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new Exception("Obserwable 1 terminated with error."));
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread());

    private final Observable<String> observable2 =
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("1");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("2");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("3");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread());

    private final Observable<String> observable3 =
            Observable.create(new Observable.OnSubscribe<String>() {
                private int attempt = 1;

                @Override
                public void call(Subscriber<? super String> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("1");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("2");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext("3");
                    }

                    if (!subscriber.isUnsubscribed()) {
                        if (attempt % 2 == 0) {
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(new Exception("First attempt failed."));
                        }

                        attempt++;
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread());

    private final Subscriber<String> observer = new Subscriber<String>() {
        @Override
        public void onCompleted() {
            outputText.append("Completed.");
        }

        @Override
        public void onError(Throwable e) {
            outputText.append("Error: " + e.getMessage());
        }

        @Override
        public void onNext(String s) {
            outputText.append(s.toString() + System.getProperty("line.separator"));
        }
    };

    //<editor-fold desc="Boilerplate">

    @Override
    protected int getMenuResource() {
        return R.menu.error_handling;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        switch (id) {
            case R.id.action_on_error_resume_next:
                this.testOnErrorResumeNext();
                return true;
            case R.id.action_retry:
                this.testRetry();
                return true;
        }

        return false;
    }

    //</editor-fold>

    private void testOnErrorResumeNext() {
        observable1
                .onErrorResumeNext(observable2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void testRetry() {
        observable3.retry().subscribe(observer);
    }
}
