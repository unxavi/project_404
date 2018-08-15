package unxavi.com.github.project404.features.main.detail;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import unxavi.com.github.project404.R;
import unxavi.com.github.project404.features.main.MainActivity;
import unxavi.com.github.project404.model.WorkLog;
import unxavi.com.github.project404.utils.Utils;

/**
 * A fragment representing a single DeleteTimeLog detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link WorkLogDetailActivity}
 * on handsets.
 */
public class WorkLogDetailFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.date)
    TextView dateTV;

    @BindView(R.id.task)
    TextView taskTV;

    @BindView(R.id.actionIcon)
    ImageView actionIconIV;

    @BindView(R.id.action)
    TextView actionTV;

    Unbinder unbinder;

    private WorkLog workLog;

    private double latitude = 0.0;

    private double longitude = 0.0;

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
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.work_log_detail_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        taskTV.setText(workLog.getTask().getName());
        dateTV.setText(Utils.dateToString(workLog.getTimestamp(), locale));
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (workLog.getLatitude() != null && workLog.getLongitude() != null) {
            latitude = workLog.getLatitude();
            longitude = workLog.getLongitude();
        }
        if (getContext() != null && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        LatLng cameraPosition = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(cameraPosition));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 14));
    }
}
