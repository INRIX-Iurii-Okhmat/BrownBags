package com.iuriio.apps.asyncandroid.app;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierFragment extends Fragment implements View.OnClickListener {
    private TextView text;
    private Handler uiHandler;

    private final CyclicBarrier cb = new CyclicBarrier(3, new Runnable() {
        @Override
        public void run() {
            if (uiHandler != null) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateText("All parties reached the barrier.");
                    }
                });
            }
        }
    });

    private final class ThreadTask implements Runnable {
        private final CyclicBarrier barrier;

        public ThreadTask(final CyclicBarrier cb) {
            this.barrier = cb;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((long)(Math.random() * 1000));
                updateText(Thread.currentThread().getName() + " reached barrier.");
                barrier.await();

                Thread.sleep(3 * (long)(Math.random() * 1000));
                updateText(Thread.currentThread().getName() + " crossed barrier.");
            } catch (InterruptedException e) {
                updateText("Thread interrupted");
            } catch (BrokenBarrierException e) {
                updateText("Barrier broken.");
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.uiHandler = this.getActivity().getWindow().getDecorView().getHandler();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        this.uiHandler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_cyclic_barrier, container, false);

        assert content != null;
        content.findViewById(R.id.cyclic_barrier_run).setOnClickListener(this);
        this.text = (TextView) content.findViewById(R.id.cyclic_barrier_text);

        return content;
    }


    @Override
    public void onClick(View v) {
        final Thread t1 = new Thread(new ThreadTask(this.cb), "Worker 1");
        final Thread t2 = new Thread(new ThreadTask(this.cb), "Worker 2");
        final Thread t3 = new Thread(new ThreadTask(this.cb), "Worker 3");

        t1.start();
        t2.start();
        t3.start();
    }

    private synchronized void updateText(final String value) {
        Log.d(CyclicBarrierFragment.class.getSimpleName(), value);

        if (uiHandler != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    text.setText(text.getText() + "\n" + value);
                }
            });
        }
    }
 }
