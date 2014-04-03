package com.iuriio.apps.asyncandroid.app;



import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class StrictModeFragment extends Fragment implements View.OnClickListener {


    public StrictModeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setRetainInstance(true);

        enableStrictMode();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void disableStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disableStrictMode();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View content = inflater.inflate(R.layout.fragment_strict_mode, container, false);

        content.findViewById(R.id.strict_mode_cause_problems).setOnClickListener(this);

        return content;
    }


    @Override
    public void onClick(View v) {
        try {
            final URL url = new URL("http://www.google.com");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();

            Toast.makeText(this.getActivity(), inputLine, Toast.LENGTH_LONG).show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
