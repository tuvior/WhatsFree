package ch.epfl.sweng.freeapp;

/**
 * Created by lois on 11/29/15.
 */

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

import static junit.framework.Assert.assertTrue;

/**
 *
 * Tests whether we can interact with the real server.
 *
 **/
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CommunicationEndToEndTest {

    @Test
    public void testGetNewSubmissions() throws CommunicationLayerException {
        DefaultNetworkProvider defaultNetworkProvider = new DefaultNetworkProvider();
        CommunicationLayer communicationLayer = new CommunicationLayer(defaultNetworkProvider);
        ArrayList<Submission> submissions = communicationLayer.sendSubmissionsRequest();

        assertTrue("There must be some submissions", submissions.size() > 0);

        submissionsAssertions(submissions);
    }

    @Test
    public void testGetCategoryRequest() throws CommunicationLayerException {
        DefaultNetworkProvider defaultNetworkProvider = new DefaultNetworkProvider();
        CommunicationLayer communicationLayer = new CommunicationLayer(defaultNetworkProvider);
        ArrayList<Submission> submissions = communicationLayer.sendCategoryRequest(SubmissionCategory.Food);

        submissionsAssertions(submissions);
    }

    private void submissionsAssertions(ArrayList<Submission> submissions) {
        for (Submission submission : submissions) {

            //The following fields are required to be at least 4 character long at creation
            assertTrue("Unexpected name", submission.getName().length() >= 4);
            assertTrue("No image", submission.getImage().length() > 0);
            assertTrue("Unexpected id", submission.getId().length() > 0);
            assertTrue("Unexpected rating", submission.getRating() >= 0);

        }
    }
}
