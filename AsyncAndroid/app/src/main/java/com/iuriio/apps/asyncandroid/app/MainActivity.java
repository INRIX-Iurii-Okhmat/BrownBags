package com.iuriio.apps.asyncandroid.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                mTitle = getString(R.string.nav_controlling_threads);
                ft.replace(R.id.container, Fragment.instantiate(this, PatientWaitFragment.class.getName()));
                break;
            case 1:
                this.mTitle = this.getString(R.string.nav_cyclic_barrier);
                ft.replace(R.id.container, Fragment.instantiate(this, CyclicBarrierFragment.class.getName()));
                break;
            case 2:
                this.mTitle = this.getString(R.string.nav_producer_consumer);
                ft.replace(R.id.container, Fragment.instantiate(this, ProducerConsumerFragment.class.getName()));
                break;
            case 3:
                this.mTitle = this.getString(R.string.nav_deadlock);
                ft.replace(R.id.container, Fragment.instantiate(this, DeadlockFragment.class.getName()));
                break;
            case 4:
                this.mTitle = this.getString(R.string.nav_strict_mode);
                ft.replace(R.id.container, Fragment.instantiate(this, StrictModeFragment.class.getName()));
                break;
            case 5:
                mTitle = getString(R.string.nav_handlers);
                ft.replace(R.id.container, Fragment.instantiate(this, HandlerFragment.class.getName()));
                break;
            case 6:
                this.startActivity(new Intent(this, OrientationChangeActivity.class));
                break;
            case 7:
                mTitle = getString(R.string.nav_async_task);
                ft.replace(R.id.container, Fragment.instantiate(this, AsyncTaskFragment.class.getName()));
                break;
            case 8:
                this.mTitle = this.getString(R.string.nav_intent_service);
                ft.replace(R.id.container, Fragment.instantiate(this, IntentSvcFragment.class.getName()));
                break;
            case 9:
                this.mTitle = this.getString(R.string.nav_loaders);
                ft.replace(R.id.container, Fragment.instantiate(this, LoaderFragment.class.getName()));
                break;
        }

        final Handler uiHandler = this.getWindow().getDecorView().getHandler();

        ft.commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }
}
