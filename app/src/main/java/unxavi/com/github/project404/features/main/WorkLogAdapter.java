package unxavi.com.github.project404.features.main;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.model.WorkLog;
import unxavi.com.github.project404.utils.Utils;

public class WorkLogAdapter extends FirestoreRecyclerAdapter<WorkLog, WorkLogAdapter.WalletHolder> {

    private final Locale locale;
    private WorkLogInterface listener;

    public interface WorkLogInterface {
        void onItemClick(WorkLog workLog);

        void isListEmpty(boolean isEmpty);
    }


    public WorkLogAdapter(@NonNull FirestoreRecyclerOptions<WorkLog> options, WorkLogInterface listener, Context context) {
        super(options);
        this.listener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
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
        holder.date.setText("");
        holder.task.setText(workLog.getTask().getName());
        switch (workLog.getAction()) {
            case WorkLog.ACTION_START:
                holder.actionIv.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                break;
            case WorkLog.ACTION_PAUSE:
                holder.actionIv.setImageResource(R.drawable.ic_pause_black_24dp);
                break;
            case WorkLog.ACTION_RETURN:
                holder.actionIv.setImageResource(R.drawable.ic_replay_black_24dp);
                break;
            case WorkLog.ACTION_STOP:
                holder.actionIv.setImageResource(R.drawable.ic_stop_black_24dp);
                break;


        }
        holder.date.setText(Utils.dateToString(workLog.getTimestamp(), locale));
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
        @BindView(R.id.date)
        TextView date;

        @BindView(R.id.task)
        TextView task;

        @BindView(R.id.actionIv)
        ImageView actionIv;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        WalletHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.mView = itemView;
            this.mView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(data);
        }
    }
}
