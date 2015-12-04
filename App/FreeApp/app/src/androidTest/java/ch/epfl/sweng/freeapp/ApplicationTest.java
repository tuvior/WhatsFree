package ch.epfl.sweng.freeapp;

import android.app.Application;
import android.content.Intent;
import android.test.ApplicationTestCase;

import org.junit.Test;

import ch.epfl.sweng.freeapp.mainScreen.DisplaySubmissionActivity;
import ch.epfl.sweng.freeapp.mainScreen.MainScreenActivity;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


}