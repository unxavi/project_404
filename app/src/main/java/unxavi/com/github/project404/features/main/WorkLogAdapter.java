package unxavi.com.github.project404.features.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import unxavi.com.github.project404.R;
import unxavi.com.github.project404.model.WorkLog;

public class WorkLogAdapter extends FirestoreRecyclerAdapter<WorkLog, WorkLogAdapter.WalletHolder> {

    private WorkLogInterface listener;

    public interface WorkLogInterface {
        void onItemClick(WorkLog workLog);

        void isListEmpty(boolean isEmpty);
    }


    public WorkLogAdapter(@NonNull FirestoreRecyclerOptions<WorkLog> options, WorkLogInterface listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0) {
            listener.isListEmpty(true);
        } else {
            listener.isListEmpty(false);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull WalletHolder holder, int position, @NonNull WorkLog model) {
        WorkLog workLog = getItem(position);
        holder.data = workLog;
        holder.contentTV.setText(workLog.getTimestamp().toString());
    }

    @NonNull
    @Override
    public WalletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.work_log_item, parent, false);
        return new WalletHolder(view);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class WalletHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public WorkLog data;
        View mView;
        TextView contentTV;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        WalletHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.mView = itemView;
            contentTV = itemView.findViewById(R.id.content);
            this.mView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(data);
        }
    }
}
