package com.iuriio.apps.asyncandroid.app;

/**
 * Created by IuriiO on 4/1/2014.
 */
@SuppressWarnings({"InfiniteLoopStatement", "StatementWithEmptyBody"})
public class NewThread {
    public void createNewThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    // Do something.
                }
            }
        });
        t.start();
    }

    public static class BackgroundThread extends Thread {
        @Override
        public void run() {
            // Do stuff here.
        }
    }

    public void createThreadBySubclass() {
        new BackgroundThread().start();
    }
}
