package ch.epfl.sweng.freeapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by francisdamachi on 24/10/15.
 */
public class LogInActivityTest {


    private LogInInfo logInfo = new LogInInfo("username", "password");


    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);


    @Test
    public void testWhenLoginButtonPressedCorrectDataBeUsed(){


        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.logInButton)).perform(click());


    }


}
