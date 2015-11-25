package ch.epfl.sweng.freeapp.communication;

import org.json.JSONException;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;

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
    abstract ResponseStatus sendAddSubmissionRequest(Submission param) throws CommunicationLayerException;

    /**
     * Asks the server for all the submissions it has.
     * @return  All the submission shortcuts available (see SubmissionShortcut class)
     * @throws JSONException
     */
    abstract ArrayList<Submission> sendSubmissionsRequest() throws JSONException, CommunicationLayerException;

    /**
     * Asks the server to send a specific submission.
     * @param name The submission's name
     * @return The submission
     */
    abstract Submission fetchSubmission(String name) throws CommunicationLayerException;

    /**
     * Asks the server for all submissions shortcuts related to the given category (see
     * SubmissionShortcut class)
     * @param category The category for which submissions shortcuts should be retrieved
     * @return The submission shortcuts corresponding to the given category
     */
    abstract ArrayList<Submission> sendCategoryRequest(SubmissionCategory category) throws CommunicationLayerException;

}
