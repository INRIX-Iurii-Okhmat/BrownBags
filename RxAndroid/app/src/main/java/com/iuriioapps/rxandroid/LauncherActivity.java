package com.iuriioapps.rxandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_launcher);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_simple)
    protected void onSimpleClick() {
        this.startActivity(new Intent(this, RxSimpleActivity.class));
    }

    @OnClick(R.id.btn_android_io)
    protected void onAndroidIoClick() {
        this.startActivity(new Intent(this, RxAndroidSchedulerActivity.class));
    }

    @OnClick(R.id.btn_lifecycle)
    protected void onLifecycleClick() {
        this.startActivity(new Intent(this, RxLifecycleActivity.class));
    }

    @OnClick(R.id.btn_rx_bindings)
    protected void onRxBindingsClick() {
        this.startActivity(new Intent(this, RxBindingsActivity.class));
    }

    @OnClick(R.id.btn_custom_operators)
    protected void onCustomOperatorsClick() {
        this.startActivity(new Intent(this, RxCustomOperatorsActivity.class));
    }

    @OnClick(R.id.btn_unsubscribe)
    protected void onUnsubscribeClick() {
        this.startActivity(new Intent(this, RxUnsubscribeActivity.class));
    }

    @OnClick(R.id.btn_sideeffects)
    protected void onSideEffectsClick() {
        this.startActivity(new Intent(this, RxSideeffectMethodsActivity.class));
    }

    @OnClick(R.id.btn_multi_source)
    protected void onMultiSourceClick() {
        this.startActivity(new Intent(this, RxMultipleSourcesActivity.class));
    }
}
