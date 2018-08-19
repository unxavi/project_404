package unxavi.com.github.project404.features.task.feed;

import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface TasksView extends MvpView {

    void showEmptyView();

    void showTasksList();

}
