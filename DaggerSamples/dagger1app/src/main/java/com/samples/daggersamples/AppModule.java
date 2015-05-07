package com.samples.daggersamples;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Main application module.
 */
@Module(
        injects = {
                MainActivity.class
        },
        library = true
)
public class AppModule {
    private final Context context;

    /**
     * Initializes a new instance of the {@link AppModule}.
     *
     * @param context Current application context.
     */
    public AppModule(final Context context) {
        this.context = context;
    }

    /**
     * Provides current application context.
     *
     * @return Current application context.
     */
    @Provides
    @Singleton
    @ForApplication
    public Context providesContext() {
        return this.context;
    }

    /**
     * Provides access to the application resources.
     *
     * @return Return an instance of the {@link Resources}.
     */
    @Provides
    @Singleton
    public Resources providesResources() {
        return this.context.getResources();
    }
}
