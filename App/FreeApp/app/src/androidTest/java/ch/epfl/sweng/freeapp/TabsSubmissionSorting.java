package ch.epfl.sweng.freeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;

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
public class TabsSubmissionSorting {

    @Test
    public void testWhatsNewOrdering() throws JSONException {
        JSONArray submissionShortcutsJson = FakeCommunicationLayer.sendSubmissionsRequest();
        ArrayList<SubmissionShortcut> submissionShortcuts = FakeCommunicationLayer.jsonArrayToArrayList(submissionShortcutsJson);

        WhatsNewFragment whatsNewFragment = new WhatsNewFragment();
        ArrayList<SubmissionShortcut> sortedSubmissionShortcuts = whatsNewFragment.sortSubmissions(submissionShortcuts);

        //TODO assert submissions are in the good order
    }

    @Test
    public void testAroundYouOrdering() throws JSONException {
        JSONArray submissionShortcutsJson = FakeCommunicationLayer.sendSubmissionsRequest();
        ArrayList<SubmissionShortcut> submissionShortcuts = FakeCommunicationLayer.jsonArrayToArrayList(submissionShortcutsJson);

        AroundYouFragment aroundYouFragment = new AroundYouFragment();
        ArrayList<SubmissionShortcut> sortedSubmissionShortcuts = aroundYouFragment.sortSubmissions(submissionShortcuts);

        //TODO assert submissions are in the good order
    }

    @Test
    public void testCategoryOrdering() throws JSONException {
        ArrayList<SubmissionShortcut> submissionShortcuts = FakeCommunicationLayer.sendCategoryRequest(SubmissionCategory.FOOD);

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        ArrayList<SubmissionShortcut> sortedSubmissionShortcuts = categoriesFragment.sortSubmissions(submissionShortcuts);

        //TODO assert submissions are in the good order
    }
}
