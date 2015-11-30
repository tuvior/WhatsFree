package ch.epfl.sweng.freeapp.mainScreen;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.BuildConfig;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

public class CategoryDisplaySubmissionsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_submissions_category);

        // Get the message from the intent
        Intent intent = getIntent();
        String category = intent.getStringExtra(CategoriesFragment.CATEGORY_MESSAGE);

        if(BuildConfig.DEBUG && (!SubmissionCategory.contains(category))){
            throw new AssertionError();
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //mShortcuts will contain the shortcuts retrieved by the asynchronous task
            new DownloadWebpageTask().execute(SubmissionCategory.valueOf(category)); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

        } else {
            //Connection problem
            displayToast("No submissions in this category yet");
        }

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


    private class DownloadWebpageTask extends AsyncTask<SubmissionCategory, Void, ArrayList<Submission>> {

        @Override
        protected ArrayList<Submission> doInBackground(SubmissionCategory ... category) {
            ArrayList<Submission> submissions = null;
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

            if(BuildConfig.DEBUG && (category.length != 1)){
                throw new AssertionError();
            }

            try {
                submissions = communicationLayer.sendCategoryRequest(category[0]);
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }

            return submissions;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Submission> submissions) {

            SubmissionListAdapter adapter = new SubmissionListAdapter(getApplicationContext(), R.layout.item_list_row, submissions);
            setListAdapter(adapter);
            if(submissions.size() == 0){
                displayToast("No submissions in this category yet");
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
