package ch.epfl.sweng.freeapp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * This communication layer is independent of the server and allows sending the app
 * some predefined responses.
 *
 * Created by lois on 11/13/15.
 */
public class FakeCommunicationLayer implements  DefaultCommunicationLayer {

    private int startTime = 17;
    private int endTime = 18;
    private Submission freeCroissants;
    private Submission unicornDiscount;
    private Submission freeClubEntrance;
    private Bitmap bitmapImage;
    private String image;
    private String keywords = "Beautiful Fun nice ";
    private String location = "Ecublens Epfl";
    private Calendar startOfEvent = Calendar.getInstance();
    private Calendar endOfEvent = Calendar.getInstance();

    private Submission.Builder submissionBuilderCroissant = new Submission.Builder();
    private Submission.Builder submissionBuilderUnicorn = new Submission.Builder();
    private Submission.Builder submissionBuilderFreeClubEntrance = new Submission.Builder();


/*
    public FakeCommunicationLayer(){

        submissionBuilderCroissant.name("Free Croissants");
        submissionBuilderCroissant.description("There's a huge croissant giveaway at Flon!");
        submissionBuilderCroissant.category(SubmissionCategory.FOOD);


        submissionBuilderUnicorn.name("Unicorn Discount");
        submissionBuilderUnicorn.description("Get one of our wonderful white unicorns!");
        submissionBuilderUnicorn.category(SubmissionCategory.MISCELLANEOUS);

        submissionBuilderFreeClubEntrance.name("Free Entrance Tonight");
        submissionBuilderFreeClubEntrance.description("Come get wasted for free tonight!");
        submissionBuilderFreeClubEntrance.category(SubmissionCategory.NIGHTLIFE);

        freeCroissants = createSubmission(submissionBuilderCroissant);
        unicornDiscount = createSubmission(submissionBuilderUnicorn);
        freeClubEntrance = createSubmission(submissionBuilderFreeClubEntrance);
    }

 */
    public FakeCommunicationLayer() {

      //AssetManager assetManager = activity.getAssets();

        this.image = "RubbishImage";

        submissionBuilderCroissant.name("Free Croissants");
        submissionBuilderCroissant.description("There's a huge croissant giveaway at Flon!");
        submissionBuilderCroissant.category(SubmissionCategory.FOOD);


        submissionBuilderUnicorn.name("Unicorn Discount");
        submissionBuilderUnicorn.description("Get one of our wonderful white unicorns!");
        submissionBuilderUnicorn.category(SubmissionCategory.MISCELLANEOUS);

        submissionBuilderFreeClubEntrance.name("Free Entrance Tonight");
        submissionBuilderFreeClubEntrance.description("Come get wasted for free tonight!");
        submissionBuilderFreeClubEntrance.category(SubmissionCategory.NIGHTLIFE);

        freeCroissants = createSubmission(submissionBuilderCroissant);
        unicornDiscount = createSubmission(submissionBuilderUnicorn);
        freeClubEntrance = createSubmission(submissionBuilderFreeClubEntrance);

    }

    public ResponseStatus sendAddSubmissionRequest(Submission submission) {
        //TODO
        return ResponseStatus.OK;
    }

    public JSONArray sendWhatIsNewRequest() throws JSONException {

        JSONArray jsonSubmissions = new JSONArray();

        jsonSubmissions.put(0, createNameJson(freeCroissants));
        jsonSubmissions.put(1, createNameJson(unicornDiscount));
        jsonSubmissions.put(2, createNameJson(freeClubEntrance));

        return jsonSubmissions;
    }

    public Submission fetchSubmission(String name) {

        Submission submission;
        switch (name) {
            case "Free Croissants":
                submission = freeCroissants;
                break;
            case "Unicorn Discount":
                submission = unicornDiscount;
                break;
            case "Free Entrance Tonight":
                submission = freeClubEntrance;
                break;
            default:
                return null;
        }

        return submission;
    }

    private JSONObject createNameJson(Submission submission) throws JSONException {

        String title = submission.getName();
        JSONObject submissionJson = new JSONObject();

        submissionJson.put("name", title);

        return submissionJson;
    }

    private String encodeImage(AssetManager assetManager) {
        if (assetManager != null) {

            InputStream inputStream = null;
            try {
                inputStream = assetManager.open("a.png");
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmapImage = BitmapFactory.decodeStream(inputStream);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


        } else {
            return null;
        }

    }

    private Submission createSubmission(Submission.Builder builder){


        builder.location(location);
        builder.category(SubmissionCategory.FOOD);

        startOfEvent.set(Calendar.HOUR_OF_DAY, startTime);
        endOfEvent.set(Calendar.HOUR_OF_DAY, endTime);

        builder.startOfEvent(startOfEvent);
        builder.endOfEvent(endOfEvent);
        builder.image(image);
        builder.keywords(keywords);
        builder.submitted(startOfEvent);

        return builder.build();

    }
}
