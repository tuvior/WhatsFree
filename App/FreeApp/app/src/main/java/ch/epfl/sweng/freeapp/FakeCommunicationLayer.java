package ch.epfl.sweng.freeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This communication layer is independent of the server and allows sending the app
 * some predefined responses.
 *
 * Created by lois on 11/13/15.
 */
public class FakeCommunicationLayer{

    private Submission freeCroissants;
    private Submission unicornDiscount;
    private Submission freeClubEntrance;

    public FakeCommunicationLayer(){
        freeCroissants = new Submission("Free Croissants", "There's a huge croissant giveaway at Flon!", SubmissionCategory.FOOD);
        unicornDiscount = new Submission("Unicorn Discount", "Get one of our wonderful white unicorns!", SubmissionCategory.MISCELLANOUS);
        freeClubEntrance = new Submission("Free Entrance Tonight", "Come get wasted for free tonight!", SubmissionCategory.NIGHTLIFE);
    }

    public ResponseStatus sendAddSubmissionRequest(Submission submission) {
        //TODO
        return null;
    }

    public JSONArray sendWhatIsNewRequest() throws JSONException {

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

    private JSONObject createNameJson(Submission submission) throws JSONException {

        String title = submission.getName();
        JSONObject submissionJson = new JSONObject();

        submissionJson.put("name", title);

        return submissionJson;
    }

}
