package unxavi.com.github.project404.features.task.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.firestore.Query;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.adapter.TaskAdapter;
import unxavi.com.github.project404.features.task.AddTaskActivity;
import unxavi.com.github.project404.features.task.feed.detail.TaskDetailActivity;
import unxavi.com.github.project404.features.task.feed.detail.TaskDetailFragment;
import unxavi.com.github.project404.model.Task;

/**
 * An activity representing a list of Tasks. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link TaskDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class TaskListActivity extends MvpActivity<TasksView, TasksPresenter> implements TasksView, TaskAdapter.WalletListInterface {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.group_empty)
    Group groupEmpty;

    @BindView(R.id.task_list)
    RecyclerView taskList;

    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.task_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }

        setupRecyclerView(taskList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Query taskQuery = presenter.getTaskQuery();
        if (taskQuery != null) {
            FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                    .setQuery(taskQuery, Task.class)
                    .build();

            adapter = new TaskAdapter(options, this);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            recyclerView.hasFixedSize();
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @NonNull
    @Override
    public TasksPresenter createPresenter() {
        return new TasksPresenter();
    }

    @Override
    public void showEmptyView() {
        groupEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTasksList() {
        groupEmpty.setVisibility(View.GONE);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, AddTaskActivity.RC_ADD_TASK);
    }

    @Override
    public void onTaskClick(Task task) {
        if (twoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Task.TASK_TAG, task);
            TaskDetailFragment fragment = new TaskDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.task_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            intent.putExtra(Task.TASK_TAG, task);
            startActivity(intent);
        }
    }

    public void isListEmpty(boolean isEmpty) {
        if (isEmpty) {
            showEmptyView();
        } else {
            showTasksList();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddTaskActivity.RC_ADD_TASK) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Task task = data.getParcelableExtra(Task.TASK_TAG);
                    if (task != null) {
                        Snackbar.make(rootView, R.string.task_created, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(rootView, R.string.no_task_created, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(rootView, R.string.no_task_created, Snackbar.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
