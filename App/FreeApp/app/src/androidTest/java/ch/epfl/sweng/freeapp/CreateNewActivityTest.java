package ch.epfl.sweng.freeapp;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by francisdamachi on 06/11/15.
 */
public class CreateNewActivityTest {



    @Test
    public void testUnableToPickPastDate(){
        onView(withId(R.id.setDateButton)).perform(click());
        onView()

    }

    //User should not be able to create a new submission if a Mandatory field isn't done
    @Test
    public void testMandatoryFieldsNotCompleted(){

        onView(withId(R.id.NameOfEvent)).perform(typeText("Free Food"));
        onView(withId(R.id.Description)).perform(typeText("I am describing the event"));
        onView(withId(R.id.Location)).perform(typeText("Ecublens EPFl"));





    }

    @Test
    public void testCanCreateNewSubmission(){



    }

    //If the event happens to happen on the same day, the user should only be able to pick times
    //equal or greater than the current time
    @Test
    public void testCannotPickTimeLessThanCurrentTime(){



    }

    //if the user sets the time of the event to be at midnight, the date should be increased by a day automatically
    @Test
    public void testMidnightEventDateChange(){


    }



    @Test
    public void testEndingTimeShouldBeAlwaysLargerThanStartingTime(){




    }











}
