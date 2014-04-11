package com.iuriio.apps.asyncandroid.app;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BingImageFeed {
    @SerializedName("images")
    private List<BingImageInfo> images;

    public final List<BingImageInfo> getImages() {
        return this.images;
    }
}
