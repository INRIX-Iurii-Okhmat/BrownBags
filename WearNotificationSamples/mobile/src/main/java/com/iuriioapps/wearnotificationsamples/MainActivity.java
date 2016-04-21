package com.iuriioapps.wearnotificationsamples;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID_SIMPLE = 1;
    private static final int NOTIFICATION_ID_BIG_TEXT = 2;
    private static final int NOTIFICATION_ID_BIG_PICTURE = 3;
    private static final int NOTIFICATION_ID_WEAR_SIMPLE = 4;
    private static final int NOTIFICATION_ID_WEAR_PAGES = 5;
    private static final int NOTIFICATION_ID_WEAR_SIMPLE_ACTION = 6;
    private static final int NOTIFICATION_ID_WEAR_MULTI_ACTION = 7;
    private static final int NOTIFICATION_ID_WEAR_ONLY_ACTION = 8;
    private static final int NOTIFICATION_ID_WEAR_GRAVITY = 9;
    private static final int NOTIFICATION_ID_WEAR_STACKING = 10;
    private static final int NOTIFICATION_ID_WEAR_VOICE_INPUT = 11;

    private static final String EXTRA_VOICE_REPLY = "_voice_reply";

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.notificationManager = NotificationManagerCompat.from(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            final CharSequence voiceInput = remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
            if (TextUtils.isEmpty(voiceInput)) {
                Toast.makeText(this, "No voice input.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Voice input: " + voiceInput.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.btnSimpleNotification)
    public void onSimpleNotificationClick() {
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentTitle("Notification title")
                .setContentText("Notification text")
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_SIMPLE, notification);
    }

    @OnClick(R.id.btnBigTextNotification)
    public void onBigTextNotificationClick() {
        final NotificationCompat.Style bigTextStyle =
                new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("Big text content title")
                        .setSummaryText("Big text summary text")
                        .bigText("Lorem ipsum dolor sit amet, consectetur etc.");

        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification title")
                .setContentText("Notification text")
                .setStyle(bigTextStyle)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_BIG_TEXT, notification);
    }

    @OnClick(R.id.btnBigPictureNotification)
    public void onBigPictureNotificationClick() {
        final NotificationCompat.Style bigPictureStyle =
                new NotificationCompat.BigPictureStyle()
                        .setBigContentTitle("Big text content title")
                        .setSummaryText("Big text summary text")
                        .bigPicture(BitmapFactory.decodeResource(this.getResources(), R.drawable.example_big_picture));

        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification title")
                .setContentText("Notification text")
                .setStyle(bigPictureStyle)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_BIG_PICTURE, notification);
    }

    @OnClick(R.id.btnWearSimpleNotification)
    public void onWearSimpleNotificationClick() {
        final NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender()
                        .setBackground(BitmapFactory.decodeResource(
                                this.getResources(),
                                R.drawable.bg_1
                        ));

        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification title")
                .setContentText("Notification text")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .extend(extender)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_SIMPLE, notification);
    }

    @OnClick(R.id.btnWearPages)
    public void onWearPagesNotificationClick() {
        final List<Notification> pages = new ArrayList<>();

        // Create notification pages.
        for (int index = 0; index < 3; index++) {
            final Notification page = new NotificationCompat.Builder(this)
                    .setContentTitle("Page title " + index)
                    .setContentText("Text for page " + index)
                    .build();
            pages.add(page);
        }

        // Create wear extender.
        final NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender()
                        .addPages(pages);

        // Create and show notification.
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification with pages")
                .setContentText("Notification text")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .extend(extender)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_PAGES, notification);
    }

    @OnClick(R.id.btnWearSimpleActionNotification)
    public void onWearSimpleActionNotificationClick() {
        // Action.
        final Intent intent = new Intent(this, MainActivity.class);
        final PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create and show notification.
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification with action")
                .setContentText("Notification text")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_SIMPLE_ACTION, notification);
    }

    @OnClick(R.id.btnWearMultipleActionsNotification)
    public void onWearMultipleActionsNotificationClick() {
        // Prepare actions.
        final Intent intent1 = new Intent(this, MainActivity.class);
        final PendingIntent action1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

        final Intent intent2 = new Intent(Intent.ACTION_DIAL);
        intent2.setData(Uri.parse("tel:5554443322"));
        final PendingIntent action2 = PendingIntent.getActivity(this, 1, intent2, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create and show notification.
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification with action")
                .setContentText("Notification text")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(R.mipmap.ic_launcher, "Open activity", action1)
                .addAction(R.drawable.ic_phone, "Call", action2)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_MULTI_ACTION, notification);
    }

    @OnClick(R.id.btnWearOnlyActionsNotification)
    public void onWearOnlyActionsNotificationClick() {
        // Prepare actions.
        final Intent intent1 = new Intent(this, MainActivity.class);
        final PendingIntent action1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        final Intent intent2 = new Intent(Intent.ACTION_DIAL);
        intent2.setData(Uri.parse("tel:5554443322"));
        final PendingIntent action2 = PendingIntent.getActivity(this, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create wear extender.
        final NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender()
                        .addAction(new NotificationCompat.Action(R.mipmap.ic_launcher, "Open activity", action1))
                        .addAction(new NotificationCompat.Action(R.drawable.ic_phone, "Call", action2));

        // Create and show notification.
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification with action")
                .setContentText("Notification text")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .extend(extender)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_ONLY_ACTION, notification);
    }

    @OnClick(R.id.btnWearGravityNotification)
    public void onWearGravityNotificationClick() {
        final NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender()
                        .setGravity(Gravity.TOP);

        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification with gravity")
                .setContentText("Notification text")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .extend(extender)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_GRAVITY, notification);
    }

    @OnClick(R.id.btnWearStackingNotification)
    public void onWearStackingNotificationClick() {
        final NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender()
                        .setGravity(Gravity.TOP);

        final String stackName = "wear_stack";

        for (int index = 0; index < 5; index++) {
            final Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Stacked title " + index)
                    .setContentText("Notification text " + index)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setGroup(stackName)
                    .extend(extender)
                    .build();

            this.notificationManager.notify(NOTIFICATION_ID_WEAR_STACKING + index, notification);
        }
    }

    @OnClick(R.id.btnWearVoiceInputNotification)
    public void onWearVoiceInputNotificationClick() {
        // Define remote input.
        final String[] predefinedResponses = new String[]{"Yes", "No", "Maybe"};

        final RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel("Reply")
                .setChoices(predefinedResponses)
                .setAllowFreeFormInput(true)
                .build();

        final Intent replyIntent = new Intent(this, MainActivity.class);
        PendingIntent replyPendingIntent = PendingIntent.getActivity(
                this,
                0,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Define wear extender.
        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_play_dark, "Reply", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        final NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender()
                        .addAction(action);

        // Create notification.
        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Voice input")
                .setContentText("Notification with voice input")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .extend(extender)
                .build();

        this.notificationManager.notify(NOTIFICATION_ID_WEAR_VOICE_INPUT, notification);

        // See onNewIntent on how to handle the response from RemoteInput.
    }
}
