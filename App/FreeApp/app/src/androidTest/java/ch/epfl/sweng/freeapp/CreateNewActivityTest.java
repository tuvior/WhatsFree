package ch.epfl.sweng.freeapp;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by francisdamachi on 06/11/15.
 */
public class CreateNewActivityTest {


   // @Rule
    //public ActivityTestRule<CreateNewSubmissionActivity> rule = new ActivityTestRule<>(CreateNewSubmissionActivity.class);


    @Rule
    public IntentsTestRule<CreateNewSubmissionActivity> activityIntentsTestRule = new IntentsTestRule<>(CreateNewSubmissionActivity.class);

    @Test
    public void testSuccessfullyTakesPictureAndSavesIt(){

      //  onView(withId(R.id.takePictureButton)).check(matches(isEnabled()));


        /*

        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher);

        ///CreateNewSubmissionActivity createNewSubmissionActivity = rule.getActivity();

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        onView(withId(R.id.takePictureButton)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.android.camera2"));

*/
    }


/*
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





*/






}
