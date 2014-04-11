package com.iuriio.apps.asyncandroid.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by IuriiO on 4/11/2014.
 */
public class BingImageInfoFragmentAdapter extends FragmentPagerAdapter {
    private final Context context;
    private List<BingImageInfo> info;

    public BingImageInfoFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
    }

    public void setData(final List<BingImageInfo> data) {
        this.info = data;
    }

    @Override
    public Fragment getItem(int position) {
        final Bundle args = new Bundle();
        args.putString("copyright", this.info.get(position).getCopyright());
        args.putString("url", this.info.get(position).getUrl());
        return Fragment.instantiate(context, BingImageInfoFragment.class.getName(), args);
    }

    @Override
    public int getCount() {
        return this.info.size();
    }
}
