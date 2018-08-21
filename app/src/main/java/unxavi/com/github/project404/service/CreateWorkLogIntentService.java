package unxavi.com.github.project404.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import timber.log.Timber;
import unxavi.com.github.project404.data.FirestoreHelper;
import unxavi.com.github.project404.model.WorkLog;
import unxavi.com.github.project404.widget.AppWidget;

public class CreateWorkLogIntentService extends IntentService implements FirestoreHelper.AddWorkLog {

    public static final String ACTION_START =
            "unxavi.com.github.project404.service.action.start_work";

    public static final String ACTION_PAUSE =
            "unxavi.com.github.project404.service.action.pause_work";

    public static final String ACTION_RETURN =
            "unxavi.com.github.project404.service.action.return_work";

    public static final String ACTION_STOP =
            "unxavi.com.github.project404.service.action.stop_work";

    public CreateWorkLogIntentService() {
        super("CreateWorkLogIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case ACTION_START:
                        createWorkLog(WorkLog.ACTION_START);
                        break;
                    case ACTION_PAUSE:
                        createWorkLog(WorkLog.ACTION_PAUSE);
                        break;
                    case ACTION_RETURN:
                        createWorkLog(WorkLog.ACTION_RETURN);
                        break;
                    case ACTION_STOP:
                        createWorkLog(WorkLog.ACTION_STOP);
                        break;

                }
            }
        }
    }

    private void createWorkLog(final int workLogAction) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SmartLocation.with(getApplicationContext()).location()
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            createWorkLogWithLocation(workLogAction, location);
                        }
                    });
        }else{
            createWorkLogWithLocation(workLogAction, null);
        }
    }

    private void createWorkLogWithLocation(final int workLogAction, final Location location) {
        Query lastUserWorkLog = FirestoreHelper.getInstance().getLastUserWorkLog();
        if (lastUserWorkLog != null) {
            lastUserWorkLog.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null) {
                        WorkLog lastWorkLog = null;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                lastWorkLog = document.toObject(WorkLog.class);
                            } catch (Exception exception) {
                                Timber.e(exception);
                            }
                        }
                        if (lastWorkLog != null) {
                            WorkLog workLog = new WorkLog(workLogAction, lastWorkLog.getTask(), location);
                            FirestoreHelper.getInstance().addUserWorkLog(workLog, CreateWorkLogIntentService.this);
                        }
                    } else {
                        Timber.e(new RuntimeException("Firestore last work log document snapshot is null"));
                    }
                }
            });
        }
    }

    @Override
    public void workLogCreated(WorkLog workLog) {
        updateWidget();
    }

    public void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, AppWidget.class));
        AppWidget.updateWidgetFromActivity(this, appWidgetManager, appWidgetIds);
    }
}
