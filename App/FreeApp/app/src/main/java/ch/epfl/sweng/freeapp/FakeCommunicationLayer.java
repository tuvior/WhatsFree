package ch.epfl.sweng.freeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.mainScreen.SubmissionShortcut;

/**
 * This communication layer is independent of the server and allows sending the app
 * some predefined responses.
 *
 * Created by lois on 11/13/15.
 */
public class FakeCommunicationLayer{

    private static Submission freeCroissants = new Submission("Free Croissants", "There's a huge croissant giveaway at Flon!", SubmissionCategory.FOOD);
    private static Submission unicornDiscount = new Submission("Unicorn Discount", "Get one of our wonderful white unicorns!", SubmissionCategory.MISCELLANOUS);
    private static Submission freeClubEntrance = new Submission("Free Entrance Tonight", "Come get wasted for free tonight!", SubmissionCategory.NIGHTLIFE);

    public FakeCommunicationLayer(){

    }

    public ResponseStatus sendAddSubmissionRequest(Submission submission) {
        //TODO
        return null;
    }

    public static JSONArray sendWhatIsNewRequest() throws JSONException {

        JSONArray jsonSubmissions = new JSONArray();

        jsonSubmissions.put(0, createNameJson(freeCroissants));
        jsonSubmissions.put(1, createNameJson(unicornDiscount));
        jsonSubmissions.put(2, createNameJson(freeClubEntrance));

        return jsonSubmissions;
    }

    public Submission fetchSubmission(String name){

        Submission submission;
        switch (name){
            case "Free Croissants": submission = freeCroissants;
                                    break;
            case "Unicorn Discount": submission = unicornDiscount;
                                     break;
            case "Free Entrance Tonight": submission = freeClubEntrance;
                                          break;
            default: return null;
        }

        return submission;
    }

    private static JSONObject createNameJson(Submission submission) throws JSONException {

        String title = submission.getName();
        JSONObject submissionJson = new JSONObject();

        submissionJson.put("name", title);

        return submissionJson;
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

}
