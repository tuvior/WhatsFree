package ch.epfl.sweng.freeapp;

/**
 * Created by lois on 11/29/15.
 */

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

import static junit.framework.Assert.assertTrue;

/**
 *
 * TODO: Remove this note when server is correctly implemented
 * These tests currently fail because:
 * .testGetNewSubmissions: sendSubmissionsRequest doesn't return the latest submissions. Some of the
 * returned submissions have some of their fields set to null (but they will eventually be removed).
 * .testGetAroundYouSubmissions: sendAroundYouRequest isn't functional yet
 * Once these issues have been taken care of, the tests should pass.
 *
 *
 *
 * Tests whether we can interact with the real server.
 **/
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CommunicationEndToEndTest {

    /**
    @Test
    public void testGetNewSubmissions() throws CommunicationLayerException {
        DefaultNetworkProvider defaultNetworkProvider = new DefaultNetworkProvider();
        CommunicationLayer communicationLayer = new CommunicationLayer(defaultNetworkProvider);
        ArrayList<Submission> submissions = communicationLayer.sendSubmissionsRequest();

        assertTrue("There must be some submissions", submissions.size() > 0);

        submissionsAssertions(submissions);
    }

    @Test
    public void testGetAroundYouSubmissions() throws CommunicationLayerException, JSONException {
        DefaultNetworkProvider defaultNetworkProvider = new DefaultNetworkProvider();
        CommunicationLayer communicationLayer = new CommunicationLayer(defaultNetworkProvider);

        //Lat/Long for EPFL. Some submissions have been created in for this area
        LatLng latLng = new LatLng(46.5221166, 6.5661442);
        ArrayList<Submission> submissions = communicationLayer.sendAroundYouRequest(latLng);

        submissionsAssertions(submissions);
    }
    **/

    private void submissionsAssertions(ArrayList<Submission> submissions){
        for(Submission submission: submissions){
            assertTrue("Unexpected category", SubmissionCategory.contains(submission.getCategory()));

            //The following fields are required to be at least 4 character long at creation
            assertTrue("Unexpected name", submission.getName().length() >= 4);
            assertTrue("Unexpected description", submission.getDescription().length() >= 4);
            assertTrue("Unexpected keywords", submission.getKeywords().length() >= 4);
            assertTrue("Unexpected name", submission.getLocation().length() >= 4);
        }
    }
}
