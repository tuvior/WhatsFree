package ch.epfl.sweng.freeapp;

import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import ch.epfl.sweng.freeapp.mainScreen.MainScreenActivity;
import ch.epfl.sweng.freeapp.mainScreen.WhatsNewFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class MainScreenActivityTest extends ActivityInstrumentationTestCase2<MainScreenActivity> {

    public MainScreenActivityTest() {
        super(MainScreenActivity.class);
    }

    /**
     * Tests that submissions are indeed displayed in the list of submissions
     */
    @Test
    public void testDisplayed(){
        
    }

    /**
     * Tests that an Intent is indeed launched when clicking on a submission in the What's New tab
     */
    @Test
    public void testIntentNewSubmission(){

    }

    @Test
    public void testClickCategories(){

    }

    @Test
    public void testClickAroundYou(){

    }

}
