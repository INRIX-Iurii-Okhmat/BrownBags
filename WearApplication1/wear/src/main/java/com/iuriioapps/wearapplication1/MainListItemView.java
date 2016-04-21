package com.iuriioapps.wearapplication1;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainListItemView extends LinearLayout {
    @Bind(android.R.id.text1)
    protected TextView text;

    private Class<?> itemClass;

    public MainListItemView(Context context) {
        super(context);
    }

    public MainListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MainListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }

    public void setText(final String value) {
        this.text.setText(value);
    }

    public void setItemClass(final Class<?> cls) {
        this.itemClass = cls;
    }

    public void launch() {
        if (itemClass == null) {
            return;
        }

        this.getContext().startActivity(new Intent(this.getContext(), this.itemClass));
    }
}
