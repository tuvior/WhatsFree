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

public class ServerVoteTest {
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

    private int getRatingFromJson(JSONObject serverResponse) throws  JSONException {
        return serverResponse.getInt("rating");
    }

    @Test
    public void serverRespondsWithFailureIfNoCookieParameter() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        assertEquals("cookie", getReasonFromJson(serverResponse, "vote"));
    }

    @Test
    public void serverRespondsWithFailureIfNoIdParameter() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie="+cookie, "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        assertEquals("no id", getReasonFromJson(serverResponse, "vote"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");
    }


    @Test
    public void serverRespondsWithFailureIfNoSession() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie=cookie&id="+id, "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        assertEquals("session", getReasonFromJson(serverResponse, "vote"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }

    @Test
    public void serverRespondsWithFailureIfNoSubmission() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie=" + cookie + "&id=" + id, "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        // Remember to fix the server part so that there is only one space
        assertEquals("no  submission", getReasonFromJson(serverResponse, "vote"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");

    }


    @Test
    public void serverRespondsWithFailureIfNoValueParameter() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie="+cookie+"&id="+id, "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        assertEquals("value", getReasonFromJson(serverResponse, "vote"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }


    @Test
    public void serverRespondsWithFailureIfValueLessThanMinus1() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie="+cookie+"&id="+id+"&value=-3", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        assertEquals("value", getReasonFromJson(serverResponse, "vote"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }

    @Test
    public void serverRespondsWithFailureIfValueGreaterThan1() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie="+cookie+"&id="+id+"&value=3", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "vote"));
        assertEquals("value", getReasonFromJson(serverResponse, "vote"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }


    @Test
    public void serverRespondsWithOkIfSuccessAndAddOneToRating() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie=" + cookie + "&id=" + id + "&value=1", "GET");
        assertEquals("ok", serverResponse.getJSONObject("vote").getString("result"));

        JSONObject retrieveSubmission = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&id="+id, "GET");
        assertEquals(1, getRatingFromJson(retrieveSubmission));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }


    @Test
    public void serverRespondsWithOkIfSuccessAndRatingIsSame() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie=" + cookie + "&id=" + id + "&value=0", "GET");
        assertEquals("ok", serverResponse.getJSONObject("vote").getString("result"));

        JSONObject retrieveSubmission = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&id="+id, "GET");
        assertEquals(0, getRatingFromJson(retrieveSubmission));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }

    @Test
    public void serverRespondsWithOkIfSuccessAndDecreaseRating() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=voteTest&password=password&email=voteTestEmail", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=voteTest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject addSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=voteTestSub&category=category&location=location&image=image", "POST");
        String id = getIdFromJson(addSubmission);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/vote?cookie=" + cookie + "&id=" + id + "&value=-1", "GET");
        assertEquals("ok", serverResponse.getJSONObject("vote").getString("result"));

        JSONObject retrieveSubmission = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=1&id="+id, "GET");
        assertEquals(-1, getRatingFromJson(retrieveSubmission));

        establishConnectionAndReturnJsonResponse("/delete/user?name=voteTest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=voteTestSub", "GET");

    }

}
