package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.epfl.sweng.freeapp.CommunicationLayer;
import ch.epfl.sweng.freeapp.CommunicationLayerException;
import ch.epfl.sweng.freeapp.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.NetworkProvider;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.FakeCommunicationLayer;

public class DisplaySubmissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_submission);

        // Get the message from the intent
        Intent intent = getIntent();
        String submissionName = intent.getStringExtra(MainScreenActivity.SUBMISSION_MESSAGE);

        //CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());
        //Submission submission = null;

        //Check connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(submissionName); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.
        } else {
            //Connection problem
            displayToast();
        }

        /**
        try {
            submission = communicationLayer.fetchSubmission(submissionName);
        } catch (CommunicationLayerException e) {
            e.printStackTrace();
        }
        **/

        /**
        TextView nameTextView = (TextView)findViewById(R.id.submissionName);
        nameTextView.setText(submission.getName());

        TextView descriptionTextView = (TextView)findViewById(R.id.submissionDescription);
        descriptionTextView.setText(submission.getDescription());

        Bitmap image = decodeImage(submission.getImage());
        ImageView submissionImage = (ImageView) findViewById(R.id.submissionImageView);
        submissionImage.setImageBitmap(image);

         **/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_submission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, Submission> {

        @Override
        protected Submission doInBackground(String... submissionName) {

            assert(submissionName.length == 1);
            String name = submissionName[0];
            Submission submission = null;
            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());

            try {
                submission = communicationLayer.fetchSubmission(name);
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }

            return submission;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Submission submission) {

            TextView nameTextView = (TextView)findViewById(R.id.submissionName);
            nameTextView.setText(submission.getName());

            TextView descriptionTextView = (TextView)findViewById(R.id.submissionDescription);
            descriptionTextView.setText(submission.getDescription());

            Bitmap image = decodeImage(submission.getImage());
            ImageView submissionImage = (ImageView) findViewById(R.id.submissionImageView);
            submissionImage.setImageBitmap(image);

        }

    }

    private Bitmap decodeImage(String input){

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

    }

    private void displayToast(){
        Context context = getApplicationContext();
        CharSequence text = "Error retrieving submission.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }
}
