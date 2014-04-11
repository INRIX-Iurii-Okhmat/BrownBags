package com.iuriio.apps.asyncandroid.app;

import com.google.gson.annotations.SerializedName;

public class BingImageInfo {
    private static final String BASE_URL = "http://www.bing.com";

    @SerializedName("url")
    private String url;

    @SerializedName("copyright")
    private String copyright;

    public final String getUrl() {
        return BASE_URL + this.url;
    }

    public final String getCopyright() {
        return this.copyright;
    }
}
