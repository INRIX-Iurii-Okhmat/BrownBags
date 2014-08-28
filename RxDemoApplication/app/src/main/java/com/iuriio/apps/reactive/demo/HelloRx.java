package com.iuriio.apps.reactive.demo;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by iuriio on 8/27/14.
 */
public class HelloRx {
    public static void sayHelloTo(String... names) {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

            }
        }).subscribe();

        Observable
                .from(names)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return "Hello, " + s;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println(s);
                    }
                });
    }
}
