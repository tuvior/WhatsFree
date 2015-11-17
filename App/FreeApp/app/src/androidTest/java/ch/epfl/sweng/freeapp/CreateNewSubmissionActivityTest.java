package ch.epfl.sweng.freeapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by francisdamachi on 06/11/15.
 */
public class CreateNewSubmissionActivityTest {


    Calendar calendar = Calendar.getInstance();


    DefaultCommunicationLayer communicationLayer = new FakeCommunicationLayer();


    @Rule
    public IntentsTestRule<CreateNewSubmissionActivity> mIntentsRule = new IntentsTestRule<>(
            CreateNewSubmissionActivity.class);



    @Before
    public void stubCameraIntent() {
        //Instrumentation.ActivityResult result = createImageCaptureActivityResultStub();

        // Stub the Intent.
      //  intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
    }

    /*
    private Instrumentation.ActivityResult createImageCaptureActivityResultStub() {

        // Put the drawable in a bundle.
        Bundle bundle = new Bundle();

        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);
        bundle.putParcelable(CreateNewSubmissionActivity.KEY_IMAGE_DATA,icon);

        // Create the Intent that will include the bundle.
        Intent resultData = new Intent();
        resultData.putExtras(bundle);

        // Create the ActivityResult with the Intent.
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }

*/


 /*   @Test
    public void checkCamera(){



        mActivityRule.getActivity();
       Bitmap icon = BitmapFactory.decodeResource(
               InstrumentationRegistry.getTargetContext().getResources(),
               R.mipmap.ic_launcher);


        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        onView(withId(R.id.takePictureButton)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        intended(toPackage("com.android.camera2"));

        // ... additional test steps and validation ...
    }



*/


   // @Test
    public void successfullyCreateSubmissionToServer(){

   //     mActivityRule.getActivity();
        onView(withId(R.id.setDateButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(setDate(2015, 4, 5));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.NameOfEvent)).perform(typeText("Food fun and amazing"));
        onView(withText("CREATE")).perform(scrollTo());
        onView(withId(R.id.keywords)).perform(typeText("yummy food"));


    }



    public static ViewAction setDate(final int year, final int month, final int day){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(DatePicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the date in DatePicker";
            }

            @Override
            public void perform(UiController uiController, View view) {

                DatePicker datePicker = (DatePicker)view;
                datePicker.updateDate(year,month,day);


            }
        };
    }


    public static ViewAction setTime(final int hours, final int minutes ){

        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }

            @Override
            public String getDescription() {
                return "Set the TimePicker";
            }

            //Because setHours and setMinute requires API level 23 if a machine
            //if a machine is running a less recent API, this code will never be called
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void perform(UiController uiController, View view) {


                TimePicker picker = (TimePicker)view;
                picker.setHour(hours);
                picker.setMinute(minutes);


            }
        };

    }


    //@Test
    public void testUnableToPickPastDate(){


    }

    //User should not be able to create a new submission if a Mandatory field isn't done
    //@Test
    public void testMandatoryFieldsNotCompleted(){



    }

    //@Test
    public void testCanCreateNewSubmission(){



    }

    //If the event happens to happen on the same day, the user should only be able to pick times
    //equal or greater than the current time
    //@Test
    public void testCannotPickTimeLessThanCurrentTime(){



    }

    //if the user sets the time of the event to be at midnight, the date should be increased by a day automatically
   // @Test
    public void testMidnightEventDateChange(){


    }



   // @Test
    public void testStartingTimeShouldBeAlwaysLargerThanEndTime(){




    }











}
