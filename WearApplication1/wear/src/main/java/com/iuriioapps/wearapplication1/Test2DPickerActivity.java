package com.iuriioapps.wearapplication1;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Test2DPickerActivity extends Activity {
    @Bind(R.id.pager)
    protected GridViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_2d_picker);

        ButterKnife.bind(this);

        final SampleGridPagerAdapter adapter = new SampleGridPagerAdapter(this, this.getFragmentManager());
        this.pager.setAdapter(adapter);
    }

    public static final class SampleGridPagerAdapter extends FragmentGridPagerAdapter {
        private final Context mContext;

        private final Page[][] PAGES;

        public SampleGridPagerAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;

            PAGES = new Page[2][];
            PAGES[0] = new Page[]{
                    new Page("Title 1", "Content 1"),
                    new Page("Title 2", "Content 2")
            };
            PAGES[1] = new Page[]{
                    new Page("Title 3", "Content 3"),
                    new Page("Title 4", "Content 4"),
                    new Page("Title 5", "Content 5")
            };
        }

        static final int[] BG_IMAGES = new int[]{
                R.drawable.bg_1,
                R.drawable.bg_2,
                R.drawable.bg_3,
                R.drawable.bg_4,
                R.drawable.bg_5
        };

        // A simple container for static data in each page
        private static class Page {
            String title;
            String text;

            public Page(final String title, final String text) {
                this.title = title;
                this.text = text;
            }
        }

        @Override
        public Fragment getFragment(int row, int column) {
            final Page page = PAGES[row][column];
            return CardFragment.create(page.title, page.text);
        }

        @Override
        public Drawable getBackgroundForRow(int row) {
            //noinspection deprecation
            return this.mContext.getResources().getDrawable((BG_IMAGES[row % BG_IMAGES.length]));
        }

        @Override
        public Drawable getBackgroundForPage(int row, int column) {
            if (row == 2 && column == 1) {
                //noinspection deprecation
                return mContext.getResources().getDrawable(R.drawable.example_big_picture);
            }

            return super.getBackgroundForPage(row, column);
        }

        @Override
        public int getRowCount() {
            return PAGES.length;
        }

        @Override
        public int getColumnCount(int rowNum) {
            return PAGES[rowNum].length;
        }
    }
}
