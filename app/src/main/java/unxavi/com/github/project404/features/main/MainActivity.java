package unxavi.com.github.project404.features.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.model.WorkLog;

public class MainActivity extends MvpActivity<MainActivityView, MainActivityPresenter>
        implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityView, WorkLogAdapter.WorkLogInterface {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.work_log_recyclerview)
    RecyclerView workLogRecyclerView;

    @BindView(R.id.group_empty)
    Group groupEmpty;

    @BindView(R.id.fab_start)
    FloatingActionButton fabStart;

    @BindView(R.id.fab_stop)
    FloatingActionButton fabStop;

    @BindView(R.id.fab_return)
    FloatingActionButton fabReturn;

    @BindView(R.id.fab_pause)
    FloatingActionButton fabPause;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private WorkLogAdapter adapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.work_log_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        setupRecyclerView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @NonNull
    @Override
    public MainActivityPresenter createPresenter() {
        return new MainActivityPresenter();
    }

    private void setupRecyclerView() {
        Query workLogsQuery = presenter.getWorkLogs();
        if (workLogsQuery != null) {
            // Configure recycler adapter options:
            FirestoreRecyclerOptions<WorkLog> options = new FirestoreRecyclerOptions.Builder<WorkLog>()
                    .setQuery(workLogsQuery, WorkLog.class)
                    .build();

            adapter = new WorkLogAdapter(options, this);
            workLogRecyclerView.hasFixedSize();
            workLogRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            workLogRecyclerView.setAdapter(adapter);
        } else {
            closeOnError();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void closeOnError() {
        // TODO: 8/5/18
    }

    @Override
    public void onItemClick(WorkLog workLog) {

    }

    @Override
    public void isListEmpty(boolean isEmpty) {
        if (isEmpty) {
            showEmptyView();
        } else {
            showWorkLogsList();
        }
    }

    @Override
    public void showEmptyView() {
        groupEmpty.setVisibility(View.VISIBLE);

    }

    @Override
    public void showWorkLogsList() {
        groupEmpty.setVisibility(View.GONE);
    }

    @OnClick({R.id.fab_start, R.id.fab_stop, R.id.fab_return, R.id.fab_pause})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_start:
                break;
            case R.id.fab_stop:
                break;
            case R.id.fab_return:
                break;
            case R.id.fab_pause:
                break;
        }
    }
}
