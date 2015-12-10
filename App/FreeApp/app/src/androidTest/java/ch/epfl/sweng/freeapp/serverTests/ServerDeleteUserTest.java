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

public class ServerDeleteUserTest {
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
    public void serverRespondsWithFailureIfNoNameParamater() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/user?", "GET");

        assertEquals("failure", getStatusFromJson(serverResponse, "delete user"));
        assertEquals("name", getReasonFromJson(serverResponse, "delete user"));
    }

    @Test
    public void serverRespondsWithFailureIfNoSuchUser() throws CommunicationLayerException, JSONException {
        //First time to make sure no such user in database, second to assert the failure message
        establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");


        assertEquals("failure", getStatusFromJson(serverResponse, "delete user"));
        assertEquals("no such user", getReasonFromJson(serverResponse, "delete user"));
    }

    @Test
    public void serverRespondsWithOkIfSuccess() throws CommunicationLayerException, JSONException, InterruptedException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=deleteTest&password=password&email=deleteTestEmail", "GET");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/delete/user?name=deleteTest", "GET");
        assertEquals("ok", getStatusFromJson(serverResponse, "delete user"));

        JSONObject tryLogin = establishConnectionAndReturnJsonResponse("/login?user=deleteTest&password=password", "GET");
        assertEquals("failure", getStatusFromJson(tryLogin, "login"));
        assertEquals("user", getReasonFromJson(tryLogin, "login"));
    }


}
