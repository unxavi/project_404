package unxavi.com.github.project404.features.task;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import unxavi.com.github.project404.data.FirestoreHelper;
import unxavi.com.github.project404.model.Task;

public class AddTaskPresenter extends MvpBasePresenter<AddTaskView> implements FirestoreHelper.AddTaskListener {


    void createTask(String name){
        String nameTrim = name.trim();
        Task task = new Task(name);
        FirestoreHelper.getInstance().addUserTask(task, this);

    }

    @Override
    public void taskCreated(final Task task) {
        ifViewAttached(new ViewAction<AddTaskView>() {
            @Override
            public void run(@NonNull AddTaskView view) {
                view.taskCreated(task);
            }
        });
    }
}