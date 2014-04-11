package com.iuriio.apps.asyncandroid.app;

import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import java.lang.reflect.Field;

public class MainLooperSpy {
    private final Field messagesField;
    private final Field nextField;
    private final MessageQueue mainMessageQueue;

    public MainLooperSpy() {
        try {
            Field queueField = Looper.class.getDeclaredField("mQueue");
            queueField.setAccessible(true);
            messagesField = MessageQueue.class.getDeclaredField("mMessages");
            messagesField.setAccessible(true);
            nextField = Message.class.getDeclaredField("next");
            nextField.setAccessible(true);
            Looper mainLooper = Looper.getMainLooper();
            mainMessageQueue = (MessageQueue) queueField.get(mainLooper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void dumpQueue() {
        try {
            Message nextMessage = (Message) messagesField.get(mainMessageQueue);
            Log.d("MainLooperSpy", "Begin dumping queue");
            dumpMessages(nextMessage);
            Log.d("MainLooperSpy", "End dumping queue");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void dumpMessages(Message message) throws IllegalAccessException {
        if (message != null) {
            try {
                Log.d("MainLooperSpy", message.toString());
            } catch (final NullPointerException ex) {
                // Do nothing.. we had a chance and blew it.
            }

            Message next = (Message) nextField.get(message);
            dumpMessages(next);
        }
    }
}
