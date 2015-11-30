package ch.epfl.sweng.freeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;

import ch.epfl.sweng.freeapp.communication.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.mainScreen.AroundYouFragment;
import ch.epfl.sweng.freeapp.mainScreen.CategoriesFragment;
import ch.epfl.sweng.freeapp.mainScreen.WhatsNewFragment;

/**
 * Ensures the tabs present the submissions in the good order.
 *
 * Submissions are ordered according to different criteria depending on the tab:
 * What's new: Time Added
 * Categories: category
 * Around You: distance to your location
 *
 * Created by lois on 11/17/15.
 */
public class TabsSubmissionSortingTest {

    /**

    @Test
    public void testWhatsNewOrdering() throws JSONException {
        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        ArrayList<Submission> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

        WhatsNewFragment whatsNewFragment = new WhatsNewFragment();
        ArrayList<Submission> sortedSubmissions = whatsNewFragment.sortSubmissions(submissions);

        //TODO assert submissions are in the good order
    }

    @Test
    public void testAroundYouOrdering() throws JSONException {
        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        ArrayList<Submission> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

        AroundYouFragment aroundYouFragment = new AroundYouFragment();
        ArrayList<Submission> sortedSubmissions = aroundYouFragment.sortSubmissions(submissions);

        //TODO assert submissions are in the good order
    }

    @Test
    public void testCategoryOrdering() throws JSONException {
        FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
        ArrayList<Submission> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        ArrayList<Submission> sortedSubmissions = categoriesFragment.sortSubmissions(submissions);

        //TODO assert submissions are in the good order
    }

    **/
}
