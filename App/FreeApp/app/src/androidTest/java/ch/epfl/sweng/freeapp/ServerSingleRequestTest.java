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
        assertEquals("failure", getStatusFromJson(serverResponse, "none"));
        assertEquals("cookie", getReasonFromJson(serverResponse, "none"));
    }

    @Test
    public void serverRespondsWithFailureIfCookieNotInDatabase() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie=cookie&flag=1", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "none"));
        assertEquals("session", getReasonFromJson(serverResponse, "none"));
    }


    @Test
    public void serverRespondsWithFailureIfNoParameters() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete?name=singlerequesttest", "GET");
        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie, "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve"));
    }

    @Test
    public void serverResponsWithFailureIfNoNameParameter() throws CommunicationLayerException, JSONException {JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete?name=singlerequesttest", "GET");
        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "single request"));
        assertEquals("name", getReasonFromJson(serverResponse, "single request"));
    }

    @Test
    public void serverResponsWithFailureIfNoCorrespondingSubmission() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete?name=singlerequesttest", "GET");
        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&name=testDoesn'tPass", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "single request"));
        assertEquals("no corresponding submission", getReasonFromJson(serverResponse, "single request"));
    }


    @Test
    public void serverResponsWithCorrespondingSubmission() throws CommunicationLayerException, JSONException {
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete?name=singlerequesttest", "GET");
        JSONObject createUser = establishConnectionAndReturnJsonResponse("/register?user=singlerequesttest&password=password&email=singlerequesttest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=singlerequesttest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);
        JSONObject createSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie="+cookie+"&name=testname&category=testcategory&location=testlocation&image=testimage", "POST");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&name=testname", "GET");


        assertEquals("testname", serverResponse.getString("name"));
        assertEquals("testcategory", serverResponse.getString("category"));
        assertEquals("testlocation", serverResponse.getString("location"));
        assertEquals("testimage", serverResponse.getString("image"));
    }



}
