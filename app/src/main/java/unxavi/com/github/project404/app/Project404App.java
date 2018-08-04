package unxavi.com.github.project404.app;

import android.app.Application;

import timber.log.Timber;
import unxavi.com.github.project404.BuildConfig;
import unxavi.com.github.project404.app.trees.ReleaseTree;

public class Project404App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        else
            Timber.plant(new ReleaseTree());
    }
}
