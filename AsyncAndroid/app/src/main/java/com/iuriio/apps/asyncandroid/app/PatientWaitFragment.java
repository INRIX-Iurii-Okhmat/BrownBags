package com.iuriio.apps.asyncandroid.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class PatientWaitFragment extends Fragment implements View.OnClickListener {
    private TextView text;

    public PatientWaitFragment() {
        // Required empty public constructor
    }

    private void updateText(final String message) {
        this.text.post(new Runnable() {
            @Override
            public void run() {
                text.setText(text.getText() + "\n" + message);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View content = inflater.inflate(R.layout.fragment_patient_wait, container, false);
        final Button start = (Button) content.findViewById(R.id.message_queue_start);
        start.setOnClickListener(this);
        this.text = (TextView) content.findViewById(R.id.message_queue_text);
        return content;
    }

    @Override
    public void onClick(View v) {
        updateText("Starting patient thread");
        new PatientThread().start();
    }

    private final class PatientThread extends Thread {
        //long patience = 1000 * 60 * 60;
        long patience = 10000;

        @Override
        public void run() {
            super.run();

            long startTime = System.currentTimeMillis();
            final Thread t = new Thread(new MQRunnable());
            t.start();

            try {
                while (t.isAlive()) {
                    updateText("PT: Still waiting...");
                    // Wait maximum of 1 second
                    // for MessageLoop thread
                    // to finish.
                    t.join(1000);
                    if (((System.currentTimeMillis() - startTime) > patience)
                            && t.isAlive()) {
                        updateText("PT: Enough is enough!");
                        t.interrupt();
                        // Shouldn't be long now
                        // -- wait indefinitely
                        t.join();
                    }
                }
                updateText("PT: Finally!");
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private final class MQRunnable implements Runnable {

        @Override
        public void run() {
            final String[] messages = new String[] {
                    "Message 1",
                    "Message 2",
                    "Message 3",
                    "Message 4"
            };
            try {
                for (String message : messages) {
                    updateText("MQ: " + message);
                    Thread.sleep(4000);
                }
            }catch(InterruptedException e){
                updateText("MQ: I wasn't done!");
            }
        }
    }
}
