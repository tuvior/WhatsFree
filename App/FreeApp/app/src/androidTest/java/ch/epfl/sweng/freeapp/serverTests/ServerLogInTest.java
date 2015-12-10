package ch.epfl.sweng.freeapp.serverTests;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.communication.NetworkProvider;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

public class ServerLogInTest {
    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private NetworkProvider networkProvider = new DefaultNetworkProvider();

    private String fetchContent(HttpURLConnection conn) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            String result = out.toString();
            Log.d("HTTPFetchContent", "Fetched string of length "
                    + result.length());
            return result;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private JSONObject establishConnectionAndReturnJsonResponse(String urlContd, String requestMethod) throws CommunicationLayerException, JSONException {
        try {
            URL url = new URL(SERVER_URL + urlContd);
            HttpURLConnection conn = networkProvider.getConnection(url);
            conn.setRequestMethod(requestMethod);
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            if (response < HTTP_SUCCESS_START || response > HTTP_SUCCESS_END) {
                throw new CommunicationLayerException("Invalid HTTP response code");
            }
            String serverResponseString = fetchContent(conn);
            return new JSONObject(serverResponseString);
        } catch (IOException e) {
            throw new CommunicationLayerException();
        }
    }

    private String getStatusFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("login").getString("status");
    }

    private String getReasonFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("login").getString("reason");
    }

    private String getCookieFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("login").getString("cookie");
    }

    @Test
    public void serverRespondsWithInvalidWhenNoParameters() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/login", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithInvalidWhenNoPassword() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/login?user=user", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithInvalidWhenNoUserName() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/login?password=password", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfWrongUserName() throws CommunicationLayerException, JSONException {
        //Make sure newuser is not there
        establishConnectionAndReturnJsonResponse("/delete/user?name=newuser", "GET");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse(("/login?user=newuser&password=somepassword"), "GET");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("user", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfWrongPassword() throws CommunicationLayerException, JSONException {
        //Registration won't work if there is already an existing user (example if we have already run the test)
        //but there is no problem because the login will work and will find that the password is incorrect
        establishConnectionAndReturnJsonResponse("/register?user=user&password=password&email=abc@abc.com", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse(("/login?user=user&password=wrong"), "GET");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("password", getReasonFromJson(serverResponse));

        //Delete user so that it is no more in db
        establishConnectionAndReturnJsonResponse("/delete/user?name=user", "GET");
    }

    @Test
    public void serverRespondsWithOkAndCookieIfLogInIsOk() throws CommunicationLayerException, JSONException {
        //Registration won't work if there is already an existing user (example if we have already run the test)
        //but there is no problem because the login will work
        establishConnectionAndReturnJsonResponse("/register?user=a&password=b&email=c", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/login?user=a&password=b", "GET");
        String cookie = getCookieFromJson(serverResponse);
        assertEquals("ok", getStatusFromJson(serverResponse));
        assertNotSame("", cookie);
        assertEquals(64, cookie.length());

        //Delete user and session so that it is no more in db
        establishConnectionAndReturnJsonResponse("/delete/user?name=user", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }


}
