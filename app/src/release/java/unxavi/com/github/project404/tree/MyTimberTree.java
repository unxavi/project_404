package unxavi.com.github.project404.tree;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;


public class MyTimberTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {

        if (priority == Log.WARN || priority == Log.ERROR) {
            Crashlytics.logException(t);
        }else{
            //discard the logs for production
        }
    }
}