package de.tubs.campusjagd.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.gps.MapsHelper;

/**
 * Stats fragment showing all found rooms
 *
 * @author l.brettin@tu-bs.de
 */
public class StatsFragment extends Fragment implements OnMapReadyCallback {

    // View representation of the map
    private MapView mMapView;

    // Object representation of the map
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        this.init(view, savedInstanceState);

        return view;
    }

    /**
     * Inits the google maps api in the fragment
     * @param view Inflated View
     * @param savedInstanceState Saved states for the view creation
     */
    private void init(View view, Bundle savedInstanceState) {
        // Init map
        mMapView = view.findViewById(R.id.stats_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

    }

    /**
     * The map needs some time to be ready. When its ready we will initiate the points
     * @param googleMap Object representation of the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setup all maps elements
        MapsHelper helper = new MapsHelper(StatsFragment.this.getContext(), mMap);
        helper.createPointsOnMap();
        helper.setCameraPosition();
    }
}
