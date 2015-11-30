package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.FakeCommunicationLayer;


/**
 *
 * Created by lois on 11/19/2015
 *
 */
public class MapActivity extends AppCompatActivity {

    // Google Map
    private GoogleMap googleMap;
    private LatLng user_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get the message from the intent
        Intent intent = getIntent();
        Bundle bundle = intent.getParcelableExtra(AroundYouFragment.BUNDLE);
        user_location = bundle.getParcelable(AroundYouFragment.USER_LOCATION);

        try {
            // Loading map
            initializeMap();

            //Perform these actions only once the map is correctly initialized
            //Enable myLocation button
            googleMap.setMyLocationEnabled(true);

            //Enable zoom in/ out buttons
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            centerCameraUser(user_location);
            displaySubmissionMarkers();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you.
     * Also retrieve
     * */
    private void initializeMap() throws JSONException {
        if (googleMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager() //Important note: DO NOT USE getFragmentManager(), it will not work for APIs below 23
                    .findFragmentById(R.id.map);

            googleMap = mapFrag.getMap();

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
    private void centerCameraUser(LatLng userLocation) {
        //TODO: figure out how to get the user's location
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 10);
        googleMap.animateCamera(cameraUpdate);

        MarkerOptions marker = new MarkerOptions().position(userLocation).title("Your Location");
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
        ArrayList<Submission> shortcuts = fakeCommunicationLayer.sendSubmissionsRequest();

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
                googleMap.addMarker(marker);
            }
        }
    }


}
