package ch.epfl.sweng.freeapp;


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

    private String getStatusFromJson(JSONObject serverResponse, String option) throws JSONException {
        return serverResponse.getJSONObject(option).getString("status");
    }


    private String getReasonFromJson(JSONObject serverResponse, String option) throws JSONException {
        return serverResponse.getJSONObject(option).getString("reason");
    }


    @Test
    public void serverRespondsWithFailureIfNoCategoryParameter() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?flag=4", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve category"));
        assertEquals("no category", getReasonFromJson(serverResponse, "retrieve category"));
    }


    @Test
    public void serverRespondsWithFailureIfEmptyOrNonExistingCategory() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?flag=4&category=nocategory", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse, "retrieve category"));
        //Empty or non existing category
        assertEquals("empty category", getReasonFromJson(serverResponse, "retrieve category"));
    }

    @Test
    public void serverRespondsWithJSONArrayOfLengthOne() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?flag=4&category=testcategory", "GET");
        assertEquals("testname", serverResponse.getString("name"));
        assertEquals("testcategory", serverResponse.getString("category"));
        assertEquals("testdescription", serverResponse.getString("description"));
        assertEquals("testlocation", serverResponse.getString("location"));
    }


    @Test
    public void serverRespondsWithJSONArrayOfLengthTwo() throws CommunicationLayerException, JSONException {
        JSONObject firstSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie="+COOKIE+"name=name&category=testarraylength&location=location&image=image", "POST");
        JSONObject secondSubmission = establishConnectionAndReturnJsonResponse("/submission?cookie="+COOKIE+"name=name&category=testarraylength&location=location&image=image", "POST");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?flag=4&category=testarraylength", "GET");
        JSONArray arrayResponse = new JSONArray(serverResponse.toString());
        assertEquals(2, arrayResponse.length());

        JSONObject firstSubmissionRetrieved = (JSONObject) arrayResponse.get(1);
        JSONObject secondSubmissionRetrieved = (JSONObject) arrayResponse.get(2);

        assertEquals("name", firstSubmissionRetrieved.getString("name"));
        assertEquals("testarraylength", firstSubmissionRetrieved.getString("category"));
        assertEquals("location", firstSubmissionRetrieved.getString("location"));
        assertEquals("image", firstSubmissionRetrieved.getString("image"));

        assertEquals("name", secondSubmissionRetrieved.getString("name"));
        assertEquals("testarraylength", secondSubmissionRetrieved.getString("category"));
        assertEquals("location", secondSubmissionRetrieved.getString("location"));
        assertEquals("image", secondSubmissionRetrieved.getString("image"));

    }

    @Test
    public void serverRespondsWithJSONArrayOfLenghtFiveIfMaxNumberOfSubmissionsReached() throws CommunicationLayerException, JSONException {
        // Change FOOD
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/retrieve?flag=4&category=FOOD", "GET");
        JSONArray arrayResponse = new JSONArray(serverResponse.toString());

        assertEquals(5, arrayResponse.length());
    }

}
