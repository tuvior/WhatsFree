package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.R;

/**
 * Created by lois on 11/6/15.
 */
public class WhatsNewFragment extends ListFragment {

  private   ArrayList<Submission> mShortcuts;
    private  static String ID = "ID";

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
            new DownloadWebpageTask().execute(); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

        } else {
            //Connection problem
            displayToast("Connection problem");
        }

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
        Submission submission = (Submission)getListAdapter().getItem(position);

        String submissionId = submission.getId();
        Intent intent = new Intent(v.getContext(), DisplaySubmissionActivity.class);
        intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submissionId);

        startActivity(intent);
    }

    /**
     * Sorts submissions according to their submission time
     * @return the sorted list of submissions
     */
    public ArrayList<Submission> sortSubmissions(ArrayList<Submission> submissions){
        //TODO
        return null;
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

                return null;

            }

            return submissions;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Submission> submissions) {

            if(submissions == null){

                    displayToast("No new submissions yet");

            }else {

                SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, submissions);
                setListAdapter(adapter);

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
