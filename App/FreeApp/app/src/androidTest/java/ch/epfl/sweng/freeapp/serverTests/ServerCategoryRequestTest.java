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

public class ServerCategoryRequestTest {
    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private NetworkProvider networkProvider = new DefaultNetworkProvider();
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private final static String COOKIE = "BEY4L9lVSlA0hHQQ1ClTXYVUn5xwcr0BfYSKc7sw0Y54XYzWObTAsJ6PHQWPQVzO";

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


    @Test
    public void serverRespondsWithFailureIfNoCookieParameter() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve"));
        assertEquals("cookie", getReasonFromJson(serverResponse, "retrieve"));
    }


    @Test
    public void serverRespondsWithFailureIfBadCookieParameter() throws CommunicationLayerException, JSONException {
        //Delete cookie to make sure they are not in db
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=cookie", "GET");

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie=cookie&flag=4", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve"));
        assertEquals("session", getReasonFromJson(serverResponse, "retrieve"));
    }

    @Test
    public void serverRespondsWithFailureIfNoCategoryParameter() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=categorytest&password=password&email=categorytest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=categorytest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=4", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve category"));
        assertEquals("no category", getReasonFromJson(serverResponse, "retrieve category"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie="+cookie, "GET");
    }


    @Test
    public void serverRespondsWithFailureIfEmptyOrNonExistingCategory() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        //To be sure that there is no such category.
        establishConnectionAndReturnJsonResponse("/delete/category?category=nocategory", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=categorytest&password=password&email=categorytest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=categorytest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?cookie="+cookie+"&flag=4&category=nocategory", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve category"));
        //Empty or non existing category
        assertEquals("empty category", getReasonFromJson(serverResponse, "retrieve category"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");
    }


    @Test
    public void serverRespondsWithJSONArrayOfLengthOne() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/category?category=testcategory", "GET");
        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=categorytest&password=password&email=categorytest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=categorytest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        //We add the submission
        establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=categorytest&category=testcategory&location=location&image=image", "POST");

        JSONArray serverResponse = establishConnectionAndReturnJsonResponseAsArray("/retrieve?cookie=" + cookie + "&flag=4&category=testcategory", "GET");

        JSONObject submissionRetrieved = new JSONObject(serverResponse.get(0).toString());

        assertEquals(1, serverResponse.length());
        assertEquals("categorytest", submissionRetrieved.getString("name"));
        assertEquals("image", submissionRetrieved.getString("image"));
        assertEquals(0, submissionRetrieved.getInt("rating"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/submission?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");

    }


    @Test
    public void serverRespondsWithJSONArrayOfLengthTwo() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/category?category=testarraylength", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=categorytest&password=password&email=categorytest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=categorytest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        JSONObject firstSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=testarraylengthfirst&category=testarraylength&location=location&image=image", "POST");
        JSONObject secondSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie=" + cookie + "&name=testarraylengthsecond&category=testarraylength&location=location&image=image", "POST");

        JSONArray serverResponse = establishConnectionAndReturnJsonResponseAsArray("/retrieve?cookie=" + cookie + "&flag=4&category=testarraylength", "GET");
        assertEquals(2, serverResponse.length());

        JSONObject firstSubmissionRetrieved = new JSONObject(serverResponse.get(0).toString());
        JSONObject secondSubmissionRetrieved = new JSONObject(serverResponse.get(1).toString());


        // Cannot test names because we don't know if get(0) really return the first submission or the second
        assertEquals("image", firstSubmissionRetrieved.getString("image"));

        assertEquals("image", secondSubmissionRetrieved.getString("image"));

        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/category?category=testarraylength", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");


    }

    @Test
    public void serverRespondsWithJSONArrayOfLengthTwentyIfMaxNumberOfSubmissionsReached() throws CommunicationLayerException, JSONException {
        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/category?category=testarraylength", "GET");

        establishConnectionAndReturnJsonResponse("/register?user=categorytest&password=password&email=categorytest@test.ch", "GET");
        JSONObject loginUser = establishConnectionAndReturnJsonResponse("/login?user=categorytest&password=password", "GET");
        String cookie = getCookieFromJson(loginUser);

        for(int i= 0; i < 20; i++) {
            establishConnectionAndReturnJsonResponse("/submission?cookie="+cookie+"&name="+i+"&category=testarraylength&location=location&image=image", "POST");

        }
        JSONArray serverResponse = establishConnectionAndReturnJsonResponseAsArray("/retrieve?cookie=" + cookie + "&flag=4&category=testarraylength", "GET");

        assertEquals(20, serverResponse.length());


        establishConnectionAndReturnJsonResponse("/delete/user?name=categorytest", "GET");
        establishConnectionAndReturnJsonResponse("/delete/category?category=testarraylength", "GET");
        establishConnectionAndReturnJsonResponse("/delete/session?cookie=" + cookie, "GET");
    }

}
