package unxavi.com.github.project404.features.main.detail;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import unxavi.com.github.project404.R;
import unxavi.com.github.project404.features.deleteMasterDetail.dummy.DummyContent;
import unxavi.com.github.project404.features.main.MainActivity;
import unxavi.com.github.project404.model.WorkLog;

/**
 * A fragment representing a single DeleteTimeLog detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link WorkLogDetailActivity}
 * on handsets.
 */
public class WorkLogDetailFragment extends Fragment {

    private  WorkLog workLog;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkLogDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(WorkLog.WORK_LOG_TAG)) {
            workLog = getArguments().getParcelable(WorkLog.WORK_LOG_TAG);
            // TODO: 14/08/2018 do we need the getactivity for something real?
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = null;
            if (activity != null) {
                appBarLayout = activity.findViewById(R.id.toolbar_layout);
            }
            if (appBarLayout != null) {
                appBarLayout.setTitle(workLog.getTask().getName());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.work_log_detail_fragment, container, false);

        // Show the dummy content as text in a TextView.
        if (workLog != null) {
            ((TextView) rootView.findViewById(R.id.deletetimelog_detail)).setText(workLog.getTask().getName());
        }

        return rootView;
    }
}
