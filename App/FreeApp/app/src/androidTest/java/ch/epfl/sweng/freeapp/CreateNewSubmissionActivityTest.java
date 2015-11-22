package ch.epfl.sweng.freeapp;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;

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
public class CreateNewSubmissionActivityTest extends ActivityInstrumentationTestCase2<CreateNewSubmissionActivity> {

    // In order to insert an image in the beginning
    Bitmap icon = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher);

    //Calendar calendar = Calendar.getInstance();


    DefaultCommunicationLayer communicationLayer = new FakeCommunicationLayer();

    public CreateNewSubmissionActivityTest(Class<CreateNewSubmissionActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

   // public void test


    //  @Rule
    //public IntentsTestRule<CreateNewSubmissionActivity> mIntentsRule = new IntentsTestRule<>(
           // CreateNewSubmissionActivity.class);



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

        int year = 2015;
        int month = 4;
        int day = 5;

        int startHoursOfDay = 16;
        int startMinute = 10;

        int endHoursOfDay = 17;
        int endMinute = 10;
   //   mActivityRule.getActivity();


        getActivity();
        onView(withId(R.id.NameOfEvent)).perform(typeText("Food fun and amazing"));
        onView(withText("CREATE")).perform(scrollTo());

        onView(withId(R.id.Description)).perform(typeText("Some good croissant food"));
        onView(withId(R.id.Location)).perform(typeText("EPFl eculblens"));;

        onView(withId(R.id.setDateButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(setDate(year, month, day));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.startTime)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))). perform(setTime(startHoursOfDay, startMinute));
        onView(withText(("OK"))).perform(click());


        onView(withId(R.id.endButton)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))). perform(setTime(endHoursOfDay, endMinute));
        onView(withText(("Ok"))).perform(click());

        //How to stub out a camera ?


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
