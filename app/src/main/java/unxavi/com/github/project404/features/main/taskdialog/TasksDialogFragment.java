package unxavi.com.github.project404.features.main.taskdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.data.FirestoreHelper;
import unxavi.com.github.project404.features.main.adapter.TaskAdapter;
import unxavi.com.github.project404.model.Task;

public class TasksDialogFragment extends DialogFragment implements TaskAdapter.WalletListInterface {

    @BindView(R.id.list)
    RecyclerView list;

    Unbinder unbinder;

    private TaskSelectDialogListener listener;
    private TaskAdapter adapter;

    public interface TaskSelectDialogListener {
        void onTaskSelected(Task task);
    }

    public static TasksDialogFragment newInstance() {
        TasksDialogFragment fragment = new TasksDialogFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        builder.setTitle(R.string.choose_task_dialog_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View rootView = inflater.inflate(R.layout.dialog_select_task, null);
        unbinder = ButterKnife.bind(this, rootView);
        list = rootView.findViewById(R.id.list);
        builder.setView(rootView);
        setupRecyclerView();
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (TaskSelectDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Timber.e(e);
            throw new ClassCastException("Caller of TasksDialogFragment must implement TaskSelectDialogListener");
        }
    }


    private void setupRecyclerView() {
        Query query = FirestoreHelper.getInstance().getUserTasksQuery();
        if (query != null) {
            // Configure recycler adapter options:
            FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                    .setQuery(query, Task.class)
                    .build();

            adapter = new TaskAdapter(options, this);
            list.hasFixedSize();
            list.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            list.setAdapter(adapter);
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



    @Override
    public void onTaskClick(Task task) {
        listener.onTaskSelected(task);
    }

    @OnClick({R.id.addTaskIV, R.id.addTaskTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addTaskIV:
            case R.id.addTaskTv:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
