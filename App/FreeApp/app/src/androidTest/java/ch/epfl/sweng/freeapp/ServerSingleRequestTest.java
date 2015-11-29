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

public class ServerSingleRequestTest {
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
    public void serverRespondsWithFailureIfNoCookie() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve"));
        assertEquals("cookie", getReasonFromJson(serverResponse, "retrieve"));
    }

    @Test
    public void serverRespondsWithFailureIfNoFlagParameter() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie=cookie", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve"));
        assertEquals("no flag", getStatusFromJson(serverResponse, "retrieve"));
    }

    @Test
    public void serverRespondsWithFailureIfCookieNotInDatabase() throws CommunicationLayerException, JSONException {
        //Make sure cookie not in db
        JSONObject deleteSession = establishConnectionAndReturnJsonResponse("/delete/session?cookie=cookie", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie=cookie&flag=1", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve"));
        assertEquals("session", getReasonFromJson(serverResponse, "retrieve"));
    }

    @Test
    public void serverRespondsWithFailureIfNoNameParameter() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=singlerequesttest", "GET");
        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "single request"));
        assertEquals("name", getReasonFromJson(serverResponse, "single request"));

        //Delete user and session
        JSONObject deleteUserAgain = establishConnectionAndReturnJsonResponse("/delete/user?name=singlerequesttest", "GET");
        JSONObject deleteSession = establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }

    @Test
    public void serverRespondsWithFailureIfNoCorrespondingSubmission() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=singlerequesttest", "GET");
        JSONObject deleteSubmission = establishConnectionAndReturnJsonResponse("/delete/submission?name=testDoesn'tPass", "GET");

        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&name=testDoesn'tPass", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "single request"));
        assertEquals("no corresponding submission", getReasonFromJson(serverResponse, "single request"));

        //Delete user and session
        JSONObject deleteUserAgain = establishConnectionAndReturnJsonResponse("/delete/user?name=singlerequesttest", "GET");
        JSONObject deleteSession = establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }


    @Test
    public void serverResponsWithCorrespondingSubmission() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=singlerequesttest", "GET");
        JSONObject deleteSubmission = establishConnectionAndReturnJsonResponse("/delete/submission?name=testname", "GET");

        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);
        JSONObject createSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie="+cookie+"&name=testname&category=testcategory&location=testlocation&image=testimage", "POST");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&name=testname", "GET");


        assertEquals("testname", serverResponse.getString("name"));
        assertEquals("testcategory", serverResponse.getString("category"));
        assertEquals("testlocation", serverResponse.getString("location"));
        assertEquals("testimage", serverResponse.getString("image"));

        //Delete user, session and submission
        JSONObject deleteUserAgain = establishConnectionAndReturnJsonResponse("/delete/user?name=singlerequesttest", "GET");
        JSONObject deleteSession = establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        JSONObject deleteSubmissionAgain = establishConnectionAndReturnJsonResponse("/delete/submission?name=testname", "GET");
    }



}
