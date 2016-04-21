package com.iuriioapps.wearapplication2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.iuriioapps.wearcommon.WearConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    private static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    @Bind(R.id.status)
    protected TextView textStatus;

    @Bind(R.id.btnSendData)
    protected Button btnSendData;

    @Bind(R.id.btnSendAsset)
    protected Button btnSendAsset;

    @Bind(R.id.btnSendMessage)
    protected Button btnSendMessage;

    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logger.trace("Creating main activity.");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyDeath()
                .build());

        this.setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        WearConnector.INSTANCE.connect(this);
        WearConnector.INSTANCE.setConnectionListener(new WearConnector.IConnectionListener() {
            @Override
            public void onConnected() {
                textStatus.setText("Connected");
                btnSendData.setEnabled(true);
                btnSendAsset.setEnabled(true);
                btnSendMessage.setEnabled(true);
            }

            @Override
            public void onDisconnected() {
                textStatus.setText("Disconnected");
                btnSendData.setEnabled(false);
                btnSendAsset.setEnabled(false);
                btnSendMessage.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WearConnector.INSTANCE.disconnect();
    }

    @OnClick(R.id.btnSendData)
    public void onSendDataButtonClick() {
        final Bundle data = new Bundle();
        data.putInt("counter", this.counter++);
        WearConnector.INSTANCE.postData("/counter", data);
    }

    @OnClick(R.id.btnSendAsset)
    public void onSendAssetButtonClick() {
        WearConnector.INSTANCE.postAsset("/bg", "bg_2", R.drawable.bg_2);
    }

    @OnClick(R.id.btnSendMessage)
    public void onSendMessageButtonClick() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        String strColor = String.format("#%06X", 0xFFFFFF & color);

        WearConnector.INSTANCE.sendMessage("/color", strColor);
    }
}
