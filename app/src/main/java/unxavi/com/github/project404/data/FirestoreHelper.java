package unxavi.com.github.project404.data;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

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


}
