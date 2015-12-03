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

import static org.junit.Assert.assertEquals;

public class ServerSearchTest {private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
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

    private String getStatusFromJson(JSONObject serverResponse, String option) throws JSONException {
        return serverResponse.getJSONObject(option).getString("status");
    }


    private String getReasonFromJson(JSONObject serverResponse, String option) throws JSONException {
        return serverResponse.getJSONObject(option).getString("reason");
    }


    private String getCookieFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("login").getString("cookie");
    }

    @Test
    public void serverRespondsWithFailureIfNoNameParameter() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchtest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchtest&password=password&email=searchtest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchtest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=5", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "search"));
        assertEquals("name", getReasonFromJson(serverResponse, "search"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=searchtest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");

    }

    @Test
    public void serverRetrieveSubmission() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchtest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchtestname", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchtest&password=password&email=searchtest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchtest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject submission = establishConnectionAndReturnJsonResponse("/submission?cookie="+cookie+"&name=searchtestname&category=category&location=location&image=image", "POST");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=5&name=searchtestname", "GET");
        assertEquals("searchtestname", serverResponse.getString("name"));
        assertEquals("image", serverResponse.getString("image"));

        // Search must only return name and image, other fields must be "", we check only one because we've already checked on the web
        assertEquals("", serverResponse.getString("category"));


        establishConnectionAndReturnJsonResponse("/delete/user?name=searchtest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchtestname", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }

}
