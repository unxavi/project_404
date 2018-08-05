package unxavi.com.github.project404.features.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import unxavi.com.github.project404.R;
import unxavi.com.github.project404.features.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    /*
     * to know when the app is in the process of signing a user
     * in anonymous and avoid multiple calls to method
     */
    private boolean isSigningInAnonymousUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startMainActivity();
        } else {
            signInAnonymously();
        }
    }

    private void startMainActivity() {
        MainActivity.start(this);
    }

    /**
     * Sign a user anonymously if there are no credentials for the user
     */
    private void signInAnonymously() {
        if (!isSigningInAnonymousUser) {
            isSigningInAnonymousUser = true;
            auth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            isSigningInAnonymousUser = false;
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                // this should be executed on the listener
                                startMainActivity();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SplashActivity.this, R.string.network_error_anonymous_first_time,
                                        Toast.LENGTH_SHORT).show();
                                // this should be executed on the listener
                            }
                        }
                    });
        }
    }
}
