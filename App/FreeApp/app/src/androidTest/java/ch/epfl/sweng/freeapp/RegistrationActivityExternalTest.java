package ch.epfl.sweng.freeapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.ResponseStatus;
import ch.epfl.sweng.freeapp.loginAndRegistration.RegistrationActivity;
import ch.epfl.sweng.freeapp.loginAndRegistration.RegistrationInfo;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by MbangaNdjock on 26.10.15.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationActivityExternalTest {

    private CommunicationLayer communicationLayer;

    @Rule
    public ActivityTestRule<RegistrationActivity> mActivityRule = new ActivityTestRule<>(
            RegistrationActivity.class);

    private RegistrationActivity activity;

    @Before
    public void setup(){
        activity = mActivityRule.getActivity();
        this.communicationLayer = Mockito.mock(CommunicationLayer.class);

    }

    private void configureResponse(ResponseStatus status) throws CommunicationLayerException {
        Mockito.doReturn(status).when(communicationLayer).sendRegistrationInfo(Mockito.any(RegistrationInfo.class));
    }

    @Test
    public void testPrecondition(){
        assertNotNull(activity);
        assertNotNull(communicationLayer);
    }

    /*
     * Must define a policy to know what's the server response when a username
     * already exists.
     */
    @Test
    public void testCannotRegisterIfUsernameAlreadyExist() throws CommunicationLayerException {
        configureResponse(ResponseStatus.USERNAME);
    }

    @Test
    public void testCannotRegisterIfEmailAlreadyAssigned(){

    }

    @Test
    public void testCannotRegisterIfEmailInvalid(){

    }


    @Test
    public void testCannotRegisterIfExistingUsernameAndEmailDoNotMatch(){

    }

    @Test
    public void testCannotRegisterIfResponseTimesOut(){

    }

    @Test
    public void testCanRegisterWhenAllInformationAreGood() throws CommunicationLayerException {
        configureResponse(ResponseStatus.OK);
    }




}
