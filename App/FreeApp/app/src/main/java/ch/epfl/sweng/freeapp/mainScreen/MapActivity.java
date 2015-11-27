package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

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

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //mShortcuts will contain the shortcuts retrieved by the asynchronous task
            new DownloadWebpageTask().execute(); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

        } else {
            //Connection problem
            displayToast("Connection problem");
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
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
        mMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    /**
     *
     *Display markers corresponding to all submissions
     *
     */
    private void displaySubmissionMarkers(ArrayList<Submission> submissions) throws MapException, CommunicationLayerException {

        //Each submission will get 1 marker if at least 1 geographical location is found with the
        //address, or none if the address is not valid.
        for (Submission submission : submissions) {
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
                    LatLng latLng = new LatLng(latitude, longitude);
                    MarkerOptions marker = new MarkerOptions().position(latLng).title(submission.getName());
                    mMap.addMarker(marker);
                }
            }
        }
    }

    private class DownloadWebpageTask extends AsyncTask<Void, Void, ArrayList<Submission>> {

        @Override
        protected ArrayList<Submission> doInBackground(Void ... params) {

            try {
                setUpMapIfNeeded();
            } catch (MapException e) {
                e.printStackTrace();
            }

            ArrayList<Submission> submissions = null;
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

            try {
                //FIXME: once server is ready, use sendAroundYouRequest
                submissions = communicationLayer.sendSubmissionsRequest();
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }

            return submissions;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Submission> submissions) {

            if(submissions.size() == 0){
                displayToast("No submissions in your area");
            } else {
                try {
                    displaySubmissionMarkers(submissions);
                } catch (MapException e) {
                    e.printStackTrace();
                } catch (CommunicationLayerException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void displayToast(String message){
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }
}
