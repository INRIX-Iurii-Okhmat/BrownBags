package com.iuriio.apps.asyncandroid.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * ADB commands
 *
 * adb shell
 * su
 * ps
 * kill -3 pid
 *
 * cd /data/anr
 * adb pull /data/anr/traces.txt
 */
public class DeadlockFragment extends Fragment implements View.OnClickListener {
    private static long sleepMillis = 1;
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    private TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_deadlock, container, false);
        content.findViewById(R.id.deadlock_run).setOnClickListener(this);
        this.text = (TextView) content.findViewById(R.id.deadlock_text);
        return content;
    }

    @Override
    public void onClick(View v) {
        doTest();
    }

    private void doTest() {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                lock12();
            }
        }, "LockThread 1");
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                lock21();
            }
        }, "LockThread 2");
        t1.start();
        t2.start();
    }

    private void lock12() {
        Log.d(Thread.currentThread().getName(), "Entering lock12");

        synchronized (lock1) {
            Log.d(Thread.currentThread().getName(), "Locked on 1st lock");
            sleep();
            synchronized (lock2) {
                Log.d(Thread.currentThread().getName(), "Locked on 2nd lock");
                sleep();
            }
        }
    }

    private void lock21() {
        Log.d(Thread.currentThread().getName(), "Entering lock21");

        synchronized (lock2) {
            Log.d(Thread.currentThread().getName(), "Locked on 2nd lock");
            sleep();
            synchronized (lock1) {
                Log.d(Thread.currentThread().getName(), "Locked on 1st lock");
                sleep();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
