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

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.mainScreen.SubmissionShortcut;

/**
 * This communication layer is independent of the server and allows sending the app
 * some predefined responses.
 *
 * Created by lois on 11/13/15.
 */
public class FakeCommunicationLayer implements  DefaultCommunicationLayer {


    private int startTime = 17;
    private int endTime = 18;

    private Bitmap bitmapImage;
    private String image;
    private String keywords = "Beautiful Fun nice ";
    private String location = "Ecublens Epfl";
    private Calendar startOfEvent = Calendar.getInstance();
    private Calendar endOfEvent = Calendar.getInstance();

    private Submission.Builder submissionBuilderCroissant = new Submission.Builder();
    private Submission.Builder submissionBuilderUnicornDiscount = new Submission.Builder();
    private Submission.Builder submissionBuilderFreeClubEntrance = new Submission.Builder();
    private Submission.Builder submissionBuilderFreeDonuts =  new Submission.Builder();



    private static Submission freeCroissants = new Submission("Free Croissants", "There's a huge croissant giveaway at Flon!", SubmissionCategory.FOOD);
    private static Submission freeDonuts =  new Submission("Free Donuts", "Migros gives a free dozen to the first 5 customers",SubmissionCategory.FOOD);
    private static Submission unicornDiscount = new Submission("Unicorn Discount", "Get one of our wonderful white unicorns!", SubmissionCategory.MISCELLANEOUS);
    private static Submission freeClubEntrance  = new Submission("Free Entrance Tonight", "Come get wasted for free tonight!", SubmissionCategory.NIGHTLIFE);


    public FakeCommunicationLayer() {

      //AssetManager assetManager = activity.getAssets();

        this.image = "RubbishImage";



        freeCroissants = createSubmission(submissionBuilderCroissant);
        unicornDiscount = createSubmission(submissionBuilderUnicornDiscount);
        freeClubEntrance = createSubmission(submissionBuilderFreeClubEntrance);
        freeDonuts = createSubmission(submissionBuilderFreeDonuts);


    }

    public ResponseStatus sendAddSubmissionRequest(Submission submission) {
        //TODO
        return ResponseStatus.OK;
    }

    public static JSONArray sendWhatIsNewRequest() throws JSONException {

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

    public static ArrayList<SubmissionShortcut> sendCategoryRequest(SubmissionCategory category){

        ArrayList<SubmissionShortcut> submissionShortcuts = new ArrayList<>();

        switch (category){
            case FOOD: {
                submissionShortcuts.add(toShortcut(freeCroissants));
                submissionShortcuts.add(toShortcut(freeDonuts));
            }
                break;
            case MISCELLANEOUS: {
                submissionShortcuts.add(toShortcut(unicornDiscount));
            }
                break;
            case NIGHTLIFE: {
                submissionShortcuts.add(toShortcut(freeClubEntrance));
            }
                break;
            default:
        }

        return submissionShortcuts;
    }

    /**
     * The server sends the submissions as a JSONArray, and the communication layer
     * conveys those to the tabs as an ArrayList
     * @param jsonSubmissions
     * @return
     * @throws JSONException
     */
    public static ArrayList<SubmissionShortcut> jsonArrayToArrayList(JSONArray jsonSubmissions) throws JSONException {

        ArrayList<SubmissionShortcut> submissionsList = new ArrayList<>();

        for(int i = 0; i < jsonSubmissions.length(); i++){
            //TODO: also include image
            JSONObject jsonSubmission = jsonSubmissions.getJSONObject(i);
            String name = jsonSubmission.getString("name");

            SubmissionShortcut submission = new SubmissionShortcut(name);
            submissionsList.add(submission);
        }

        return submissionsList;

    }


    private static JSONObject createNameJson(Submission submission) throws JSONException {

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

    /**
     * Transforms the submission into its shortcut equivalent
     *
     * @param submission
     * @return the shortcut version of the submission
     */
    private static SubmissionShortcut toShortcut(Submission submission){
        return new SubmissionShortcut(submission.getName());
    }


}
