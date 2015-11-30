package ch.epfl.sweng.freeapp.mainScreen;

/**
 * Created by lois on 11/6/15.
 */

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.R;

public class AroundYouFragment extends ListFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String BUNDLE = "bundle";
    public static final String USER_LOCATION = "user_location";

    // LogCat tag
    private static final String TAG = AroundYouFragment.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private LatLng latLng;

    public AroundYouFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.around_you_fragment, container, false);

        //get user's location
        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //mShortcuts will contain the shortcuts retrieved by the asynchronous task
            new DownloadWebpageTask().execute(); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

        } else {
            //Connection problem
            displayToast("Connection problem");
        }

        //Set listener for mapButton
        ImageButton mapButton = (ImageButton) rootView.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                Bundle args = new Bundle();
                args.putParcelable(USER_LOCATION, latLng);
                intent.putExtra(BUNDLE, args);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /**
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Submission submissionShortcut = (Submission)getListAdapter().getItem(position);
        String submissionId = submissionShortcut.getId();
        Intent intent = new Intent(v.getContext(), DisplaySubmissionActivity.class);
        intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submissionId);
        startActivity(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        latLng = getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    /**
     * Sort submissions according to how close they are to you
     */
    public ArrayList<Submission> sortSubmissions(ArrayList<Submission> submissionShortcuts){
        Collections.sort(submissionShortcuts, new Comparator<Submission>() {
            @Override
            public int compare(Submission lhs, Submission rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return submissionShortcuts;
    }

    private class DownloadWebpageTask extends AsyncTask<Void, Void, ArrayList<Submission>> {

        @Override
        protected ArrayList<Submission> doInBackground(Void ... params) {
            ArrayList<Submission> submissions = null;
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

            try {
                submissions = communicationLayer.sendSubmissionsRequest();
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }

            return submissions;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Submission> submissions) {

            SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, submissions);
            setListAdapter(adapter);
            if(submissions.size() == 0){
                displayToast("No submissions around you yet");
            }

        }

    }

    private void displayToast(String message){
        Context context = getActivity().getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    /**
     * Method to display the location on UI
     */
    private LatLng getLocation() {

        LatLng latLng = null;

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            latLng = new LatLng(latitude, longitude);

        } else {
            displayToast("Couldn't get the location. Make sure location is enabled on the device");
        }

        return latLng;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
               displayToast("This device is not supported.");
            }
            return false;
        }
        return true;
    }

}
