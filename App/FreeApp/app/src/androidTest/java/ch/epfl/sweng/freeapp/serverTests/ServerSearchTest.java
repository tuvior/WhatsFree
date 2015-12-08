package ch.epfl.sweng.freeapp.serverTests;


import android.util.Log;

import org.json.JSONArray;
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

    private JSONArray establishConnectionAndReturnJsonResponseAsArray(String urlContd, String requestMethod) throws CommunicationLayerException, JSONException {
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
            return new JSONArray(serverResponseString);
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

    private String getIdFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("submission").getString("id");
    }

    @Test
    public void serverRespondsWithFailureIfNoCookieParameter() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/search?", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "search"));
        assertEquals("cookie", getReasonFromJson(serverResponse, "search"));

    }

    @Test
    public void serverRespondsWithFailureIfWrongSession() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=cookie", "GET");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/search?cookie=cookie", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "search"));
        assertEquals("session", getReasonFromJson(serverResponse, "search"));

    }

    @Test
    public void serverRespondsWithFailureIfNoNameParameter() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchTestUser&password=password&email=searchTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchTestUser&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/search?cookie="+cookie, "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "search"));
        assertEquals("name", getReasonFromJson(serverResponse, "search"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");

    }

    @Test
    public void serverRespondsWithFailureIfNoCorrespondingSubmission() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchTestSubmission", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchTestUser&password=password&email=searchTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchTestUser&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/search?cookie="+cookie+"&name=searchTestSubmission", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "search"));
        assertEquals("no result", getReasonFromJson(serverResponse, "search"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");

    }

    @Test
    public void serverRetrieveSubmission() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchTestSubmission", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchTestUser&password=password&email=searchTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchTestUser&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=searchTestSubmission&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONArray serverResponse = establishConnectionAndReturnJsonResponseAsArray("/search?cookie=" + cookie + "&name=searchTestSubmission", "GET");
        assertEquals(1, serverResponse.length());

        JSONObject serverJson = new JSONObject(serverResponse.get(0).toString());
        assertEquals(id, serverJson.getString("id"));
        assertEquals("searchTestSubmission", serverJson.getString("name"));
        assertEquals("image", serverJson.getString("image"));
        assertEquals(0, serverJson.getInt("rating"));



        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchTestSubmission", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }


    @Test
    public void serverRetrieveSubmissionIfSearchNameInLowerCase() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchTestSubmission", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchTestUser&password=password&email=searchTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchTestUser&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=searchTestSubmission&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONArray serverResponse = establishConnectionAndReturnJsonResponseAsArray("/search?cookie=" + cookie + "&name=searchtestsubmission", "GET");
        assertEquals(1, serverResponse.length());

        JSONObject serverJson = new JSONObject(serverResponse.get(0).toString());
        assertEquals(id, serverJson.getString("id"));
        assertEquals("searchTestSubmission", serverJson.getString("name"));
        assertEquals("image", serverJson.getString("image"));
        assertEquals(0, serverJson.getInt("rating"));



        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchTestSubmission", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }

    @Test
    public void serverRetrieveArrayOfLengthForty() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=searchTestSubmission", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=searchTestUser&password=password&email=searchTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=searchTestUser&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);


        for(int i= 0; i < 20; i++) {
            establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=searchTestSubmission&category=category&location=location&image=image", "POST");

        }

        JSONArray serverResponse = establishConnectionAndReturnJsonResponseAsArray("/search?cookie=" + cookie + "&name=searchTestSubmission", "GET");
        assertEquals(20, serverResponse.length());

        establishConnectionAndReturnJsonResponse("/delete/user?name=searchTestUser", "GET");
        establishConnectionAndReturnJsonResponse("/delete/category?category=category", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }

}
