package com.samples.daggersamples;

import android.util.Log;

import javax.inject.Inject;

/**
 * Custom data provider.
 */
public class DataProvider {
    private DataServiceWrapper serviceWrapper;

    @Inject
    public DataProvider(final DataServiceWrapper serviceWrapper) {
        this.serviceWrapper = serviceWrapper;
    }

    public String getData() {
        Log.d("DP", "Getting data");

        return this.serviceWrapper.callWebService();
    }
}
