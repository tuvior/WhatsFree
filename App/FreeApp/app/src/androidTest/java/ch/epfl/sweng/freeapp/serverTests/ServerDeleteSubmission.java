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


public class ServerDeleteSubmission {
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

    private String getIdFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("submission").getString("id");
    }


    @Test
    public void serverRespondsWithFailureIfNoNameParamater() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/submission?", "GET");

        assertEquals("failure", getStatusFromJson(serverResponse, "delete submission"));
        assertEquals("name", getReasonFromJson(serverResponse, "delete submission"));
    }

    @Test
    public void serverRespondsWithFailureIfNoSuchSession() throws CommunicationLayerException, JSONException {
        //First time to make sure no such submission in database, second to assert the failure message
        establishConnectionAndReturnJsonResponse("/delete/submission?name=deleteSubmissionTest", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/submission?name=deleteSubmissionTest", "GET");


        assertEquals("failure", getStatusFromJson(serverResponse, "delete submission"));
        assertEquals("no such submission", getReasonFromJson(serverResponse, "delete submission"));
    }

    @Test
    public void serverRespondsWithOkIfSuccess() throws CommunicationLayerException, JSONException, InterruptedException {
        // Delete user and submission if they are already in database
        establishConnectionAndReturnJsonResponse("/delete/submission?name=deleteSubmissionTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=deleteTest&password=password&email=deleteTestEmail", "GET");
        JSONObject login = establishConnectionAndReturnJsonResponse("/login?user=deleteTest&password=password", "GET");
        String cookie = getCookieFromJson(login);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=deleteSubmissionTest&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/submission?name=deleteSubmissionTest", "GET");
        assertEquals("ok", getStatusFromJson(serverResponse, "delete submission"));

        JSONObject retrieveSubmission = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&id="+id, "GET");
        assertEquals("failure", getStatusFromJson(retrieveSubmission, "single request"));
        assertEquals("no corresponding submission", getReasonFromJson(retrieveSubmission, "single request"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }


}
