package unxavi.com.github.project404.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import timber.log.Timber;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.data.FirestoreHelper;
import unxavi.com.github.project404.model.WorkLog;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    public static void updateWidgetFromActivity(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateWidget(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        Query lastUserWorkLog = FirestoreHelper.getInstance().getLastUserWorkLog();
        if (lastUserWorkLog != null) {
            lastUserWorkLog.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots != null) {
                        WorkLog workLog = null;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                workLog = document.toObject(WorkLog.class);
                            } catch (Exception exception) {
                                Timber.e(exception);
                            }
                        }
                        renderWidgetFlow(views, appWidgetManager, appWidgetId, workLog);
                    } else {
                        Timber.e(new RuntimeException("Firestore last work log document snapshot is null"));
                    }
                }
            });
        }
    }

    private static void renderWidgetFlow(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId, WorkLog workLog) {
        views.setTextViewText(R.id.appwidget_text, workLog.getTask().getName());
        switch (workLog.getAction()) {
            case WorkLog.ACTION_START:
            case WorkLog.ACTION_RETURN:
                views.setViewVisibility(R.id.start_button, View.GONE);
                views.setViewVisibility(R.id.restart_button, View.GONE);
                views.setViewVisibility(R.id.pause_button, View.VISIBLE);
                views.setViewVisibility(R.id.stop_button, View.VISIBLE);
                break;
            case WorkLog.ACTION_PAUSE:
                views.setViewVisibility(R.id.start_button, View.GONE);
                views.setViewVisibility(R.id.pause_button, View.GONE);
                views.setViewVisibility(R.id.stop_button, View.GONE);
                views.setViewVisibility(R.id.restart_button, View.VISIBLE);
                break;
            case WorkLog.ACTION_STOP:
                views.setViewVisibility(R.id.pause_button, View.GONE);
                views.setViewVisibility(R.id.stop_button, View.GONE);
                views.setViewVisibility(R.id.restart_button, View.GONE);
                views.setViewVisibility(R.id.start_button, View.VISIBLE);
                break;
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        updateWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

