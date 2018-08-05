package unxavi.com.github.project404.features.main;

import android.support.annotation.Nullable;

import com.google.firebase.firestore.Query;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import unxavi.com.github.project404.data.FirestoreHelper;

public class MainActivityPresenter extends MvpBasePresenter<MainActivityView> {


    @Nullable
    public Query getWorkLogs() {
        return FirestoreHelper.getInstance().getUserWorkLogs();
    }

}