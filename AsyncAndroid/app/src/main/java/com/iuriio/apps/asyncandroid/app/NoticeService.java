package com.iuriio.apps.asyncandroid.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

public class NoticeService extends IntentService {
    public static final String BROADCAST = "com.iuriio.apps.NoticeService.BROADCAST";

    private static Intent broadcast = new Intent(BROADCAST);

    public NoticeService() {
        super("NoticeServiceWorkerThread");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // SystemClock.sleep(millis) is a utility function very similar to Thread.sleep(millis),
        // but it ignores InterruptedException. Use this function for delays if you do not use
        // Thread.interrupt(), as it will preserve the interrupted state of the thread.
        SystemClock.sleep(5000);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }
}
