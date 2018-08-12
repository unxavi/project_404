package unxavi.com.github.project404.features.task;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import unxavi.com.github.project404.model.Task;

public interface AddTaskView extends MvpView {

    void taskCreated(Task task);

}
