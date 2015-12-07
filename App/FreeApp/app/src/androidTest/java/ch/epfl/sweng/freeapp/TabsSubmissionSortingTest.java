package ch.epfl.sweng.freeapp;

/**
 * Ensures the tabs present the submissions in the good order.
 * <p/>
 * Submissions are ordered according to different criteria depending on the tab:
 * What's new: Time Added
 * Categories: category
 * Around You: distance to your location
 * <p/>
 * Created by lois on 11/17/15.
 */
public class TabsSubmissionSortingTest {

    /**

     @Test public void testWhatsNewOrdering() throws JSONException {
     FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
     ArrayList<Submission> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

     WhatsNewFragment whatsNewFragment = new WhatsNewFragment();
     ArrayList<Submission> sortedSubmissions = whatsNewFragment.sortSubmissions(submissions);

     //TODO assert submissions are in the good order
     }

     @Test public void testAroundYouOrdering() throws JSONException {
     FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
     ArrayList<Submission> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

     AroundYouFragment aroundYouFragment = new AroundYouFragment();
     ArrayList<Submission> sortedSubmissions = aroundYouFragment.sortSubmissions(submissions);

     //TODO assert submissions are in the good order
     }

     @Test public void testCategoryOrdering() throws JSONException {
     FakeCommunicationLayer fakeCommunicationLayer = new FakeCommunicationLayer();
     ArrayList<Submission> submissions = fakeCommunicationLayer.sendSubmissionsRequest();

     CategoriesFragment categoriesFragment = new CategoriesFragment();
     // ArrayList<Submission> sortedSubmissions = categoriesFragment.sortSubmissions(submissions);

     //TODO assert submissions are in the good order
     }

     **/
}
