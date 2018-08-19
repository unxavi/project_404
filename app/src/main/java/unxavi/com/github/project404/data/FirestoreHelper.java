package unxavi.com.github.project404.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import timber.log.Timber;
import unxavi.com.github.project404.auth.AuthHelper;
import unxavi.com.github.project404.model.Task;
import unxavi.com.github.project404.model.User;
import unxavi.com.github.project404.model.WorkLog;

public class FirestoreHelper {

    public interface AddTaskListener {
        void taskCreated(Task task);
    }

    public interface AddWorkLog {
        void workLogCreated(WorkLog workLog);
    }

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


    public void addUserTask(final Task task, final AddTaskListener listener) {
        if (AuthHelper.getInstance().isUserSignedIn()) {
            db.collection(User.COLLECTION)
                    .document(AuthHelper.getInstance().getCurrentUser().getUid())
                    .collection(Task.COLLECTION)
                    .document(task.getNameLowerCase())
                    .set(task, SetOptions.merge())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.e(e);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.taskCreated(task);
                        }
                    });
        }

    }

    public void addUserWorkLog(final WorkLog workLog, final AddWorkLog listener) {
        if (AuthHelper.getInstance().isUserSignedIn()) {
            db.collection(User.COLLECTION)
                    .document(AuthHelper.getInstance().getCurrentUser().getUid())
                    .collection(WorkLog.COLLECTION)
                    .document()
                    .set(workLog, SetOptions.merge())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Timber.e(e);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.workLogCreated(workLog);
                        }
                    });
        }
    }

    public void deleteUserTask(String uid, Task task) {
        db.collection(User.COLLECTION)
                .document(uid)
                .collection(Task.COLLECTION)
                .document(task.getNameLowerCase())
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       Timber.e(e);
                    }
                });
    }


}
