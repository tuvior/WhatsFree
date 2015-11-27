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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.communication.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.R;

public class AroundYouFragment extends ListFragment {

    private GoogleMap googleMap;
    private Location location;

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
                //intent.putExtra(MAP_MESSAGE, mapMessage);
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
        String submissionName = submissionShortcut.getName();
        Intent intent = new Intent(v.getContext(), DisplaySubmissionActivity.class);
        intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submissionName);
        startActivity(intent);
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
}
