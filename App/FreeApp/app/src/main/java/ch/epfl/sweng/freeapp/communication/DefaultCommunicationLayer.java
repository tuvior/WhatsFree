package ch.epfl.sweng.freeapp.communication;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;
import ch.epfl.sweng.freeapp.mainScreen.VOTE;

/**
 * Created by lois on 11/22/15.
 */
public interface DefaultCommunicationLayer {

    /**
     * Tells the server to add a submission and gets back the response
     * @param param the submission to be added
     * @return the result of the request
     * @throws CommunicationLayerException
     */
    ResponseStatus sendAddSubmissionRequest(Submission param) throws CommunicationLayerException;

    /**
     * Asks the server for all the submissions it has.
     * @return  All the submission shortcuts available (see SubmissionShortcut class)
     * @throws JSONException
     */
    ArrayList<Submission> sendSubmissionsRequest() throws JSONException, CommunicationLayerException;

    /**
     * Asks the server to send a specific submission.
     * @param name The submission's id
     * @return The submission
     */
    Submission fetchSubmission(String  name) throws CommunicationLayerException;



    /**
     * Asks the server for all submissions shortcuts related to the given category (see
     * SubmissionShortcut class)
     * @param category The category for which submissions shortcuts should be retrieved
     * @return The submission shortcuts corresponding to the given category
     */
    ArrayList<Submission> sendCategoryRequest(SubmissionCategory category) throws CommunicationLayerException;

    /**
     * Asks the server for the submissions which are situated in a ... perimeter
     * @return  All the submission shortcuts available (see SubmissionShortcut class)
     * @throws JSONException
     */
    ArrayList<Submission> sendAroundYouRequest(LatLng userLocation) throws JSONException, CommunicationLayerException;

    //TODO : javadoc
    ResponseStatus sendVote(Submission submission, VOTE vote) throws CommunicationLayerException;
}
