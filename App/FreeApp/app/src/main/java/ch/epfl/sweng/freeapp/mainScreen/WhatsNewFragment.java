package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.CommunicationLayer;
import ch.epfl.sweng.freeapp.CommunicationLayerException;
import ch.epfl.sweng.freeapp.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionShortcut;

/**
 * Created by lois on 11/6/15.
 */
public class WhatsNewFragment extends ListFragment {

    ArrayList<SubmissionShortcut> mShortcuts;

    public WhatsNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.whats_new_fragment, container, false);

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //mShortcuts will contain the shortcuts retrieved by the asynchronous task
            new DownloadWebpageTask().execute(); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

        } else {
            //Connection problem
            displayToast();
        }

        /**
        //Get the JSONArray corresponding to the submissions
        try {
            FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
            ArrayList<SubmissionShortcut> submissions = fakeCommunicationLayer.sendSubmissionsRequest();
            //Adapter provides a view for each item in the data set
            SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, submissions);
            this.setListAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        **/

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
        SubmissionShortcut submissionShortcut = (SubmissionShortcut)getListAdapter().getItem(position);
        String submissionName = submissionShortcut.getName();
        Intent intent = new Intent(v.getContext(), DisplaySubmissionActivity.class);
        intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submissionName);
        startActivity(intent);
    }

    /**
     * Sorts submissions according to their submission time
     * @return the sorted list of submissions
     */
    public ArrayList<SubmissionShortcut> sortSubmissions(ArrayList<SubmissionShortcut> submissionShortcuts){
        //TODO
        return null;
    }

    private class DownloadWebpageTask extends AsyncTask<Void, Void, ArrayList<SubmissionShortcut>> {

        @Override
        protected ArrayList<SubmissionShortcut> doInBackground(Void ... params) {
            ArrayList<SubmissionShortcut> shortcuts = null;
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

            try {
                shortcuts = communicationLayer.sendSubmissionsRequest();
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }

            return shortcuts;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<SubmissionShortcut> shortcuts) {

            //Adapter provides a view for each item in the data set
            SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, shortcuts);
            setListAdapter(adapter);

        }

    }

    private void displayToast(){
        Context context = getActivity().getApplicationContext();
        CharSequence text = "Error retrieving submission.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

}
