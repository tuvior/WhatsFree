package ch.epfl.sweng.freeapp;

import org.junit.Test;

import ch.epfl.sweng.freeapp.loginAndRegistration.LogInInfo;

/**
 * Created by lois on 12/7/15.
 */
public class LogInInfoTest {

    private String username = "Test username";
    private String password = "Test password";

    @Test
    public void testConstructor(){
        LogInInfo logInInfo = new LogInInfo(username, password);

        assert (logInInfo.getUsername().equals(username));
        assert (logInInfo.getPassword().equals(password));
    }

}
