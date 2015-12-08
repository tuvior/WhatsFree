package ch.epfl.sweng.freeapp;


import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.communication.ResponseStatus;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by lois on 11/29/15.
 */
public class FakeCommunicationLayerTest {

    private FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();

    @Test
    public void testSendSubmissionsRequest() throws JSONException {
        ArrayList<Submission> fakeSubmissions = fakeCommunicationLayer.sendSubmissionsRequest();
        ArrayList<Submission> predefinedSubmissions = fakeCommunicationLayer.getFakeSubmissions();

        assertTrue(fakeSubmissions.size() == 5);

        for(Submission predefinedSubmission : predefinedSubmissions){
            assertTrue(fakeSubmissions.contains(predefinedSubmission));
        }
    }

    @Test
    public void testFetchSubmission(){
        Submission submissionId0 = fakeCommunicationLayer.fetchSubmission("id0");
        Submission submissionId2 = fakeCommunicationLayer.fetchSubmission("id2");
        Submission submissionId3 = fakeCommunicationLayer.fetchSubmission("id3");
        Submission submissionUndefinedId = fakeCommunicationLayer.fetchSubmission("Test id");

        assertTrue ( submissionId0.equals(fakeCommunicationLayer.getFreeCroissants()) );
        assertTrue ( submissionId2.equals(fakeCommunicationLayer.getUnicornDiscount()) );
        assertTrue ( submissionId3.equals(fakeCommunicationLayer.getFreeClubEntrance()) );
        assertTrue(submissionUndefinedId == null);
    }

    @Test
    public void testSendCategoryRequest(){
        ArrayList<Submission> foodSubmissions = fakeCommunicationLayer.sendCategoryRequest(SubmissionCategory.Food);
        assertTrue(foodSubmissions.contains(fakeCommunicationLayer.getFreeCroissants()));
        assertTrue(foodSubmissions.contains(fakeCommunicationLayer.getFreeDonuts()));

        ArrayList<Submission> miscellaneousSubmissions = fakeCommunicationLayer.sendCategoryRequest(SubmissionCategory.Miscellaneous);
        assertTrue(miscellaneousSubmissions.contains(fakeCommunicationLayer.getUnicornDiscount()));

        ArrayList<Submission> nightlifeSubmissions = fakeCommunicationLayer.sendCategoryRequest(SubmissionCategory.Nightlife);
        assertTrue(nightlifeSubmissions.contains(fakeCommunicationLayer.getFreeClubEntrance()));
    }

    @Test (expected = CommunicationLayerException.class)
    public void testSendVoteNullSubmission() throws CommunicationLayerException {

        fakeCommunicationLayer.sendVote(null, null);

    }

    @Test
    public void sendVoteTest() throws CommunicationLayerException {
        Submission submission = fakeCommunicationLayer.getFreeClubEntrance();

        ResponseStatus responseStatus = fakeCommunicationLayer.sendVote(submission, null);
        assertEquals(responseStatus, ResponseStatus.OK);
    }

    @Test
    public void sendAddSubmissionRequest() throws CommunicationLayerException {
        ResponseStatus responseStatus = fakeCommunicationLayer.sendAddSubmissionRequest(null);
        assertEquals(responseStatus, ResponseStatus.OK);
    }

}
