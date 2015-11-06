package ch.epfl.sweng.freeapp;

import android.provider.ContactsContract;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;
import android.widget.Toast;


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

    private final int LONG_FIELD = 300;



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
        int max = activity.USERNAME_MIN_LENGTH - 1;
        int min = 1;
        assertFalse(activity.isUsernameValid(getRandomGeneratedString(min, max)));
    }

    @Test
    public void testShortPasswordNotAccepted(){
        int maxLength = 7;
        int minLength = 1;
        assertFalse(activity.isPasswordValid(getRandomGeneratedString(minLength, maxLength)));
    }

    /*
     * Here "invalid" means that both passwords fields
     * are not identical.
     */
    @Test
    public void testInvalidPasswordFieldsResetOnSignUpCLicked(){
        String password = "";
        String confirmPassword = "";
        do {
            // There is a VERY low probability for both generated passwords to be equals
            password = getRandomGeneratedString(1, 7);
            confirmPassword = getRandomGeneratedString(1, 7);
        }while(password.equals(confirmPassword));

        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.confirmPassword)).perform(typeText(confirmPassword));
        onView(withId(R.id.button)).perform(ViewActions.scrollTo());
        onView(withId(R.id.button)).perform(click());
        assertTrue(passwordView.getText().toString().isEmpty());
        assertTrue(confirmPasswordView.getText().toString().isEmpty());
    }

    @Test
    public void testInvalidUsernameFieldResetOnSignUpClicked(){
        String username = getRandomGeneratedString(1, 5).concat(" .");

        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.button)).perform(ViewActions.scrollTo());
        onView(withId(R.id.button)).perform(click());
        assertEquals(usernameView.getText().toString().length(), 0);
    }

    @Test
    public void testInvalidEmailFieldResetOnSignUpClicked(){
        String validEmail = getRandomValidEmailAddress(15, activity.EMAIL_MAX_LENGTH); // valid email
        String regex = "[@]";
        String replacement = "xxx";
        String invalidEmail = validEmail.replaceAll(regex, replacement); // invalid email

        onView(withId(R.id.email)).perform(typeText(invalidEmail));
        onView(withId(R.id.button)).perform(ViewActions.scrollTo());
        onView(withId(R.id.button)).perform(click());
        assertEquals(emailView.getText().toString().length(), 0);

    }

    /*
    @Test
    public void testUsernameHintMessageDisplayedWhenInvalidUsernameEnteredOnSignUpCLicked(){
        String username = getRandomGeneratedString(1, 4); // invalid username
        String email = getRandomValidEmailAddress();
        String password = getRandomGeneratedString(8, 10);

        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.email)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.confirmPassword)).perform(typeText(password));
        onView(withId(R.id.button)).perform(click());

        assertTrue(activity.findViewById(R.id.hintMessageUsername).isShown());
    }

    @Test
    public void testEmailHintMessageDisplayedWhenInvalidEmailEnteredOnSignUpClicked(){
        String username = getRandomGeneratedString(1, 8);
        String email = getRandomValidEmailAddress().replaceAll("@", "a");  // invalid email
        String password = getRandomGeneratedString(1, 8);

        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.email)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.confirmPassword)).perform(typeText(password));
        onView(withId(R.id.button)).perform(click());

        assertTrue(activity.findViewById(R.id.hintMessageEmail).isShown());
    }

    @Test
    public void testPasswordHintMessageDisplayedWhenDifferentPasswordsEnteredOnSignUpClicked(){
        String username = getRandomGeneratedString(1, 7);
        String email = getRandomValidEmailAddress();
        String password;
        String confirmPassword;

        do{
            password = getRandomGeneratedString(1, 8);
            confirmPassword = getRandomGeneratedString(1, 8);
        }while(password.equals(confirmPassword));

        onView(withId(R.id.username)).perform(typeText(username));
        onView(withId(R.id.email)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.confirmPassword)).perform(typeText(confirmPassword));
        onView(withId(R.id.button)).perform(click());

        assertTrue(activity.findViewById(R.id.hintMessagePassword).isShown());

    }
    */

    /*
     * Even if the entered username is invalid, the username hint message
     * must not be displayed when there are still empty fields. Instead,
     * the empty fields hint message must be displayed.
     */

    /*
    @Test
    public void testUsernameHintMessageNotDisplayedWhenEmptyFieldsOnSignUpClicked(){
        String username = getRandomGeneratedString(1, 4); // invalid username
        String email;
        String password;
        String confirmPassword;
        int maxLength = 8;
        int minLength = 1;

        Random random = new Random();
        int i = random.nextInt(3);

        if(i == 0){
            email = getRandomValidEmailAddress();
            onView(withId(R.id.email)).perform(typeText(email));
        }
        else if(i == 1)
        {
            password = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.password)).perform(typeText(password));
        }
        else if(i == 2){
            confirmPassword = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.confirmPassword)).perform(typeText(confirmPassword));
        }

        onView(withId(R.id.username)).perform(typeText(username));
        assertFalse(activity.findViewById(R.id.hintMessageUsername).isShown());

    }
    */

    /*
     * Even if the entered email address is invalid, the email hint message
     * must not be displayed when there are still empty fields. Instead,
     * the empty fields hint message must be displayed.
    */
    /*
    @Test
    public void testEmailHintMessageNotDisplayedWhenEmptyFieldsOnSignUpClicked(){
        String email = getRandomValidEmailAddress().replaceAll("@", "a"); // invalid email address
        String username;
        String password;
        String confirmPassword;
        int maxLength = 8;
        int minLength = 1;

        Random random = new Random();
        int i = random.nextInt(3);

        if(i == 0){
            username = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.username)).perform(typeText(username));
        }
        else if(i == 1)
        {
            password = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.password)).perform(typeText(password));
        }
        else if(i == 2){
            confirmPassword = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.confirmPassword)).perform(typeText(confirmPassword));
        }

        onView(withId(R.id.email)).perform(typeText(email));
        assertFalse(activity.findViewById(R.id.hintMessageEmail).isShown());

    }
    */

    /*
     * Even if the entered password is invalid, the password hint message
     * must not be displayed when there are still empty fields. Instead,
     * the empty fields hint message must be displayed.
    */

    /*
    @Test
    public void testPasswordHintMessageNotDisplayedWhenEmptyFieldsOnSignUpClicked(){
        String password = getRandomGeneratedString(1, 6); // invalid password
        String email;
        String username;
        String confirmPassword;
        int maxLength = 8;
        int minLength = 1;

        Random random = new Random();
        int i = random.nextInt(3);

        if(i == 0){
            username = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.username)).perform(typeText(username));
        }
        else if(i == 1)
        {
            email = getRandomValidEmailAddress();
            onView(withId(R.id.email)).perform(typeText(email));
        }
        else if(i == 2){
            confirmPassword = getRandomGeneratedString(minLength, maxLength);
            onView(withId(R.id.confirmPassword)).perform(typeText(confirmPassword));
        }

        onView(withId(R.id.password)).perform(typeText(password));
        assertFalse(activity.findViewById(R.id.hintMessagePassword).isShown());

    }
    */



    /*
     * The empty field message is prioritized compared to the others
     */
    /*
    @Test
    public void testEmptyFieldsHintMessageDisplayedWhenEmptyFieldsOnSignUpCLicked(){
        // TODO: 26.10.15 create empty fields hint message

    }
    */

    @Test
    public void testPasswordInvalidIfNoSpecialCharacter(){
        String password = getRandomGeneratedString(activity.PASSWORD_MIN_LENGTH, activity.PASSWORD_MAX_LENGTH);
        String specRegex = "[^(a-z)^(A-Z)^(0-9)]";
        String replacement = "a";
        password = password.replaceAll(specRegex, replacement);

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testPasswordInvalidIfNoNumericalCharacter(){
        String password = getRandomGeneratedString(activity.PASSWORD_MIN_LENGTH, activity.PASSWORD_MAX_LENGTH);
        String digitRegex = "[0-9]";
        String replacement = "a";
        password = password.replaceAll(digitRegex, replacement);

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testPasswordInvalidIfNoUppercaseLetter(){
        String password = getRandomGeneratedString(activity.PASSWORD_MIN_LENGTH, activity.PASSWORD_MAX_LENGTH);
        String uppercaseRegex = "[A-Z]";
        String replacement = "a";
        password = password.replaceAll(uppercaseRegex, replacement);

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testPasswordInvalidIfWhiteSpaceCharacter(){
        String password = getRandomGeneratedString(activity.PASSWORD_MIN_LENGTH, activity.PASSWORD_MAX_LENGTH);

        Random random = new Random();
        int replacIndex = random.nextInt(password.length());

        password = password.replace(password.charAt(replacIndex), ' ');

        assertFalse(activity.isPasswordValid(password));
    }

    @Test
    public void testUsernameInvalidIfWhiteSpaceCharacter(){
        String username = getRandomGeneratedString(activity.USERNAME_MIN_LENGTH, activity.USERNAME_MAX_LENGTH);

        username = username.concat(" ").concat(" Hej");

        assertFalse(activity.isUsernameValid(username));
    }

    @Test
    public void testEmailInvalidIfWhiteSpaceCharacter(){
        String email = getRandomValidEmailAddress(15, activity.EMAIL_MAX_LENGTH);

        Random random = new Random();
        int replacIndex = random.nextInt(email.length());

        email = email.replace(email.charAt(replacIndex), ' ');

        assertFalse(activity.isEmailValid(email));
    }

    @Test
    public void testEmailInvalidIfNoAtCharacter(){
        String email = getRandomValidEmailAddress(15, activity.EMAIL_MAX_LENGTH);
        String regex = "[@]";
        String replacement = "a";

        email = email.replaceAll(regex, replacement);
        assertFalse(activity.isEmailValid(email));
    }

    @Test
    public void testEmailInvalidIfNoDotCharacter(){
        String email = getRandomValidEmailAddress(15, activity.EMAIL_MAX_LENGTH);
        String regex = "[\\.]";
        String replacement = "a";

        email = email.replaceAll(regex, replacement);
        assertFalse(activity.isEmailValid(email));
    }

    @Test
    public void testUsernameInvalidIfTooManyCharacter(){
        String username = getRandomGeneratedString(activity.USERNAME_MAX_LENGTH + 1, LONG_FIELD);
        assertFalse(activity.isUsernameValid(username));
    }

    @Test
    public void testEmailInvalidIfTooManyCharacter(){
        String email = getRandomValidEmailAddress(activity.EMAIL_MAX_LENGTH + 1, LONG_FIELD);
        assertFalse(activity.isEmailValid(email));
    }

    @Test
    public void testPasswordInvalidIfTooManyCharacter(){
        String password = getRandomGeneratedString(activity.PASSWORD_MAX_LENGTH + 1, LONG_FIELD);
        assertFalse(activity.isPasswordValid(password));
    }



    /*
    @Test
    public void testEmailHintMessageInvisibleWhenCorrectlyReentered(){

    }

    @Test
    public void testUsernameHintMessageInvisibleWhenCorrectlyReentered(){

    }

    @Test
    public void testPasswordHintMessageInvisibleWhenCorrectlyReentered(){

    }
    */

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

    /*
     * minLength and maxLength represent the username part of the email address
     * the username part is at most 300(LONG_FIELD) character
     */
    private String getRandomValidEmailAddress(int minLength, int maxLength){
        Random random = new Random();
        int max = EmailDomain.domains.size();

        if(minLength <= 0 || maxLength <= 0 || maxLength < minLength){
            throw new IllegalArgumentException();
        }

        String domain = "@".concat(EmailDomain.domains.get(random.nextInt(max)));
        String email = getRandomGeneratedString(minLength, maxLength);

        String regex = "[^(a-z)^(A-Z)^(0-9)]";
        String replacement = "b";
        email = email.replaceAll(regex, replacement).concat(domain);

        return email;
    }


}