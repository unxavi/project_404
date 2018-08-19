package unxavi.com.github.project404.features.task.feed;

import com.google.firebase.firestore.Query;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import unxavi.com.github.project404.data.FirestoreHelper;

class TasksPresenter extends MvpBasePresenter<TasksView> {

    public Query getTaskQuery(){
        return FirestoreHelper.getInstance().getUserTasksQuery();
    }
}