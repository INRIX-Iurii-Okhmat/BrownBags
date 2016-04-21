package com.iuriioapps.wearapplication1;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WearableListItemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener {

    private ImageView icon;
    private TextView text;

    private final float fadeAlpha;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.fadeAlpha = 70 / 100f;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.icon = (ImageView) findViewById(android.R.id.icon);
        this.text = (TextView) findViewById(android.R.id.text1);
    }

    public void setText(final String text) {
        this.text.setText(text);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        this.text.setAlpha(1f);
        this.icon.setAlpha(1f);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        this.icon.setAlpha(fadeAlpha);
        this.text.setAlpha(fadeAlpha);
    }
}

