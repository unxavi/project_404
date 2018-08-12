package unxavi.com.github.project404.data;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import unxavi.com.github.project404.auth.AuthHelper;
import unxavi.com.github.project404.model.Task;
import unxavi.com.github.project404.model.User;
import unxavi.com.github.project404.model.WorkLog;

public class FirestoreHelper {

    private static FirestoreHelper ourInstance;

    private final FirebaseFirestore db;

    public static synchronized FirestoreHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new FirestoreHelper();
        }
        return ourInstance;
    }

    private FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    @Nullable
    public Query getUserWorkLogs() {
        if (AuthHelper.getInstance().isUserSignedIn()) {
            return db.collection(User.COLLECTION)
                    .document(AuthHelper.getInstance().getCurrentUser().getUid())
                    .collection(WorkLog.COLLECTION)
                    .orderBy(WorkLog.FIELD_DATE, Query.Direction.DESCENDING);
        } else {
            return null;
        }
    }

    @Nullable
    public Query getLastUserWorkLog() {
        if (AuthHelper.getInstance().isUserSignedIn()) {
            return db.collection(User.COLLECTION)
                    .document(AuthHelper.getInstance().getCurrentUser().getUid())
                    .collection(WorkLog.COLLECTION)
                    .orderBy(WorkLog.FIELD_DATE, Query.Direction.DESCENDING)
                    .limit(1);
        } else {
            return null;
        }
    }

    @Nullable
    public Query getUserTasksQuery() {
        if (AuthHelper.getInstance().isUserSignedIn()) {
            return db.collection(User.COLLECTION)
                    .document(AuthHelper.getInstance().getCurrentUser().getUid())
                    .collection(Task.COLLECTION)
                    .orderBy(Task.FIELD_NAME_LOWERCASE, Query.Direction.ASCENDING);
        } else {
            return null;
        }
    }


}
