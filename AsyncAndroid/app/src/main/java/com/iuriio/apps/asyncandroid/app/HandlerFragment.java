package com.iuriio.apps.asyncandroid.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by IuriiO on 4/2/2014.
 */
public class HandlerFragment extends ListFragment {
    private ArrayList<String> model;
    private ArrayAdapter<String> adapter;

    private Thread runner;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            final String text = msg.getData().getString("value");
            adapter.add(text);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        if (model == null) {
            model=new ArrayList<String>();
        }

        adapter=
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        model);

        getListView().setScrollbarFadingEnabled(false);
        setListAdapter(adapter);

        this.runner = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int index = 0; index < 100; index++) {
                    try {
                        final Bundle data = new Bundle();
                        data.putString("value", "Item " + String.valueOf(index));
                        final Message message = handler.obtainMessage();
                        message.setData(data);
                        handler.sendMessage(message);

                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        this.runner.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.runner != null) {
            this.runner.interrupt();
        }
    }
}
