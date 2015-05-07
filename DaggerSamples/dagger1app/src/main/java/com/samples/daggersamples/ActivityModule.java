package com.samples.daggersamples;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Module for injecting activity-specific dependencies.
 */
@Module(
        library = true,
        injects = {CustomFragment.class},
        addsTo = AppModule.class)
public class ActivityModule {
    private final Activity activity;

    /**
     * Initializes a new instance of the {@link ActivityModule}.
     *
     * @param activity An instance of the target activity.
     */
    public ActivityModule(final Activity activity) {
        this.activity = activity;
    }

    /**
     * Provides activity context.
     *
     * @return Activity instance.
     */
    @Provides
    @ForActivity
    public Context providesContext() {
        return this.activity;
    }

    /**
     * Provides current activity instance.
     *
     * @return Activity instance.
     */
    @Provides
    public Activity providesActivity() {
        return this.activity;
    }
}
