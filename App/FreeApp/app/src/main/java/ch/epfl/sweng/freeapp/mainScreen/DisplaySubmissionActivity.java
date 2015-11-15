package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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
        String submissionName = intent.getStringExtra(AroundYouFragment.SUBMISSION_MESSAGE);

        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        Submission submission = fakeCommunicationLayer.fetchSubmission(submissionName);
        TextView nameTextView = (TextView)findViewById(R.id.submissionName);
        nameTextView.setText(submission.getName());
        TextView descriptionTextView = (TextView)findViewById(R.id.submissionDescription);
        descriptionTextView.setText(submission.getDescription());

        //TODO Set submissionImageView to an image retrieved from the server
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
}
