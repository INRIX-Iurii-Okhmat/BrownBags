package com.iuriio.apps.asyncandroid.app;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class BingImageFeedLoader extends AsyncTaskLoader<List<BingImageInfo>> {
    private static final String IMAGE_FEED_URL = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=20";
    private List<BingImageInfo> data;

    public BingImageFeedLoader(Context context) {
        super(context);
    }

    @Override
    public List<BingImageInfo> loadInBackground() {
        final Gson gson = new Gson();

        BingImageFeed feed = null;
        try {
            feed = gson.fromJson(readUrl(IMAGE_FEED_URL), BingImageFeed.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (feed == null) {
            return new LinkedList<BingImageInfo>();
        }

        return feed.getImages();
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see android.support.v4.content.Loader#deliverResult(java.lang.Object)
	 */
    @Override
    public final void deliverResult(final List<BingImageInfo> result) {
        if (this.isStarted()) {
            super.deliverResult(result);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.content.Loader#onStartLoading()
     */
    @Override
    protected final void onStartLoading() {
        if (this.data != null) {
            this.deliverResult(this.data);
        }

        if (this.takeContentChanged() || this.data == null) {
            this.forceLoad();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.content.Loader#onStopLoading()
     */
    @Override
    protected final void onStopLoading() {
        this.cancelLoad();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.content.Loader#onReset()
     */
    @Override
    protected final void onReset() {
        super.onReset();

        // Ensure the loader is stopped.
        this.onStopLoading();
    }
}
