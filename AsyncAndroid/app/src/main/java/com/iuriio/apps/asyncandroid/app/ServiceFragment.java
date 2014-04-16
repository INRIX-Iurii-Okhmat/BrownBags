package com.iuriio.apps.asyncandroid.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class ServiceFragment extends Fragment implements View.OnClickListener, PrimeService.IPrimeServiceCallback {
    private ArrayAdapter<String> adapter;
    private ServiceConnection connection;
    private PrimeService service;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final List<String> initData = new LinkedList<String>();
        this.adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                initData);
        this.adapter.setNotifyOnChange(true);

        final View content = inflater.inflate(R.layout.fragment_service, container, false);
        assert content != null;

        content.findViewById(R.id.svc_run).setOnClickListener(this);
        ((ListView)content.findViewById(R.id.svc_prime_list)).setAdapter(this.adapter);

        return content;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                PrimeService.PrimeServiceBinder psb = (PrimeService.PrimeServiceBinder) binder;
                service = psb.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                connection = null;
                service = null;
            }
        };

        this.getActivity().bindService(new Intent(getActivity(), PrimeService.class), this.connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        this.service = null;
        if (this.connection != null) {
            this.getActivity().unbindService(this.connection);
        }
    }

    @Override
    public void onClick(View v) {
        this.service.calculatePrime(this);
    }

    @Override
    public void onPrimeNumberFound(final long number) {
        if (this.getActivity() == null) {
            return;
        }

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(String.valueOf(number));
            }
        });
    }
}
