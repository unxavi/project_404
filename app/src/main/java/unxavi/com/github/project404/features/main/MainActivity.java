package unxavi.com.github.project404.features.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

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
import unxavi.com.github.project404.auth.AuthHelper;
import unxavi.com.github.project404.features.main.detail.WorkLogDetailActivity;
import unxavi.com.github.project404.features.main.detail.WorkLogDetailFragment;
import unxavi.com.github.project404.features.main.taskdialog.TasksDialogFragment;
import unxavi.com.github.project404.features.splash.SplashActivity;
import unxavi.com.github.project404.features.task.AddTaskActivity;
import unxavi.com.github.project404.model.Task;
import unxavi.com.github.project404.model.WorkLog;

@RuntimePermissions
public class MainActivity extends MvpActivity<MainActivityView, MainActivityPresenter>
        implements NavigationView.OnNavigationItemSelectedListener,
        MainActivityView,
        WorkLogAdapter.WorkLogInterface,
        TasksDialogFragment.TaskSelectDialogListener {

    private static final int RC_SIGN_IN = 847;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    /*
     * to know when the app is in the process of signing a user
     * in anonymous and avoid multiple calls to method
     */
    private boolean isSigningInAnonymousUser;

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
    private FirebaseAuth.AuthStateListener authStateListener;
    private MenuItem itemNavMenuSignin;
    private MenuItem itemNavMenuSignout;
    private NavigationView navigationView;

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
        initAuth();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu navMenu = navigationView.getMenu();
        itemNavMenuSignin = navMenu.findItem(R.id.signin);
        itemNavMenuSignout = navMenu.findItem(R.id.signout);


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

        if (id == R.id.signin) {
            // Handle the camera action
            signInUser();
        } else if (id == R.id.tasks) {

        } else if (id == R.id.signout) {
            signOutUser();
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
        AuthHelper.getInstance().getAuth().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SmartLocation.with(this).location().stop();
        AuthHelper.getInstance().getAuth().removeAuthStateListener(authStateListener);
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
        } else if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
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

    private void initAuth() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                getCurrentUser();
            }
        };
    }

    private void signInUser() {
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build()))
                        .setLogo(R.drawable.ic_access_time_grey_96dp)
                        .setTheme(R.style.SignupTheme)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * Logout the current sign-in user
     */
    private void signOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        // user is now signed out
                        finish();
                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
    }


    private void getCurrentUser() {
        AuthHelper authHelper = AuthHelper.getInstance();
        FirebaseUser currentUser = authHelper.getAuth().getCurrentUser();
        if (!authHelper.isUserSignedIn()) {
            signInAnonymously();
        } else {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        populateHeaderMenuData(currentUser);
        if (currentUser.isAnonymous()) {
            showSignInMenu();
        } else {
            showSignOutMenu();
        }
    }

    @Override
    public void showSignInMenu() {
        itemNavMenuSignout.setVisible(false);
        itemNavMenuSignin.setVisible(true);
    }

    @Override
    public void showSignOutMenu() {
        itemNavMenuSignin.setVisible(false);
        itemNavMenuSignout.setVisible(true);
    }

    /**
     * Sign a user anonymously if there are no credentials for the user
     */
    private void signInAnonymously() {
        if (!isSigningInAnonymousUser) {
            isSigningInAnonymousUser = true;
            auth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            isSigningInAnonymousUser = false;
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                // this should be executed on the listener
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, R.string.network_error_anonymous_first_time,
                                        Toast.LENGTH_SHORT).show();
                                // this should be executed on the listener
                            }
                        }
                    });
        }
    }

    private void populateHeaderMenuData(FirebaseUser user) {
        View header = navigationView.getHeaderView(0);
        ImageView profileIV = header.findViewById(R.id.imageView);
        TextView userNameTV = header.findViewById(R.id.nameTV);
        TextView emailTV = header.findViewById(R.id.emailTV);

        if (user != null && !user.isAnonymous()) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String phoneNumber = user.getPhoneNumber();

            emailTV.setText(email);
            userNameTV.setText(!TextUtils.isEmpty(name) ? name : phoneNumber);
            if (photoUrl != null) {
                Picasso.get().load(photoUrl).into(profileIV);
            }
        } else {
            emailTV.setText("");
            userNameTV.setText("");
        }
    }

    // TODO: 16/08/2018 on create task, the info the the view is empty does not disapear
    // TODO: 16/08/2018 on sign in or sign out the RV Adapater does not listen to the new user

}
