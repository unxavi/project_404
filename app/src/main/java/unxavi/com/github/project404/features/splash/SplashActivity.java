package unxavi.com.github.project404.features.splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import unxavi.com.github.project404.features.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.start(this);
    }
}
