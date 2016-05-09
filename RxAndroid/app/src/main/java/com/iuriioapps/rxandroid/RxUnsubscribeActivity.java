package com.iuriioapps.rxandroid;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.iuriioapps.rxandroid.DAL.Error;
import com.iuriioapps.rxandroid.DAL.Incident;
import com.iuriioapps.rxandroid.DAL.IncidentManager.IResponse;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxUnsubscribeActivity extends RxAppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_unsubscribe);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        DAL.INCIDENT_MANAGER.getIncidentsAsync(new IResponse() {
//            @Override
//            public void onData(List<Incident> data) {
//                handler.post(() -> showData(data));
//            }
//
//            @Override
//            public void onError(Error error) {
//                handler.post(() -> showError(error));
//            }
//        });

        DAL.getIncidents()
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> showData(data),
                        error -> showError(error));
    }

    public void showData(final List<Incident> data) {
        Toast.makeText(RxUnsubscribeActivity.this, "Received: " + data.size(), Toast.LENGTH_SHORT).show();
    }

    public void showError(final Throwable error) {
        Toast.makeText(RxUnsubscribeActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
