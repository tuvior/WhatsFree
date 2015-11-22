package ch.epfl.sweng.freeapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by lois on 11/19/2015
 *
 */
public class MapActivity extends AppCompatActivity {

    // Google Map
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        try {
            // Loading map
            initializeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() throws JSONException {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            centerCameraUser();
            displaySubmissionMarkers();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initializeMap();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Center the camera and places a marker on the user's location
     */
    private void centerCameraUser() {
        //TODO: figure out how to get the user's location
        Location location = getBestLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
       // LatLng latLng = new LatLng(-17.536407, -149.566035);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        googleMap.animateCamera(cameraUpdate);

        MarkerOptions marker = new MarkerOptions().position(latLng).title("Your Location");
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        googleMap.addMarker(marker);
    }

    /**
     *
     * Displays markers corresponding to submissions that were listed in the AroundYou tab.
     * Submissions for which no coordinates are found (invalid address) do not get a marker.
     *
     */
    private void displaySubmissionMarkers() throws JSONException {
        //TODO: use real communication layer when available
        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        ArrayList<SubmissionShortcut> shortcuts = fakeCommunicationLayer.sendSubmissionsRequest();

        for (SubmissionShortcut shortcut : shortcuts) {
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
                googleMap.addMarker(marker);
            }
        }
    }

    /**
     * try to get the 'best' location selected from all providers
     */
    private Location getBestLocation() {
        Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
        Location networkLocation =
                getLocationByProvider(LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpslocation;
        }
        return null;
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private Location getLocationByProvider(String provider) {
        Location location = null;

        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            if (locationManager.isProviderEnabled(provider)) {
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return location;
    }
}
