package com.iuriio.apps.reactive.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class BaseActivity extends Activity {
    @InjectView(R.id.output)
    TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(this.getMenuResource(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        outputText.setText("");
        return this.onMenuItemClick(item.getItemId()) ? true : super.onOptionsItemSelected(item);
    }

    protected abstract int getMenuResource();

    protected abstract boolean onMenuItemClick(final int id);
}
