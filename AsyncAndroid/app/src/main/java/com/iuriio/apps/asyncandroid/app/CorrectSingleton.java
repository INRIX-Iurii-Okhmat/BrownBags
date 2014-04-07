package com.iuriio.apps.asyncandroid.app;

public class CorrectSingleton {
    private static volatile CorrectSingleton instance = null;

    public static CorrectSingleton getInstance() {
        if (instance == null) {
            synchronized(CorrectSingleton.class) {
                if (instance == null)
                    instance = new CorrectSingleton();
            }
        }

        return instance;
    }
}
