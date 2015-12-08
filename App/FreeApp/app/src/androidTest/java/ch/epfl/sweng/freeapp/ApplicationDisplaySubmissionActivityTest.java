package ch.epfl.sweng.freeapp;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.freeapp.mainScreen.DisplaySubmissionActivity;
import ch.epfl.sweng.freeapp.mainScreen.MainScreenActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by lois on 12/3/15.
 */

@RunWith(AndroidJUnit4.class)
public class ApplicationDisplaySubmissionActivityTest {

    @Rule
    public ActivityTestRule<DisplaySubmissionActivity> rule =
            new ActivityTestRule<>(DisplaySubmissionActivity.class, true, false);
    //The following are know attributes for a specific submission named "example" that is stored in the server
    String submissionId = "ahFlfnN3ZW5nLXdpaW5vdGZpdHIXCxIKU3VibWlzc2lvbhiAgICA-f6TCgw";

    @Test
    public void testCorrectName() {
        launchExampleSubmissionActivity();

        onView(withId(R.id.submissionName)).check(matches(withText("example")));
    }

    @Test
    public void testCorrectDescription() {
        launchExampleSubmissionActivity();

        onView(withId(R.id.submissionDescription)).check(matches(withText("example")));
    }


    @Test
    public void testLikeIsClickable(){

        launchExampleSubmissionActivity();
        onView(withId(R.id.like)).check(matches(isClickable()));

    }
    @Test
    public void testDislikeIsClickable(){
        launchExampleSubmissionActivity();
        onView(withId(R.id.dislike)).check(matches(isClickable()));


    }

    //Todo
    @Test
    public void testWhenSubmissionAlreadyLikedButtonIsHighlighted(){

       // launchExampleSubmissionActivity();

    }
    //Todo
    @Test
    public void testWhenSubmissionAlreadyDislikedButtonHighlighted(){

        //launchExampleSubmissionActivity();


    }

    //Todo
    @Test
    public void testProblemWithServerToastDisplayedWhenBadInternet(){
        //launchExampleSubmissionActivity();



    }

    /**
     * Launches the DisplaySubmissionActivity with a predefined Intent
     */
    private void launchExampleSubmissionActivity() {
        Intent intent = new Intent();
        intent.putExtra(MainScreenActivity.SUBMISSION_MESSAGE, submissionId);
        rule.launchActivity(intent);
    }


}
