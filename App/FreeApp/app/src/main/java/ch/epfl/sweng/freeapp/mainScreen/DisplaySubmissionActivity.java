package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.freeapp.BuildConfig;
import ch.epfl.sweng.freeapp.R;
import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultCommunicationLayer;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.communication.ProvideCommunicationLayer;
import ch.epfl.sweng.freeapp.communication.ResponseStatus;

public class DisplaySubmissionActivity extends AppCompatActivity {

    private int likes;
    private int dislikes;
    private boolean dislikedClicked = false;
    private boolean likedClicked = false;


    private Submission submissionDisplayed;

    //useful for testing
    private DefaultCommunicationLayer communicationLayer = ProvideCommunicationLayer.getCommunicationLayer();
    private int defaultColor = Color.LTGRAY;

    private ImageButton likeButton;
    private ImageButton dislikeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_submission);

        this.likeButton = (ImageButton) findViewById(R.id.like);
        this.dislikeButton = (ImageButton) findViewById(R.id.dislike);
        // this.likeButton.setColorFilter(defaultColor);
        //this.dislikeButton.setColorFilter(defaultColor);


        // Get the message from the intent
        Intent intent = getIntent();
        String submissionId = intent.getStringExtra(MainScreenActivity.SUBMISSION_MESSAGE);

        //Check connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadWebPageTask(this).execute(submissionId); //Caution: submission MUST be retrieved from an async task (performance). Otherwise the app will crash.

        } else {
            //Connection problem
            displayToast();
        }

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

    public void dislikeButtonOnclick(View view) {

        //how do you know when a button is already clicked when going to the activity for the first time.
        //Easiest when inside activity, you can return 0  1 -1
        //When starting activity, you can only send 1 -1 and based on the server response//

        Vote vote = Vote.DISLIKE;


        if (dislikedClicked) {


            vote = Vote.NEUTRAL;
        }


        Vote buttonClicked = Vote.DISLIKE;

        SubmissionVoteWrapper submissionVoteWrapper = new SubmissionVoteWrapper();
        submissionVoteWrapper.submission = submissionDisplayed;
        submissionVoteWrapper.voteToServer = vote;

        new GetVoteTask(this, buttonClicked).execute(submissionVoteWrapper);

        dislikedClicked = true;
    }

    public void likeButtonOnClick(View view) {
        Vote vote = Vote.LIKE;

        if (likedClicked) {
            vote = Vote.NEUTRAL;
        }

        Vote buttonClicked = Vote.LIKE;

        SubmissionVoteWrapper submissionVoteWrapper = new SubmissionVoteWrapper();
        submissionVoteWrapper.submission = submissionDisplayed;
        submissionVoteWrapper.voteToServer = vote;

        new GetVoteTask(this, buttonClicked).execute(submissionVoteWrapper);

        likedClicked = true;


    }

    private Bitmap decodeImage(String input) {
        try {
            byte[] decodedByte = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
            return null;

        }
    }

    private void displayToast() {
        Context context = getApplicationContext();
        CharSequence text = "Error retrieving submission.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    //class used to pass Multiple arguments in Async task
    private class SubmissionVoteWrapper {
        public Submission submission;
        public Vote voteToServer;
    }

    private class GetVoteTask extends AsyncTask<SubmissionVoteWrapper, Void, ResponseStatus> {

        private Context context;
        private Vote typeVote;
        private Vote buttonClicked;


        public GetVoteTask(Context context, Vote buttonClicked) {
            this.context = context;
            this.buttonClicked = buttonClicked;
        }

        @Override
        protected ResponseStatus doInBackground(SubmissionVoteWrapper... params) {
            typeVote = params[0].voteToServer;
            try {
                return communicationLayer.sendVote(params[0].submission, params[0].voteToServer);
            } catch (CommunicationLayerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseStatus status) {


            if (status == null) {

                Toast.makeText(context, "Problem from the server side", Toast.LENGTH_SHORT).show();
            } else if (status == ResponseStatus.OK) {
                if (typeVote == Vote.LIKE) {

                    submissionDisplayed.setLikes(submissionDisplayed.getLikes() + 1);
                    TextView view = (TextView) (findViewById(R.id.numberOfLikes));
                    view.setText(Integer.toString(submissionDisplayed.getLikes()));
                    likeButton.setColorFilter(Color.rgb(135, 206, 250));  //Light blue


                } else if (typeVote == Vote.DISLIKE) {

                    submissionDisplayed.setDislikes(submissionDisplayed.getDislikes() + 1);
                    TextView view = (TextView) (findViewById(R.id.numberOfDislikes));
                    view.setText(Integer.toString(submissionDisplayed.getDislikes()));
                    dislikeButton.setColorFilter(Color.rgb(255, 0, 0));  // Red

                } else {
                    //Case when neutral ,basically we want to undo or action.

                    if (buttonClicked == Vote.LIKE) {

                        submissionDisplayed.setLikes(submissionDisplayed.getLikes() - 1);
                        TextView view = (TextView) (findViewById(R.id.numberOfLikes));
                        view.setText(Integer.toString(submissionDisplayed.getLikes()));

                        likeButton.setColorFilter(defaultColor);
                    } else {

                        submissionDisplayed.setLikes(submissionDisplayed.getDislikes() - 1);
                        TextView view = (TextView) (findViewById(R.id.numberOfDislikes));
                        view.setText(Integer.toString(submissionDisplayed.getDislikes()));
                        dislikeButton.setColorFilter(defaultColor);

                    }

                }
            } else {

                //Response status will be some failure indicating that it already exists
                if (buttonClicked == Vote.LIKE) {

                    likeButton.setColorFilter(Color.rgb(135, 206, 250));  //Light blue
                } else {
                    dislikeButton.setColorFilter(Color.rgb(255, 0, 0)); //Red


                }

            }
        }
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, Submission> {

        private Context context;

        public DownloadWebPageTask(Context context) {

            this.context = context;
        }

        @Override
        protected Submission doInBackground(String... submissionId) {

            //Only 1 id should be passed as parameter
            if (BuildConfig.DEBUG && (submissionId.length != 1)) {
                throw new AssertionError();
            }
            String id = submissionId[0];
            Submission submission = null;

            CommunicationLayer communicationLayer = new CommunicationLayer(new DefaultNetworkProvider());
            try {

                submission = communicationLayer.fetchSubmission(id);
                submissionDisplayed = submission;

            } catch (CommunicationLayerException e) {
                e.printStackTrace();
            }


            return submission;

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Submission submission) {

            if (submission != null) {

                likes = submission.getLikes();
                dislikes = submission.getDislikes();

                TextView numberOfLikes = (TextView) findViewById(R.id.numberOfLikes);
                TextView numberOfDislikes = (TextView) findViewById(R.id.numberOfDislikes);

                numberOfLikes.setText(Integer.toString(likes));
                numberOfDislikes.setText(Integer.toString(dislikes));

                TextView nameTextView = (TextView) findViewById(R.id.submissionName);
                nameTextView.setText(submission.getName());

                TextView descriptionTextView = (TextView) findViewById(R.id.submissionDescription);
                descriptionTextView.setText(submission.getDescription());

                Bitmap image = decodeImage(submission.getImage());

                if (image == null) {
                    Toast.makeText(context, "Unknown Image ", Toast.LENGTH_SHORT).show();
                } else {
                    ImageView submissionImage = (ImageView) findViewById(R.id.submissionImageView);
                    submissionImage.setImageBitmap(image);
                }

                if (submission.getLocation() != null) {
                    TextView locationTextView = (TextView) findViewById(R.id.submissionLocation);
                    locationTextView.setText("Location: " + submission.getLocation());
                }

                TextView ratingTextView = (TextView) findViewById(R.id.submissionRating);
                ratingTextView.setText(String.valueOf(submission.getRating()));

            }
        }

    }
}
