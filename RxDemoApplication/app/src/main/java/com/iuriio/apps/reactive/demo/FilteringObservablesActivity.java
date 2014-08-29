package com.iuriio.apps.reactive.demo;

public class FilteringObservablesActivity extends BaseActivity {
    //<editor-fold desc="Boilerplate">
    @Override
    protected int getMenuResource() {
        return R.menu.filtering_observables;
    }

    @Override
    protected boolean onMenuItemClick(int id) {
        switch (id) {
            case R.id.action_filter:
                return true;
        }

        return true;
    }
    //</editor-fold>
}
