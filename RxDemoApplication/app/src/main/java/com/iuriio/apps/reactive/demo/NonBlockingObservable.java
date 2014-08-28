package com.iuriio.apps.reactive.demo;

import rx.Observable;
import rx.Subscriber;

public class NonBlockingObservable {
    public static final Observable<String> create() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                final Thread runThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }

                        try {
                            for (int index = 0; index < 100; index++) {
                                if (subscriber.isUnsubscribed()) {
                                    return;
                                }


                                subscriber.onNext(String.valueOf("value_" + index));
                                try {
                                    Thread.currentThread().sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onCompleted();
                            }
                        } catch (Throwable t) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(t);
                            }
                        }
                    }
                });
                runThread.start();
            }
        });
    }


    //<editor-fold desc="Code blocks">
    // Error.
    // if (index == 50) {
    // throw new InvalidAlgorithmParameterException("You shall not pass index:50!");
    // }
    //</editor-fold>
}
