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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testCanGreetUsers() {
        getActivity();
        onView(withId(R.id.mainName)).perform(typeText("from my unit test"));
        onView(withId(R.id.mainGoButton)).perform(click());
        //onView(withId(R.id.greetingMessage)).check(matches(withText("Hello from my unit test!")));
    }

    /**
     * Makes sure the What's new tab is displayed by default
     */
    @Test
    public void testDefaultTab(){
        getActivity();

    }

    @Test
    public void testClickCategories(){

    }

    @Test
    public void testClickAroundYou(){

    }

    //Other tests:
    //search button
    //map button

}
