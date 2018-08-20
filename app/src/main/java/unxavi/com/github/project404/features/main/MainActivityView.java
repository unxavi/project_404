package unxavi.com.github.project404.features.main;

import android.support.annotation.Nullable;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import unxavi.com.github.project404.model.WorkLog;

public interface MainActivityView extends MvpView {

    void showEmptyView();

    void showWorkLogsList();

    void renderFabButtonsFlow(@Nullable WorkLog lastWorkLog);

    void showEnableLocation();

    void showSignInMenu();

    void showSignOutMenu();

    void updateWidget();
}
