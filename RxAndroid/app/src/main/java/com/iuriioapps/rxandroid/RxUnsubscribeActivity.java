package com.iuriioapps.rxandroid;

import android.os.Bundle;
import android.widget.Toast;

import com.iuriioapps.rxandroid.DAL.Error;
import com.iuriioapps.rxandroid.DAL.Incident;
import com.iuriioapps.rxandroid.DAL.IncidentManager.IResponse;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.List;

public class RxUnsubscribeActivity extends RxAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_unsubscribe);

        DAL.INCIDENT_MANAGER.getIncidentsAsync(new IResponse() {
            @Override
            public void onData(List<Incident> data) {
                getWindow().getDecorView().getHandler().post(() -> showToast(data));
            }

            @Override
            public void onError(Error error) {

            }
        });

        // DAL.getIncidents().subscribe(this::showToast);
    }


    public void showToast(final List<Incident> data) {
        Toast.makeText(RxUnsubscribeActivity.this, "Received: " + data.size(), Toast.LENGTH_SHORT).show();
    }
}
