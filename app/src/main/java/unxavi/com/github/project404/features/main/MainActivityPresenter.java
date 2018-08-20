package unxavi.com.github.project404.features.main;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.Query;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import unxavi.com.github.project404.data.FirestoreHelper;
import unxavi.com.github.project404.model.Task;
import unxavi.com.github.project404.model.WorkLog;

public class MainActivityPresenter extends MvpBasePresenter<MainActivityView> implements FirestoreHelper.AddWorkLog{


    @Nullable
    public Query getWorkLogs() {
        return FirestoreHelper.getInstance().getUserWorkLogs();
    }

    @Nullable
    public Query getLastWorkLogQuery() {
        return FirestoreHelper.getInstance().getLastUserWorkLog();
    }

    public void createWorkLog(Task task, int action, Location location) {
        WorkLog workLog = new WorkLog(action, task, location);
        FirestoreHelper.getInstance().addUserWorkLog(workLog, this);
    }

    @Override
    public void workLogCreated(WorkLog workLog) {
    }
}