package ch.epfl.sweng.freeapp.mainScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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





    private Submission submissionDisplayed;
    //useful for testing
    private DefaultCommunicationLayer communicationLayer = ProvideCommunicationLayer.getCommunicationLayer();


    private ImageButton likeButton;
    private ImageButton dislikeButton;

    private int defaultColor = android.R.color.transparent;

    private boolean likedClicked = false;
    private boolean dislikedClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_submission);

        this.likeButton = (ImageButton) findViewById(R.id.like);
        this.dislikeButton = (ImageButton) findViewById(R.id.dislike);


        likeButton.setBackgroundResource(defaultColor);
        dislikeButton.setBackgroundResource(defaultColor);


        // this.likeButton.setColorFilter(DEFAULT_COLOR);
        //this.dislikeButton.setColorFilter(DEFAULT_COLOR);


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

        if(submissionDisplayed != null) {

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
    }

    public void likeButtonOnClick(View view) {

        if(submissionDisplayed != null) {
            Vote vote = Vote.LIKE;

            if (likedClicked) {
                vote = Vote.NEUTRAL;
            }

            Vote buttonClicked = Vote.LIKE;

            SubmissionVoteWrapper submissionVoteWrapper = new SubmissionVoteWrapper();
            submissionVoteWrapper.submission = submissionDisplayed;
            submissionVoteWrapper.voteToServer = vote;

            new GetVoteTask(this, buttonClicked).execute(submissionVoteWrapper);


        }


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
                    if (dislikedClicked) {  // if i clicked previously the dislike button
                        dislikedClicked = false;
                        //dislikeButton.setBackgroundResource(defaultColor);

                        /**
                         * Set dislike Button to default color; --done
                         */

                    dislikeButton.setBackgroundResource(defaultColor);
                    }

                    /**
                     * Set the liked button to blue; --done
                     * Set likedButtonClicked = true; --done
                     */

                    likedClicked = true;
                    likeButton.setBackgroundResource(android.R.color.holo_blue_light);


                } else if (typeVote == Vote.DISLIKE) {

                    if (likedClicked) {  // if i clicked previously the like button
                        likedClicked = false;
                        /**
                         * Set liked button to default color --done
                         */

                    likeButton.setBackgroundResource(defaultColor);

                    }

                    /**
                     * Set Disliked button color to red   --done
                     * Set dislikedButtonClicked  = true  --done
                     */

                    dislikedClicked = true;
                    dislikeButton.setBackgroundResource(android.R.color.holo_red_light);



                } else {

                    assert (typeVote == Vote.NEUTRAL);  //Undo action
                    //Case when neutral ,basically we want to undo or action, we have already clicked before.


                    if (buttonClicked == Vote.LIKE) {


                        /**
                         * Set the likeButtonClicked = false --done
                         * set it's color to its default color as before --done
                         */

                      likedClicked = false;
                      likeButton.setBackgroundResource(defaultColor);

                    } else {

                        /**
                         * Set the dislikedButtonClicked = false --done
                         * set it's color to its default color as before; --done
                         */

                        dislikedClicked = false;
                        dislikeButton.setBackgroundResource(defaultColor);

                    }
                }

            }else {

               Toast.makeText(context, "Server problem", Toast.LENGTH_SHORT);

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

                /*

                likes = submission.getLikes();
                dislikes = submission.getDislikes();

                TextView numberOfLikes = (TextView) findViewById(R.id.numberOfLikes);
                TextView numberOfDislikes = (TextView) findViewById(R.id.numberOfDislikes);

                numberOfLikes.setText(Integer.toString(likes));
                numberOfDislikes.setText(Integer.toString(dislikes));
               */
                TextView nameTextView = (TextView) findViewById(R.id.submissionName);
                nameTextView.setText(submission.getName());

                TextView descriptionTextView = (TextView) findViewById(R.id.submissionDescription);
                descriptionTextView.setText(submission.getDescription());



                Vote vote = submission.getVote();

                if( vote  == Vote.LIKE){

                    likeButton.setBackgroundResource(android.R.color.holo_blue_light);
                    likedClicked = true;

                }else if(vote == Vote.NEUTRAL){

                     likedClicked = false;
                     dislikedClicked = false;

                }else{
                    //dislike

                    dislikeButton.setBackgroundResource(android.R.color.holo_red_light);
                    dislikedClicked = true;
                }


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
                ratingTextView.setText("Rating: " + String.valueOf(submission.getRating()));

            }
        }

    }
}
