package com.iuriio.apps.asyncandroid.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class LoaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<BingImageInfo>>
{
    private ViewPager pager;
    private BingImageInfoFragmentAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View content = inflater.inflate(R.layout.fragment_loader, container, false);
        this.pager = (ViewPager) content.findViewById(R.id.pager);
        return content;
    }

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View, android.os.Bundle)
         */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<List<BingImageInfo>> onCreateLoader(int id, Bundle args) {
        return new BingImageFeedLoader(this.getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BingImageInfo>> loader, List<BingImageInfo> data) {
        this.pagerAdapter = new BingImageInfoFragmentAdapter(getActivity(), getActivity().getSupportFragmentManager());
        this.pagerAdapter.setData(data);
        this.pager.setAdapter(this.pagerAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<BingImageInfo>> loader) {

    }
}
