package unxavi.com.github.project404.features.task.feed.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.auth.AuthHelper;
import unxavi.com.github.project404.data.FirestoreHelper;
import unxavi.com.github.project404.features.task.feed.TaskListActivity;
import unxavi.com.github.project404.model.Task;

/**
 * A fragment representing a single Task detail screen.
 * This fragment is either contained in a {@link TaskListActivity}
 * in two-pane mode (on tablets) or a {@link TaskDetailActivity}
 * on handsets.
 */
public class TaskDetailFragment extends Fragment implements DeleteDialogFragment.DeleteDialogFragmentListener {

    public static final String IS_TWO_PANE = "IS_TWO_PANE";

    private Task task;

    private boolean isTwoPane;

    @BindView(R.id.taskNameET)
    EditText taskNameET;

    @BindView(R.id.taskNameIL)
    TextInputLayout taskNameIL;

    Unbinder unbinder;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(Task.TASK_TAG)) {
            task = getArguments().getParcelable(Task.TASK_TAG);
        }
        if (getArguments() != null && getArguments().containsKey(IS_TWO_PANE)) {
            isTwoPane = getArguments().getBoolean(IS_TWO_PANE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        taskNameET.setText(task.getName());
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_task_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            confirmDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteDialog() {
        DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
        deleteDialogFragment.show(getChildFragmentManager(), "DeleteDialogFragment");
    }

    @Override
    public void onDialogPositiveClick() {
        FirestoreHelper.getInstance().deleteUserTask(AuthHelper.getInstance().getCurrentUser().getUid(), task);
        if (getActivity() != null) {
            if (isTwoPane) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }else{
                getActivity().finish();
            }
        }
    }

    @Override
    public void onDialogNegativeClick() {
        //no need to implement anything
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
