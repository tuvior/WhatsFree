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

public class ServerDeleteSessionTest {
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
    public void serverRespondsWithFailureIfNoCookieParamater() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/session?", "GET");

        assertEquals("failure", getStatusFromJson(serverResponse, "delete session"));
        assertEquals("cookie", getReasonFromJson(serverResponse, "delete session"));
    }

    @Test
    public void serverRespondsWithFailureIfNoSuchSession() throws CommunicationLayerException, JSONException {
        //First time to make sure no such session in database, second to assert the failure message
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=deleteSessionTest", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/session?cookie=deleteSessionTest", "GET");


        assertEquals("failure", getStatusFromJson(serverResponse, "delete session"));
        assertEquals("no such session", getReasonFromJson(serverResponse, "delete session"));
    }

    @Test
    public void serverRespondsWithOkIfSuccess() throws CommunicationLayerException, JSONException, InterruptedException {
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=deleteSessionTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=deleteTest&password=password&email=deleteTestEmail", "GET");
        JSONObject login = establishConnectionAndReturnJsonResponse("/login?user=deleteTest&password=password", "GET");
        String cookie = getCookieFromJson(login);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        assertEquals("ok", getStatusFromJson(serverResponse, "delete session"));

        JSONObject tryToAddSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie="+cookie+"&name=name&category=category&location=location&image=image", "POST");
        assertEquals("failure", getStatusFromJson(tryToAddSubmission, "submission"));
        assertEquals("session", getReasonFromJson(tryToAddSubmission, "submission"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");
    }


}
