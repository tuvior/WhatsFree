package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.util.ArrayList;


import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByLocation;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
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
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new DownloadWebpageTask().execute(); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.
            } else {
                //Connection problem
                displayToast("Connection problem");
            }

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

    private class DownloadWebpageTask extends AsyncTask<Void, Void, ArrayList<Submission>> {

        @Override
        protected ArrayList<Submission> doInBackground(Void ... params) {
            ArrayList<Submission> submissions;
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

            //TODO: remove once debugged
            ArrayList<Submission> fakeSubmissions = null;
            FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
            try {
                fakeSubmissions = fakeCommunicationLayer.sendSubmissionsRequest();
                submissions = communicationLayer.sendSubmissionsRequest();
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return fakeSubmissions;
            //return submissions;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Submission> submissions) {
            // Loading map
            try {
                initializeMap();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Perform these actions only once the map is correctly initialized
            //Enable myLocation button
            googleMap.setMyLocationEnabled(true);

            //Enable zoom in/ out buttons
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            centerCameraUser(user_location);
            displaySubmissionMarkers(submissions);
        }

    }

    /**
     * Center the camera and places a marker on the user's location
     */
    private void centerCameraUser(LatLng userLocation) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 10);
        googleMap.animateCamera(cameraUpdate);
    }

    /**
     *
     * Displays markers corresponding to submissions that were listed in the AroundYou tab.
     * Submissions for which no coordinates are found (invalid address) do not get a marker.
     *
     */
    private void displaySubmissionMarkers(ArrayList<Submission> submissions) {
        for (Submission submission : submissions) {
            SortSubmissionByLocation sortSubmissionByLocation = new SortSubmissionByLocation(this, null);
            LatLng submissionLatLng = sortSubmissionByLocation.getSubmissionLatLng(submission);

            //TODO remove below once debugged
            /**
            LatLng submissionLatLng = null;

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = new ArrayList<>();

            if(submission.getLocation() != null) {
                //Get maximum 1 address
                try {
                    addresses = geocoder.getFromLocationName(submission.getLocation(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //If an address has been found
                if (addresses.size() > 0) {
                    double latitude = addresses.get(0).getLatitude();
                    double longitude = addresses.get(0).getLongitude();
                    submissionLatLng = new LatLng(latitude, longitude);
                }

            }

             **/

            if ( submissionLatLng != null ) {
                MarkerOptions marker = new MarkerOptions().position(submissionLatLng).title(submission.getName());
                googleMap.addMarker(marker);
            }

        }
    }

    private void displayToast(String message){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);

        toast.show();
    }


}
