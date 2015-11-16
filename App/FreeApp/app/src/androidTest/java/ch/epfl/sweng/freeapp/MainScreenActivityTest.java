package ch.epfl.sweng.freeapp;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import ch.epfl.sweng.freeapp.mainScreen.MainScreenActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MainScreenActivityTest extends ActivityInstrumentationTestCase2<MainScreenActivity> {

    public MainScreenActivityTest() {
        super(MainScreenActivity.class);
    }

    /**
     * Makes sure the What's new tab is displayed by default
     */
    @Test
    public void testDefaultTab(){

    }

    @Test
    public void testClickCategories(){

    }

    @Test
    public void testClickAroundYou(){

    }

}
