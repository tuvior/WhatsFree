package ch.epfl.sweng.freeapp.communication;

import org.json.JSONException;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.Submission;
import ch.epfl.sweng.freeapp.SubmissionCategory;
import ch.epfl.sweng.freeapp.mainScreen.Vote;

/**
 * Interface with which helps communicate with the server.
 */
public interface DefaultCommunicationLayer {

    ResponseStatus sendAddSubmissionRequest(Submission param) throws CommunicationLayerException;

    /**
     * Asks the server for all the submissions it has.
     *
     * @return All the submission shortcuts available (see SubmissionShortcut class)
     * @throws JSONException
     */
    ArrayList<Submission> sendSubmissionsRequest() throws JSONException, CommunicationLayerException;

    /**
     * Asks the server to send a specific submission.
     *
     * @param name The submission's id
     * @return The submission
     */
    Submission fetchSubmission(String name) throws CommunicationLayerException;


    /**
     * Asks the server for all submissions shortcuts related to the given category (see
     * SubmissionShortcut class)
     *
     * @param category The category for which submissions shortcuts should be retrieved
     * @return The submission shortcuts corresponding to the given category
     */
    ArrayList<Submission> sendCategoryRequest(SubmissionCategory category) throws CommunicationLayerException;

    /**
     * The communication layer ask the server to process the vote done by the user
     * The server will then respond with some type of response.
     * @param submission The submission
     * @param vote  The type of vote either like, dislike, or neutral
     * @return a Response Status
     * @throws CommunicationLayerException if problem with server.
     */
    ResponseStatus sendVote(Submission submission, Vote vote) throws CommunicationLayerException;
}
