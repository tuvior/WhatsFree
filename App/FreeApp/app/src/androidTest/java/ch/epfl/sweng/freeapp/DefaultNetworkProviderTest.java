package ch.epfl.sweng.freeapp;

/**
 * Created by lois on 11/29/15.
 */

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

/** Tests the DefaultNetworkProvider */
@RunWith(AndroidJUnit4.class)
public class DefaultNetworkProviderTest {

    /**
     * Test that getConnection() calls url.openConnection() and does not tamper
     * with it.
     */
    @Test
    public void testOpenConnectionCalled() throws IOException {
        final HttpURLConnection expected = Mockito.mock(HttpURLConnection.class);

        URL url = new URL("http", "example.com", -1, "/", new URLStreamHandler() {

            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return expected;
            }

        });

        DefaultNetworkProvider dnp = new DefaultNetworkProvider();

        HttpURLConnection result = dnp.getConnection(url);
        assertSame("Wrong URL method called", expected, result);
        Mockito.verifyZeroInteractions(expected);
    }

    @Test
    public void testInvalidUrl(){

        try {
            DefaultNetworkProvider dnp = new DefaultNetworkProvider();
            dnp.getConnection(new URL("this is an invalid URL"));
            fail("Client should fail on invalid URL");
        } catch (IOException e) {
            //Successfully threw an IOException
        }

    }


}
