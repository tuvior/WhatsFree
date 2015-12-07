package ch.epfl.sweng.freeapp;

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

public class ServerAddSubmissionTest {
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
        return serverResponse.getJSONObject("submission").getString("status");
    }

    private String getReasonFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("submission").getString("reason");
    }

    private String getCookieFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("login").getString("cookie");
    }

    @Test
    public void serverRespondsWithFailureIfMissingCookie() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("cookie", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfMissingName() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission?cookie=cookie", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("name", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfMissingCategory() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission?cookie=cookie&name=name", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("category", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfMissingLocation() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission?cookie=cookie&name=name&category=category", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("location", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfMissingImage() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission?cookie=cookie&name=name&category=category&location=location", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("image", getReasonFromJson(serverResponse));
    }


    @Test
    public void serverRespondsWithFailureIfCookieNotInDataBase() throws CommunicationLayerException, JSONException {
        //Make sure cookie not in db, since we can directly create entry in db using the web
        JSONObject deleteUserIfInDB = establishConnectionAndReturnJsonResponse("/delete/session?cookie=cookie", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission?cookie=cookie&name=name&category=category&location=location&image=image", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("session", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithOk() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=submissiontest", "GET");
        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=submissiontest&password=password&email=submissiontest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=submissiontest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=name&category=category&location=location&image=image", "POST");
        assertEquals("ok", getStatusFromJson(serverResponse));

        //Delete user, session and submission so that it is no more in db
        JSONObject deleteUserAgain = establishConnectionAndReturnJsonResponse("/delete/user?name=submissiontest", "GET");
        JSONObject deleteSession = establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");
        JSONObject deleteSubmission = establishConnectionAndReturnJsonResponse("/delete/submission?name=name", "GET");
    }
}
