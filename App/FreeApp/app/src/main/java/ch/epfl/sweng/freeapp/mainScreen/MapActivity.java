package ch.epfl.sweng.freeapp.mainScreen;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.FakeCommunicationLayer;

/**
 *
 * Important note: If running this Activity on an emulator, do not worry if by clicking
 * the my location button (top-right hand corner of the screen) nothing happens.
 * Even if the GPS/Location is enabled, the emulator need to be provided a mock location
 * in order to function properly.
 * Go to Tools -> Android -> Android Device Monitor, then under location controls, go on the
 * Manual tab, choose decimal and provide the longitude and latitude of your choice (do this while
 * the app is running on the emulator).
 * Papeete: longitude: -149.558476      latitude: -17.551625
 *
 * Created by lois on 11/19/2015
 *
 */
public class MapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        try {
            setUpMapIfNeeded();
        } catch (MapException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setUpMapIfNeeded();
        } catch (MapException e) {
            e.printStackTrace();
        }
    }

    private void setUpMapIfNeeded() throws MapException {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() throws MapException {
        displaySubmissionMarkers();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    /**
     *
     * Displays markers corresponding to submissions that were listed in the AroundYou tab.
     * Submissions for which no coordinates are found (invalid address) do not get a marker.
     *
     */
    private void displaySubmissionMarkers() throws MapException {
        //TODO: use real communication layer when available
        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        ArrayList<Submission> shortcuts = null;
        try {
            shortcuts = fakeCommunicationLayer.sendSubmissionsRequest();
        } catch (JSONException e) {
            throw new MapException();
        }

        for (Submission shortcut : shortcuts) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = new ArrayList<>();

            //Get maximum 1 address
            try {
                addresses = geocoder.getFromLocationName(shortcut.getLocation(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //If an address has been found
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions marker = new MarkerOptions().position(latLng).title(shortcut.getName());
                mMap.addMarker(marker);
            }
        }
    }
}
