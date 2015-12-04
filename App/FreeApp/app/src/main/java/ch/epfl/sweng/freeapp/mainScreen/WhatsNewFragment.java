package ch.epfl.sweng.freeapp.mainScreen;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmission;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByEndOFEvent;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByLikes;
import ch.epfl.sweng.freeapp.SortingSubmissionAlgorithnms.SortSubmissionByName;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

/**
 * Created by lois on 11/6/15.
 */
public class WhatsNewFragment extends ListFragment {

    private   ArrayList<Submission> cachedSubmissions;
    private  static String ID = "ID";
    private static SortSubmission sortSubmissions ;




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


        Button button = (Button)rootView.findViewById(R.id.filterButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Sort Submission");
                dialog.setContentView(R.layout.sort_submission_dialog);
                dialog.show();

                final SortSubmission[] sortSubmissionList = new SortSubmission[1];

                RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.radioGroupFilter);


                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) dialog.findViewById(group.getCheckedRadioButtonId());

                        switch (radioButton.getId()) {
                            case R.id.byTime:
                                sortSubmissionList[0] = new SortSubmissionByEndOFEvent();
                                break;
                            case R.id.byLikes:
                                sortSubmissionList[0] = new SortSubmissionByLikes();
                                break;
                            case R.id.byName:
                                sortSubmissionList[0] = new SortSubmissionByName();
                                break;

                        }

                    }
                });



                final Button okButton = (Button)dialog.findViewById(R.id.dialogOkButton);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sortSubmissions = sortSubmissionList[0];

                        if (sortSubmissions != null) {
                            sortSubmissions.sort(cachedSubmissions);
                        }

                        SubmissionListAdapter adapter = new SubmissionListAdapter(getContext(), R.layout.item_list_row, cachedSubmissions);
                        setListAdapter(adapter);

                        dialog.dismiss();
                    }
                });

                //when i click on the dialog button i should re-fetch the submissions again and sort






            }
        });


        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //mShortcuts will contain the shortcuts retrieved by the asynchronous task
            new DownloadWebpageTask(getContext()).execute(); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

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
        private Context context;

        public  DownloadWebpageTask(Context context){
            this.context = context;

        }

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


                cachedSubmissions = submissions;
                if(sortSubmissions != null){

                    try {
                          sortSubmissions.sort(submissions);
                    }catch(Exception e ){
                        e.printStackTrace();
                        Toast.makeText(context, " Problem with server when sorting ", Toast.LENGTH_SHORT).show();
                    }
                }



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
