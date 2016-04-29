package com.iuriioapps.rxandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RxBindingsActivity extends AppCompatActivity {
    @BindView(R.id.input)
    protected EditText input;

    @BindView(R.id.output)
    protected TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_rx_bindings);

        ButterKnife.bind(this);

        RxTextView.textChanges(this.input).subscribe(this.output::setText);
    }
}
