package com.iuriioapps.rxandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

public class RxMultipleSourcesActivity extends AppCompatActivity {
    private static final String TAG = "MultiSource";
    private Subscription s1;
    private Subscription s2;

    @SuppressLint("RxSubscribeOnError")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_multiple_sources);

        Sources sources = new Sources();

        // Create our sequence for querying best available data
        Observable<Data> source = Observable.concat(
                sources.memory(),
                sources.disk(),
                sources.network()
        ).first(data -> data != null && data.isUpToDate());

        // "Request" latest data once a second
        s1 = Observable.interval(1, TimeUnit.SECONDS)
                .flatMap(__ -> source)
                .subscribe(data -> Log.d(TAG, "Received: " + data.value));

        // Occasionally clear memory (as if app restarted) so that we must go to disk
        s2 = Observable.interval(3, TimeUnit.SECONDS)
                .subscribe(__ -> sources.clearMemory());
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.s1.unsubscribe();
        this.s2.unsubscribe();
    }

    /**
     * Simulates three different sources - one from memory, one from disk,
     * and one from network. In reality, they're all in-memory, but let's
     * play pretend.
     * <p>
     * Observable.create() is used so that we always return the latest data
     * to the subscriber; if you use just() it will only return the data from
     * a certain point in time.
     */
    public static class Sources {
        // Memory cache of data
        private Data memory = null;

        // What's currently "written" on disk
        private Data disk = null;

        // Each "network" response is different
        private int requestNumber = 0;

        // In order to simulate memory being cleared, but data still on disk
        public void clearMemory() {
            Log.d(TAG, "Wiping memory...");
            memory = null;
        }

        public Observable<Data> memory() {
            Observable<Data> observable = Observable.create(subscriber -> {
                subscriber.onNext(memory);
                subscriber.onCompleted();
            });

            return observable.compose(logSource("MEMORY"));
        }

        public Observable<Data> disk() {
            Observable<Data> observable = Observable.create(subscriber -> {
                subscriber.onNext(disk);
                subscriber.onCompleted();
            });

            // Cache disk responses in memory
            return observable.doOnNext(data -> memory = data)
                    .compose(logSource("DISK"));
        }

        public Observable<Data> network() {
            Observable<Data> observable = Observable.create(subscriber -> {
                requestNumber++;
                subscriber.onNext(new Data("Server Response #" + requestNumber));
                subscriber.onCompleted();
            });

            // Save network responses to disk and cache in memory
            return observable.doOnNext(data -> {
                disk = data;
                memory = data;
            }).compose(logSource("NETWORK"));
        }

        // Simple logging to let us know what each source is returning
        Observable.Transformer<Data, Data> logSource(final String source) {
            return dataObservable -> dataObservable.doOnNext(data -> {
                if (data == null) {
                    Log.d(TAG, source + " does not have any data.");
                } else if (!data.isUpToDate()) {
                    Log.d(TAG, source + " has stale data.");
                } else {
                    Log.d(TAG, source + " has the data you are looking for!");
                }
            });
        }

    }

    /**
     * Simple data class, keeps track of when it was created
     * so that it knows when the its gone stale.
     */
    public static class Data {

        private static final long STALE_MS = 5 * 1000; // Data is stale after 5 seconds

        final String value;

        final long timestamp;

        public Data(String value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isUpToDate() {
            return System.currentTimeMillis() - timestamp < STALE_MS;
        }
    }
}
