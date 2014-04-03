package com.iuriio.apps.asyncandroid.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by IuriiO on 4/2/2014.
 */
public class AsyncTaskFragment extends ListFragment {
    private static final String[] items= { "lorem", "ipsum", "dolor",
            "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi",
            "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam",
            "vel", "erat", "placerat", "ante", "porttitor", "sodales",
            "pellentesque", "augue", "purus" };
    private ArrayList<String> model=null;
    private ArrayAdapter<String> adapter=null;
    private AddStringTask task=null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);

        if (model == null) {
            model=new ArrayList<String>();
            task=new AddStringTask();
            task.execute();
        }

        adapter=
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        model);

        getListView().setScrollbarFadingEnabled(false);
        setListAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        if (task != null) {
            task.cancel(false);
        }

        super.onDestroy();
    }

    class AddStringTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            for (String item : items) {
                if (!isCancelled()) {
                    publishProgress(item);
                    SystemClock.sleep(400);
                }
            }

            return(null);
        }

        @Override
        protected void onProgressUpdate(String... item) {
            adapter.add(item[0]);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (!isCancelled()) {
                Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT)
                        .show();
            }

            task=null;
        }
    }
}
