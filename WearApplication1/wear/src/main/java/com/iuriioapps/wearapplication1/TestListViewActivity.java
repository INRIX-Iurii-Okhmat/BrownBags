package com.iuriioapps.wearapplication1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestListViewActivity extends Activity implements WearableListView.ClickListener {
    @Bind(R.id.wearable_list)
    protected WearableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_list_view);

        ButterKnife.bind(this);

        final String[] elements = new String[]{"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

        this.listView.setAdapter(new Adapter(this, elements));
        this.listView.setClickListener(this);
    }

    @Override
    public void onClick(WearableListView.ViewHolder v) {
    }

    @Override
    public void onTopEmptyRegionClick() {
    }

    private static final class Adapter extends WearableListView.Adapter {
        private final String[] dataset;
        private final LayoutInflater inflater;

        public Adapter(final Context context, final String[] dataset) {
            this.inflater = LayoutInflater.from(context);
            this.dataset = dataset;
        }

        @SuppressLint("InflateParams")
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(inflater.inflate(R.layout.wearable_list_item, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            ((ItemViewHolder) holder).setText(dataset[position]);
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return dataset.length;
        }

        public static class ItemViewHolder extends WearableListView.ViewHolder {
            private WearableListItemLayout item;

            public ItemViewHolder(View itemView) {
                super(itemView);

                this.item = (WearableListItemLayout) itemView;
            }

            public void setText(final String text) {
                this.item.setText(text);
            }
        }
    }
}
