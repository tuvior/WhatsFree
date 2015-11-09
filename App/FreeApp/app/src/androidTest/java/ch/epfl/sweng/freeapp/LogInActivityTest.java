package ch.epfl.sweng.freeapp;

import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

/**
 * Created by francisdamachi on 24/10/15.
 */
public class LogInActivityTest {


    private LogInInfo logInfo = new LogInInfo("username", "password");
    private CommunicationLayer communicationLayer;
    private NetworkProvider networkProvider;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);


    @Before
    public void setUp(){

       networkProvider = Mockito.mock(NetworkProvider.class);
       communicationLayer = new CommunicationLayer(networkProvider);

    }


    private void configureResponseFromCommuncationLayer(ResponseStatus status) throws CommunicationLayerException {

        assert(status == ResponseStatus.OK || status ==ResponseStatus.USERNAME|| status== ResponseStatus.PASSWORD);
        Mockito.doReturn(status).when(communicationLayer).sendLogInInfo(logInfo);


    }


    @Test
    public void testWhenLoginButtonPressedCorrectDataShouldBeUsed(){
        LoginActivity loginActivity = mActivityRule.getActivity();

        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.logInButton)).perform(click());

        assertEquals(loginActivity.getLogin().getUsername(), logInfo.getUsername());
        assertEquals(loginActivity.getLogin().getPassword(), logInfo.getPassword());

    }
    //successfully goes to new Screen
    @Test
    public void testGoToNewScreen(){




    }
  //I expect this test to display a message
    @Test
    public void testWhenCorrectUserNameFalsePassword(){



    }


    @Test
    public void testErrorMessageWhenPassWordFieldEmpty(){

    }

    @Test
    public void testUserLengthPasswordMethod(){
        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText)loginActivity.findViewById(R.id.username);
        EditText password = (EditText)loginActivity.findViewById(R.id.password);

        int minUser = 6;
        int midLength = 9;
        int minPassword = 8;
        int max = 30;

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(minUser - 1)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(minPassword)));
        assertEquals(false, loginActivity.checkLengthOfUserNameAndPassword(username, password));

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(max + 1)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(minPassword)));
        assertEquals(false, loginActivity.checkLengthOfUserNameAndPassword(username, password));

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(minUser)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(minPassword - 1)));
        assertEquals(false, loginActivity.checkLengthOfUserNameAndPassword(username, password));

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(minUser)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(max + 1)));
        assertEquals(false, loginActivity.checkLengthOfUserNameAndPassword(username, password));

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(minUser)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(max)));
        assertEquals(true,loginActivity.checkLengthOfUserNameAndPassword(username, password));


        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(midLength)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(midLength)));
        assertEquals(true,loginActivity.checkLengthOfUserNameAndPassword(username, password));


    }


    private String generateStringOfLengthN(int n){

        StringBuilder b = new StringBuilder();
        char c = 'c';

        for (int i = 0; i< n ;i++){
            b.append(c);
        }
        return  b.toString();
    }


}
