package unxavi.com.github.project404.tree;

import android.support.annotation.NonNull;
import android.util.Log;

import timber.log.Timber;


public class MyTimberTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {

        if (priority == Log.WARN) {
            // TODO: 8/4/18   YourCrashLibrkary.logWarning(throwable);
        } else if (priority == Log.ERROR) {
            // TODO: 8/4/18   YourCrashLibrary.logException(throwable);
        }else{
            //discard the logs
        }
    }
}