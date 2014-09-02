package com.iuriio.apps.reactive.demo;

import android.content.Intent;

public class MainActivity extends BaseActivity {
    @Override
    protected int getMenuResource() {
        return R.menu.main;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        switch (id) {
            case R.id.action_creating_observables:
                this.startActivity(new Intent(this, CreatingObservablesActivity.class));
                return true;
            case R.id.action_transforming_observables:
                this.startActivity(new Intent(this, TransformingObservablesActivity.class));
                return true;
            case R.id.action_filtering_observables:
                this.startActivity(new Intent(this, FilteringObservablesActivity.class));
                return true;
            case R.id.action_combining_observables:
                this.startActivity(new Intent(this, CombiningObservablesActivity.class));
                return true;
        }

        return false;
    }
}
