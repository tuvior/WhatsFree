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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;

public class ServerAddSubmissionTest {
    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private NetworkProvider networkProvider = new DefaultNetworkProvider();
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;

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
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission/cookie=cookie&name=name", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("category", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfMissingLocation() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission/cookie=cookie&name=name&category=category", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("location", getReasonFromJson(serverResponse));
    }

    @Test
    public void serverRespondsWithFailureIfMissingImage() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission/cookie=cookie&name=name&category=category&location=location", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("image", getReasonFromJson(serverResponse));
    }


    @Test
    public void serverRespondsWithFailureIfCookieNotInDataBase() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/submission/cookie=cookie&name=name&category=category&location=location&image=image", "POST");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("session", getReasonFromJson(serverResponse));
    }
}
