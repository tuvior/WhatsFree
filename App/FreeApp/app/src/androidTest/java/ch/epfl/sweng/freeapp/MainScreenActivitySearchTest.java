package ch.epfl.sweng.freeapp;

import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.SearchView;

import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import ch.epfl.sweng.freeapp.mainScreen.MainScreenActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by MbangaNdjock on 05.12.15.
 */
public class MainScreenActivitySearchTest extends ActivityInstrumentationTestCase2<MainScreenActivity> {

    private final int LONG_FIELD = 300;
    private final int LONG_SEARCH = 100;
    private final int searchViewButtonId = 2131689591;
    private final int searchViewTextId = 2131689683;



    public MainScreenActivitySearchTest(){
        super(MainScreenActivity.class);
    }

    private SearchView searchView;


    @Test
    public void testPrecondition(){
        getActivity();



        assertEquals(true, true);
        //assertNotNull(activity);
    }

    /*
    @Test
    public void testPostCondition(){
        //searchView = (SearchView) activity.findViewById(R.id.action_search);

        // MenuInflater inflater = activity.getMenuInflater();
        // inflater.inflate(R.menu.menu_main_screen_activity, null );

        //activity.getActionBar().

        //      (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();


        //assertNotNull(searchView);
    }



    @Test
    public void testSubmissionDisplayWhenSearchIsSuccessful(){

    }

    @Test
    public void testSubmissionNotDisplayedWhenSearchIsUnsuccessful(){

    }*/

    @Test
    public void testCannotHandleSearchWithMoreThan100Characters(){
        getActivity();
        String search = getRandomGeneratedString(LONG_SEARCH + 1, LONG_SEARCH + 100);

        //ActionMenuItemView itemView = (ActionMenuItemView) getActivity().findViewById(R.id.action_search);
        //SearchView searchView = (SearchView) itemView.findViewById(R.id.action_search);


        onView(withId(R.id.action_search)).perform(click());
        onView(withId(searchViewTextId)).perform(typeText(search));
       // assertTrue(activity.getVisibility());



        onView(withText("No submission exists with this id")).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

        //onView(withId(R.id.like)).check(matches(isNotDisplayed()));
    }


    /*
    // Implies cannot create submission with special characters
    @Test
    public void testCannotHandleSearchWithSpecialCharacters(){

    }

    @Test
    public void testSearchProcessStoppedIfServerResponseTakesTooLong(){

    }

    @Test
    public void testServerReturnsNullOnInvalidSearchQuery(){

    }

    /*
 * Generate randomly a string whose length is at most 300(LONG_FIELD) characters
 */
    private String getRandomGeneratedString(int minLength, int maxLength){    	String gen = null;
        Random random = new Random();
        String uuid = UUID.randomUUID().toString();

        if(minLength <= 0 || maxLength <= 0 || maxLength < minLength){
            throw new IllegalArgumentException();
        }


        while (uuid.length() < maxLength) {
            uuid = uuid.concat(UUID.randomUUID().toString());
        }

        if(LONG_FIELD < maxLength){
            maxLength = LONG_FIELD;
        }
        if(LONG_FIELD < minLength){
            minLength = LONG_FIELD;
        }

        if (1 < maxLength && maxLength <= uuid.length() &&
                1 <= minLength && minLength <= uuid.length()){

            int sMax = uuid.length() - maxLength;

            int start = random.nextInt(sMax + 1);
            int window = random.nextInt((maxLength - minLength) + 1);  // 0 <= window < (maxLength-minLength) + 1

            gen = uuid.substring(start, start + minLength + window);

        } else if (maxLength == 1 && minLength == maxLength) {
            int min = 33;
            int max = 126;
            gen = String.valueOf((char) (random.nextInt((max - min) + 1) + min));
        }

        return gen;
    }
}
