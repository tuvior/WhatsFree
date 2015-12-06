package ch.epfl.sweng.freeapp;

import org.junit.Test;

import ch.epfl.sweng.freeapp.loginAndRegistration.RegistrationInfo;

import static junit.framework.Assert.assertTrue;

/**
 * Created by francisdamachi on 05/12/15.
 */
public class RegistrationInfoTest {


    @Test
    public void testCreateRegistration() {


        String name = "francis";
        String password = "PasswordPassword";
        String email = "francis.damachi@epfl.ch";

        RegistrationInfo registrationInfo = new RegistrationInfo("francis", "PasswordPassword", "francis.damachi@epfl.ch");


        boolean equalName = name.equals(registrationInfo.getUsername());
        boolean equalPassword = password.equals(registrationInfo.getPassword());
        boolean equalEmail = email.equals(registrationInfo.getEmail());


        assertTrue(equalName);
        assertTrue(equalPassword);
        assertTrue(equalEmail);


    }

}