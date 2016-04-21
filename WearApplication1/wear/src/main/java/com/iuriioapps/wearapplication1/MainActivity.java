package com.iuriioapps.wearapplication1;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {
    @Bind(R.id.main_list)
    protected WearableListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        final List<ItemInfo> info = new ArrayList<>();
        info.add(new ItemInfo("View stub", TestViewStubActivity.class));
        info.add(new ItemInfo("Card fragment", TestCardFragmentActivity.class));
        info.add(new ItemInfo("Card frame", TestCardFrameActivity.class));
        info.add(new ItemInfo("Box inset", TestBoxInsetActivity.class));
        info.add(new ItemInfo("List view", TestListViewActivity.class));
        info.add(new ItemInfo("2D picker", Test2DPickerActivity.class));
        info.add(new ItemInfo("Confirmation", TestConfirmationActivity.class));
        info.add(new ItemInfo("Press to Dismiss", TestPressToDismissActivity.class));
        info.add(new ItemInfo("Ambient", TestAmbientActivity.class));

        this.list.setAdapter(new Adapter(this, info));
        this.list.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                ((ItemHolder) viewHolder).launch();
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });
    }

    private static final class ItemInfo {
        private final String text;
        private final Class<?> cls;

        public ItemInfo(final String text, final Class<?> cls) {
            this.text = text;
            this.cls = cls;
        }

        public final String getText() {
            return this.text;
        }

        public final Class<?> getItemClass() {
            return this.cls;
        }
    }

    private static final class Adapter extends WearableListView.Adapter {
        private LayoutInflater inflater;
        private List<ItemInfo> items;

        public Adapter(final Context context, final List<ItemInfo> items) {
            this.inflater = LayoutInflater.from(context);
            this.items = items;
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(this.inflater.inflate(R.layout.view_main_list_item, null, false));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            final ItemInfo info = this.items.get(position);
            final ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.setText(info.getText());
            itemHolder.setClass(info.getItemClass());
        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }
    }

    private static final class ItemHolder extends WearableListView.ViewHolder {
        private MainListItemView item;

        public ItemHolder(View itemView) {
            super(itemView);

            this.item = (MainListItemView) itemView;
        }

        public void setText(final String text) {
            this.item.setText(text);
        }

        public void setClass(final Class<?> cls) {
            this.item.setItemClass(cls);
        }

        public void launch() {
            this.item.launch();
        }
    }
}
