package com.samples.daggersamples;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Application class.
 */
public class App extends Application {
    private static App instance;

    private ObjectGraph graph;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        this.graph = ObjectGraph.create(new AppModule(this));
    }

    /**
     * Gets the current app instance.
     *
     * @return Current app instance.
     */
    public static App get() {
        return App.instance;
    }

    /**
     * Gets an instance of the global DI graph.
     *
     * @return An instance of the {@link ObjectGraph}.
     */
    public ObjectGraph graph() {
        return this.graph;
    }
}
