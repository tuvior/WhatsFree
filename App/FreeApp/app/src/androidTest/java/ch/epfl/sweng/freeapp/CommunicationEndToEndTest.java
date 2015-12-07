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
 * TODO: Remove this note when server is correctly implemented
 * These tests currently fail because:
 * .testGetNewSubmissions: sendSubmissionsRequest doesn't return the latest submissions. Some of the
 * returned submissions have some of their fields set to null (but they will eventually be removed).
 * .testGetAroundYouSubmissions: sendAroundYouRequest isn't functional yet
 * Once these issues have been taken care of, the tests should pass.
 * <p/>
 * <p/>
 * <p/>
 * Tests whether we can interact with the real server.
 **/
@RunWith(AndroidJUnit4.class)
@LargeTest
//@Ignore("TEMPORARY. -Solal")
public class CommunicationEndToEndTest {

    @Test
    public void testGetNewSubmissions() throws CommunicationLayerException {
        DefaultNetworkProvider defaultNetworkProvider = new DefaultNetworkProvider();
        CommunicationLayer communicationLayer = new CommunicationLayer(defaultNetworkProvider);
        ArrayList<Submission> submissions = communicationLayer.sendSubmissionsRequest();

        assertTrue("There must be some submissions", submissions.size() > 0);

        submissionsAssertions(submissions);
    }

    private void submissionsAssertions(ArrayList<Submission> submissions) {
        for (Submission submission : submissions) {

            //The following fields are required to be at least 4 character long at creation
            assertTrue("Unexpected name", submission.getName().length() >= 4);
            assertTrue("No image", submission.getImage().length() > 0);

        }
    }
}
