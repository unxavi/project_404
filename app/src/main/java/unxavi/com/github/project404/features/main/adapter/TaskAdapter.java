package unxavi.com.github.project404.features.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.model.Task;

public class TaskAdapter extends FirestoreRecyclerAdapter<Task, TaskAdapter.TaskHolder> {


    private WalletListInterface listener;

    public interface WalletListInterface {
        void onTaskClick(Task task);
    }


    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Task> options, WalletListInterface listener) {
        super(options);
        this.listener = listener;
    }


    @Override
    protected void onBindViewHolder(@NonNull TaskHolder holder, int position, @NonNull Task model) {
        Task task = getItem(position);
        holder.data = task;
        holder.name.setText(task.getName());
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskHolder(view);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Task data;

        View view;

        @BindView(R.id.name)
        TextView name;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        TaskHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.view = itemView;
            this.view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            listener.onTaskClick(data);
        }
    }
}
