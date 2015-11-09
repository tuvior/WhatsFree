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


    private LogInInfo logInfo;
    private CommunicationLayer communicationLayer;
    private NetworkProvider networkProvider;
    private static final int MIN_USER = 6;

    private static final int  MID_LENGTH = 9;
    private static final int  MIN_PASSWORD= 8;
    private static final int MAX_LENGTH = 30;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);


    @Before
    public void setUp(){

        this.logInfo = new LogInInfo("username","password");
        networkProvider = Mockito.mock(NetworkProvider.class);
        communicationLayer = Mockito.mock(CommunicationLayer.class);

    }


    private void configureResponseFromCommunicationLayer(ResponseStatus status) throws CommunicationLayerException {

        // assert(status == ResponseStatus.OK || status ==ResponseStatus.USERNAME|| status== ResponseStatus.PASSWORD);
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
    public void testPasswordShouldNotBeLeftBlank(){

        LoginActivity loginActivity = mActivityRule.getActivity();

        EditText password = (EditText)loginActivity.findViewById(R.id.password);
        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MIN_USER)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true,password.getError().toString().equals("Fill password"));

    }


    @Test
    public void testUserNameShouldNotBeLeftBlank(){
        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText)loginActivity.findViewById(R.id.username);
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MAX_LENGTH)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError().toString().equals("Fill username"));

    }

    @Test
    public void testUserLengthPasswordMethodCornerCase0() {
        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText) loginActivity.findViewById(R.id.username);
        EditText password = (EditText) loginActivity.findViewById(R.id.password);


        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MIN_USER - 1)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MIN_PASSWORD)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError().toString().length() > 0);
        assertEquals(true, password.getError() == null);

    }



    @Test
    public void  testUserLengthPasswordMethodCornerCase1 (){

        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText) loginActivity.findViewById(R.id.username);
        EditText password = (EditText) loginActivity.findViewById(R.id.password);

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MAX_LENGTH + 1)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MIN_PASSWORD)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError().toString().length() > 0);
        assertEquals(true, password.getError() == null);


    }

    @Test
    public void  testUserLengthPasswordMethodCornerCase2 (){

        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText) loginActivity.findViewById(R.id.username);
        EditText password = (EditText) loginActivity.findViewById(R.id.password);

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MIN_USER)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MIN_PASSWORD - 1)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError() == null);
        assertEquals(true, password.getError().toString().length() > 0);



    }

    @Test
    public void  testUserLengthPasswordMethodCornerCase3(){
        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText) loginActivity.findViewById(R.id.username);
        EditText password = (EditText) loginActivity.findViewById(R.id.password);
        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MIN_USER)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MIN_PASSWORD - 1)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError() == null);
        assertEquals(true, password.getError().toString().length() > 0);


    }

    @Test
    public void  testUserLengthPasswordMethodCornerCase4(){
        LoginActivity loginActivity = mActivityRule.getActivity();
        EditText username = (EditText) loginActivity.findViewById(R.id.username);
        EditText password = (EditText) loginActivity.findViewById(R.id.password);

        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MIN_USER)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MAX_LENGTH + 1)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError() == null);
        assertEquals(true, password.getError().toString().length() > 0);

    }


    @Test
    public void testCorrectUserPasswordLength() throws CommunicationLayerException {

        LoginActivity loginActivity = mActivityRule.getActivity();


        EditText username = (EditText)loginActivity.findViewById(R.id.username);
        EditText password = (EditText)loginActivity.findViewById(R.id.password);


        onView(withId(R.id.username)).perform(typeText(generateStringOfLengthN(MIN_USER)));
        onView(withId(R.id.password)).perform(typeText(generateStringOfLengthN(MAX_LENGTH)));
        onView(withId(R.id.logInButton)).perform(click());
        assertEquals(true, username.getError()  == null);
        assertEquals(true, password.getError()  == null);


    }



    private String generateStringOfLengthN(int n){

        StringBuilder b = new StringBuilder();
        char c = 'c';

        for (int i = 0; i< n ;i++){
            b.append(c);
        }
        return  b.toString();
    }



    @Test
    public void testPasswordAndUserCantContainSpace(){

        String userWithSpace = "My name is francis";
        String passwordWithSpace = "Password is good";

        LoginActivity loginActivity = mActivityRule.getActivity();


        EditText username = (EditText)loginActivity.findViewById(R.id.username);
        EditText password = (EditText)loginActivity.findViewById(R.id.password);

        onView(withId(R.id.username)).perform(typeText(userWithSpace));
        onView(withId(R.id.password)).perform(typeText(passwordWithSpace));
        onView(withId(R.id.logInButton)).perform(click());

        assertEquals(true, username.getError().toString().equals("No spaces allowed"));
        assertEquals(true, password.getError().toString().equals("No spaces allowed"));
















    }






}
