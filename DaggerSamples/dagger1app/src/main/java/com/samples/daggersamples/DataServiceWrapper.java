package com.samples.daggersamples;

import android.util.Log;

import javax.inject.Inject;

/**
 * Data service wrapper.
 */
public class DataServiceWrapper {
    @Inject
    public DataServiceWrapper() {
    }

    public String callWebService() {
        Log.d("DSW", "Calling web service");
        return "Service response";
    }
}
