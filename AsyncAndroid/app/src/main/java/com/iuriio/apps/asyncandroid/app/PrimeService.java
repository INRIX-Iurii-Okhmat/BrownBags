package com.iuriio.apps.asyncandroid.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrimeService extends Service {
    public interface IPrimeServiceCallback {
        void onPrimeNumberFound(long number);
    }

    public final class PrimeServiceBinder extends Binder {
        public PrimeService getService() {
            return PrimeService.this;
        }
    }

    private ExecutorService executor;
    private WeakReference<IPrimeServiceCallback> callback;

    public PrimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PrimeServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        this.executor.shutdownNow();
        return super.onUnbind(intent);
    }

    public void calculatePrime(final IPrimeServiceCallback listener) {
        this.callback = new WeakReference<IPrimeServiceCallback>(listener);
        this.executor = Executors.newSingleThreadExecutor();
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("PrimeCalcThread");
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

                BigInteger prime = new BigInteger("2");
                for (int iteration = 0; iteration < 500; iteration++) {
                    prime = prime.nextProbablePrime();

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        return;
                    }

                    if (callback.get() != null) {
                        //noinspection ConstantConditions
                        callback.get().onPrimeNumberFound(prime.longValue());
                    } else {
                        return;
                    }
                }
            }
        });
    }
}
