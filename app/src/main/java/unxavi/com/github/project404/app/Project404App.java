package unxavi.com.github.project404.app;

import android.app.Application;

import timber.log.Timber;
import unxavi.com.github.project404.tree.MyTimberTree;

public class Project404App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new MyTimberTree());
    }
}
