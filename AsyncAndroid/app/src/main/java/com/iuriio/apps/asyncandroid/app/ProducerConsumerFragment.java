package com.iuriio.apps.asyncandroid.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumerFragment extends Fragment implements View.OnClickListener {
    private static final class Dish {
        private final String name;

        public Dish(final String name) {
            this.name = name;
        }

        public final String getName() {
            return this.name;
        }
    }

    public final class Chef implements Runnable {
        private BlockingQueue<Dish> dishes;

        public Chef(final BlockingQueue<Dish> dishes) {
            this.dishes = dishes;
        }

        @Override
        public void run() {
            try {
                updateText("Prepared pizza");
                this.dishes.put(new Dish("Pizza"));

                Thread.sleep(3 * (long)(Math.random() * 1000));
                updateText("Prepared meatloaf");
                this.dishes.put(new Dish("Meatloaf"));

                Thread.sleep(5 * (long) (Math.random() * 1000));
                updateText("Prepared cake and casserole");
                this.dishes.put(new Dish("Cake"));
                this.dishes.put(new Dish("Casserole"));
            } catch (InterruptedException e) {
                updateText("Going home early today!");
            }
        }
    }

    public final class Waiter implements Runnable {
        private final BlockingQueue<Dish> dishes;

        public Waiter(final BlockingQueue<Dish> dishes) {
            this.dishes = dishes;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    final Dish dish = dishes.take();
                    updateText("Served: " + dish.getName());
                } catch (InterruptedException e) {
                    updateText("Going home early today");
                }
            }
        }
    }

    private Thread waiterThread;
    private Thread chefThread;
    private Handler uiHandler;
    private TextView text;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        final LinkedBlockingQueue<Dish> dishes = new LinkedBlockingQueue<Dish>();
        this.chefThread = new Thread(new Chef(dishes));
        this.waiterThread = new Thread(new Waiter(dishes));
        this.uiHandler = this.getActivity().getWindow().getDecorView().getHandler();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (this.waiterThread != null) {
            this.waiterThread.interrupt();
            this.waiterThread = null;
        }

        if (this.chefThread != null) {
            this.chefThread.interrupt();
            this.chefThread = null;
        }

        this.uiHandler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_cyclic_barrier, container, false);

        content.findViewById(R.id.cyclic_barrier_run).setOnClickListener(this);
        this.text = (TextView) content.findViewById(R.id.cyclic_barrier_text);

        return content;
    }

    @Override
    public void onClick(View v) {
        this.chefThread.start();
        this.waiterThread.start();
    }

    private synchronized void updateText(final String value) {
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
