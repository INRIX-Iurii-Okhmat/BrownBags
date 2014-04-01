package com.iuriio.apps.asyncandroid.app;

/**
 * Created by IuriiO on 4/1/2014.
 */
public class CorrectSingleton {
    private volatile CorrectSingleton helper = null;

    public CorrectSingleton getInstance() {
        if (helper == null) {
            synchronized(this) {
                if (helper == null)
                    helper = new CorrectSingleton();
            }
        }

        return helper;
    }
}
