package ch.epfl.sweng.freeapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by MbangaNdjock on 25.10.15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationActivityLocalTest {

    @Rule
    public ActivityTestRule<RegistrationActivity> mActivityRule = new ActivityTestRule<>(
            RegistrationActivity.class);

    private RegistrationActivity activity;

    private EditText usernameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText confirmPasswordView;



    @Before
    public void setup(){
        activity = mActivityRule.getActivity();

        usernameView = (EditText) activity.findViewById(R.id.username);
        emailView = (EditText) activity.findViewById(R.id.email);
        passwordView = (EditText) activity.findViewById(R.id.password);
        confirmPasswordView = (EditText) activity.findViewById(R.id.confirmPassword);
    }

    @Test
    public void testPrecondition(){
        assertNotNull(activity);
        assertNotNull(usernameView);
        assertNotNull(emailView);
        assertNotNull(passwordView);
        assertNotNull(confirmPasswordView);
    }

    @Test
    public void testShortUsernameNotAccepted(){
        RegistrationActivity activity = mActivityRule.getActivity();
        int maxLength = 5;
        assertFalse(activity.isUsernameValid(getRandomGeneratedString(maxLength)));
    }

    @Test
    public void testShortPasswordNotAccepted(){
        RegistrationActivity activity = mActivityRule.getActivity();
        int maxLength = 7;
        assertFalse(activity.isPasswordValid(getRandomGeneratedString(maxLength)));
    }

    /*
     * The empty field message is prioritized compared to the others
     */
    // UI
    @Test
    public void testEmptyFieldsHintMessageDisplayedWhenEmptyFieldsOnSignUpCLicked(){
        // TODO: 26.10.15 create empty fields hint message

    }


    @Test
    public void testPasswordInvalidIfNoSpecialCharacter(){
        RegistrationActivity activity = mActivityRule.getActivity();
        String password = UUID.randomUUID().toString();
        String specRegex = "[^(a-z)^(A-Z)^(0-9)]";
        String replacement = "a";
        password = password.replaceAll(specRegex, replacement);

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testPasswordInvalidIfNoNumericalCharacter(){
        RegistrationActivity activity = mActivityRule.getActivity();
        String password = getRandomGeneratedString(10);
        String digitRegex = "[0-9]";
        String replacement = "a";
        password = password.replaceAll(digitRegex, replacement);

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testPasswordInvalidIfNoUppercaseLetter(){
        RegistrationActivity activity = mActivityRule.getActivity();
        String password = UUID.randomUUID().toString();
        String uppercaseRegex = "[A-Z]";
        String replacement = "a";
        password = password.replaceAll(uppercaseRegex, replacement);

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testPasswordInvalidIfWhiteSpaceCharacter(){
        RegistrationActivity activity = mActivityRule.getActivity();
        String password = UUID.randomUUID().toString();

        Random random = new Random();
        int replacIndex = random.nextInt(password.length());

        password = password.replace(password.charAt(replacIndex), ' ');

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testUsernameInvalidIfWhiteSpaceCharacter(){
        RegistrationActivity activity = mActivityRule.getActivity();
        String username = UUID.randomUUID().toString();

        username = username.concat("  ").concat(" Hej");

        assertFalse(activity.isUsernameValid(username));
    }

    @Test
    public void testEmailInvalidIfWhiteSpaceCharacter(){
        RegistrationActivity activity = mActivityRule.getActivity();
        String email = UUID.randomUUID().toString();

        Random random = new Random();
        int replacIndex = random.nextInt(email.length());

        email = email.replace(email.charAt(replacIndex), ' ');

        assertFalse(activity.isEmailValid(email));
    }


    @Test
    public void testEmailHintMessageInvisibleWhenCorrectlyReentered(){

    }

    @Test
    public void testUsernameHintMessageInvisibleWhenCorrectlyReentered(){

    }

    @Test
    public void testPasswordHintMessageInvisibleWhenCorrectlyReentered(){

    }

    private String getRandomGeneratedString(int maxLength){
        String gen = null;
        String uuid = UUID.randomUUID().toString();

        if(1 < maxLength && maxLength <= uuid.length()) {
            Random endRandom = new Random();
            int eMin = 2;
            int eMax = maxLength;

            //====================================
            Random startRandom = new Random();
            int sMin = 0;
            int sMax = uuid.length() - (maxLength + 1);
            int start = startRandom.nextInt((sMax - sMin) + 1);
            //====================================
            gen = uuid.substring(start, start + endRandom.nextInt(eMax - eMin) + eMin);

            return gen;
        }
        else if (maxLength == 1){
            int min = 33; int max = 126;
            Random random = new Random();
            return (new StringBuilder((char)(random.nextInt((max - min) + 1) + min))).toString();
        }
        else{
            return gen;
        }
    }

    private String getRandomValidEmailAddress(){
        Random addressLength = new Random();
        int min = 4;int max = 10;

        String email = getRandomGeneratedString(addressLength.nextInt((max - min) + 1) + min);

        String regex = "[^(a-z)^(A-Z)^(0-9)]";
        String replacement = "b";
        email = email.replaceAll(regex, replacement).concat("@gmail.com");

        return email;
    }


}
