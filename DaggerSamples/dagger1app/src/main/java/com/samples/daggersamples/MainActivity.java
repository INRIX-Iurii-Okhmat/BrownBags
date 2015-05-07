package com.samples.daggersamples;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * Main activity.
 */
public class MainActivity extends ActionBarActivity implements ScopedGraphProvider {
    @Inject
    @ForApplication
    protected Context context;

    @Inject
    protected Resources resources;

    private ObjectGraph scopedGraph;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.scopedGraph = App.get().graph().plus(
                new ActivityModule(this));

        this.scopedGraph.inject(this);

        this.getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new CustomFragment())
                .commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectGraph getGraph() {
        return this.scopedGraph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ALWAYS!
        this.scopedGraph = null;
    }
}
