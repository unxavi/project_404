package unxavi.com.github.project404.features.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.features.main.detail.WorkLogDetailActivity;
import unxavi.com.github.project404.features.main.detail.WorkLogDetailFragment;
import unxavi.com.github.project404.features.main.taskdialog.TasksDialogFragment;
import unxavi.com.github.project404.features.task.AddTaskActivity;
import unxavi.com.github.project404.model.Task;
import unxavi.com.github.project404.model.WorkLog;

@RuntimePermissions
public class MainActivity extends MvpActivity<MainActivityView, MainActivityPresenter>
        implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityView,
        WorkLogAdapter.WorkLogInterface,
        TasksDialogFragment.TaskSelectDialogListener {

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

    @BindView(R.id.rootView)
    CoordinatorLayout rootView;

    private WorkLogAdapter adapter;

    @Nullable
    private WorkLog lastWorkLog;
    @Nullable
    private Location location;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;

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
            twoPane = true;
        }
        setupRecyclerView();
        prepareLastWorkLogListener();
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

            adapter = new WorkLogAdapter(options, this, this);
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
    protected void onResume() {
        super.onResume();
        location = null;
        MainActivityPermissionsDispatcher.getLocationWithPermissionCheck(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SmartLocation.with(this).location().stop();
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
                TasksDialogFragment tasksDialogFragment = TasksDialogFragment.newInstance();
                tasksDialogFragment.show(getSupportFragmentManager(), "TasksDialogFragment");
                break;
            case R.id.fab_stop:
                if (lastWorkLog != null) {
                    presenter.createWorkLog(lastWorkLog.getTask(), WorkLog.ACTION_STOP, location);
                } else {
                    Timber.w(new Throwable("No lastWorkLog in MainActivity to get the task from"));
                }
                break;
            case R.id.fab_return:
                if (lastWorkLog != null) {
                    presenter.createWorkLog(lastWorkLog.getTask(), WorkLog.ACTION_RETURN, location);
                } else {
                    Timber.w(new Throwable("No lastWorkLog in MainActivity to get the task from"));
                }
                break;
            case R.id.fab_pause:
                if (lastWorkLog != null) {
                    presenter.createWorkLog(lastWorkLog.getTask(), WorkLog.ACTION_PAUSE, location);
                } else {
                    Timber.w(new Throwable("No lastWorkLog in MainActivity to get the task from"));
                }
                break;
        }
    }

    @Override
    public void renderFabButtonsFlow(@Nullable WorkLog lastWorkLog) {
        if (lastWorkLog != null) {
            switch (lastWorkLog.getAction()) {
                case WorkLog.ACTION_START:
                case WorkLog.ACTION_RETURN:
                    renderPauseStopFab();
                    break;
                case WorkLog.ACTION_PAUSE:
                    renderReturnFab();
                    break;
                case WorkLog.ACTION_STOP:
                    renderStartFab();
                    break;
            }
        } else {
            renderStartFab();
        }

    }

    private void renderPauseStopFab() {
        fabStart.setVisibility(View.INVISIBLE);
        fabReturn.setVisibility(View.INVISIBLE);
        fabPause.setVisibility(View.VISIBLE);
        fabStop.setVisibility(View.VISIBLE);
    }

    private void renderReturnFab() {
        fabStart.setVisibility(View.INVISIBLE);
        fabPause.setVisibility(View.INVISIBLE);
        fabStop.setVisibility(View.INVISIBLE);
        fabReturn.setVisibility(View.VISIBLE);
    }

    private void renderStartFab() {
        fabPause.setVisibility(View.INVISIBLE);
        fabStop.setVisibility(View.INVISIBLE);
        fabReturn.setVisibility(View.INVISIBLE);
        fabStart.setVisibility(View.VISIBLE);
    }

    private void prepareLastWorkLogListener() {
        Query lastWorkLogQuery = presenter.getLastWorkLogQuery();
        if (lastWorkLogQuery != null) {
            // TODO: 8/13/18 we never stop listening
            lastWorkLogQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Timber.e(e);
                    } else {
                        if (queryDocumentSnapshots != null) {
                            WorkLog workLog = null;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                try {
                                    workLog = document.toObject(WorkLog.class);
                                } catch (Exception exception) {
                                    Timber.e(exception);
                                }
                            }
                            MainActivity.this.lastWorkLog = workLog;
                            renderFabButtonsFlow(workLog);
                        } else {
                            Timber.e(new RuntimeException("Firestore last work log document snapshot is null"));
                        }
                    }
                }
            });
        } else {
            renderFabButtonsFlow(null);
        }

    }

    @Override
    public void onTaskSelected(Task task) {
        createStartWorkLog(task);
    }

    private void createStartWorkLog(Task task) {
        presenter.createWorkLog(task, WorkLog.ACTION_START, location);
    }

    @Override
    public void newTaskSelected() {
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, AddTaskActivity.RC_ADD_TASK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddTaskActivity.RC_ADD_TASK) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Task task = data.getParcelableExtra(AddTaskActivity.TASK_CREATED);
                    if (task != null) {
                        Snackbar.make(rootView, R.string.task_created, Snackbar.LENGTH_LONG).show();
                        createStartWorkLog(task);
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

    public boolean isLocationEnable() {
        boolean locationServicesEnabled = SmartLocation.with(this).location().state().locationServicesEnabled();
        boolean anyProviderAvailable = SmartLocation.with(this).location().state().isAnyProviderAvailable();
        return locationServicesEnabled && anyProviderAvailable;
    }

    @Override
    public void showEnableLocation() {
        Snackbar.make(rootView, R.string.location_services_off, Snackbar.LENGTH_LONG).show();
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void getLocation() {
        if (!isLocationEnable()) {
            showEnableLocation();
        } else {
            SmartLocation
                    .with(this)
                    .location()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {
                            MainActivity.this.location = location;
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_location_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDeniedForCamera() {
        Toast.makeText(this, R.string.permission_location_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskForCamera() {
        Toast.makeText(this, R.string.permission_location_neverask, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(WorkLog workLog) {
        if (twoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(WorkLog.WORK_LOG_TAG, workLog);
            WorkLogDetailFragment fragment = new WorkLogDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.work_log_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, WorkLogDetailActivity.class);
            intent.putExtra(WorkLog.WORK_LOG_TAG, workLog);
            startActivity(intent);
        }
    }

}
