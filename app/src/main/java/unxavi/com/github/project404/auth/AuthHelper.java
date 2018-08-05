package unxavi.com.github.project404.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthHelper {
    private static AuthHelper ourInstance;

    private final FirebaseAuth auth;

    public static synchronized AuthHelper getInstance() {
        if (ourInstance == null) {
            ourInstance = new AuthHelper();
        }
        return ourInstance;
    }

    private AuthHelper() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    public boolean isUserSignedIn(){
        return getCurrentUser() != null;
    }
}
