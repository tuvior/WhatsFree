package ch.epfl.sweng.freeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import ch.epfl.sweng.freeapp.communication.FakeCommunicationLayer;
import ch.epfl.sweng.freeapp.communication.ProvideCommunicationLayer;
import ch.epfl.sweng.freeapp.mainScreen.CreateNewSubmissionActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by francisdamachi on 06/11/15.
 */
public class CreateNewSubmissionActivityTest extends ActivityInstrumentationTestCase2<CreateNewSubmissionActivity> {

    // In order to insert an image at the beginning
    private Bitmap bitmapIcon = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher);
    private TimeZone timeZone = TimeZone.getTimeZone("Europe/Zurich");
    //DefaultCommunicationLayer communicationLayer = new FakeCommunicationLayer();

    public  CreateNewSubmissionActivityTest(){

        super(CreateNewSubmissionActivity.class);
    }




    @Test
    public void testSuccessfulSubmissionCreationOnLine(){

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeZone(timeZone);

        int dayOfMonth  = calendar.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Calendar calendarEventHour  = Calendar.getInstance();
        calendarEventHour.add(Calendar.HOUR_OF_DAY, 1);


        int hour = calendarEventHour.get(Calendar.HOUR_OF_DAY);


        int startHoursOfDay = hour; int startMinute = 10;
        int endHoursOfDay = hour;   int endMinute = 15;



        ProvideImage provideImage = new ProvideImage();
        provideImage.setTypeOfImage(ProvideImage.ImageType.FROM_TEST);
        ProvideImage.setImage(bitmapIcon);

        ProvideCommunicationLayer.setCommunicationLayer(new FakeCommunicationLayer());


        getActivity();

        onView(withId(R.id.NameOfEvent)).perform(typeText("Food fun and amazing"));
        onView(withId(R.id.Description)).perform(typeText("Some good croissant food"));
        onView(withId(R.id.Location)).perform(typeText("EPFl ecublens"));;



        onView(withText("SET DATE")).perform(scrollTo());
        onView(withId(R.id.setDateButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))). perform(setDate(year, monthOfYear, dayOfMonth));
        onView(withText(("OK"))).perform(click());




        onView(withText("STARTS")).perform(scrollTo());
        onView(withId(R.id.startButton)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))). perform(setTime(startHoursOfDay, startMinute));
        onView(withText(("OK"))).perform(click());


        onView(withText("ENDS")).perform(scrollTo());
        onView(withId(R.id.endButton)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))). perform(setTime(endHoursOfDay, endMinute));
        onView(withText(("OK"))).perform(click());


        onView(withText("TAKE PHOTO")).perform(scrollTo());
        onView(withId(R.id.takePictureButton)).perform(click());


        onView(withText("CREATE")).perform(scrollTo());
        onView(withId(R.id.keywords)).perform(scrollTo());
        onView(withId(R.id.keywords)).perform(typeText("YUMMY FOOD"));
        onView(withText("CREATE")).perform(scrollTo());
        onView(withId(R.id.createSubmissionButton)).perform(click());
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));

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

            @Override
            public void perform(UiController uiController, View view) {


                TimePicker picker = (TimePicker)view;
                picker.setCurrentHour(hours);
                picker.setCurrentMinute(minutes);


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
