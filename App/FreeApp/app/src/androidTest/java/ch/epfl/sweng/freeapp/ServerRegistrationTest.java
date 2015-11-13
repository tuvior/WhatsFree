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

public class ServerRegistrationTest {
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

    private String getStatusFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("register").getString("status");
    }


    private String getReasonFromJson(JSONObject serverResponse) throws JSONException {
        return serverResponse.getJSONObject("register").getString("reason");
    }


    @Test
    public void testRegisterServerRespondWithInvalidStatusIfNoParameters() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    @Test
    public void testRegisterServerRespondWithInvalidStatusIfOnlyUserName() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?username=username", "GET");
        assertEquals(getStatusFromJson(serverResponse), "invalid");
    }

    @Test
    public void testRegisterServerRespondWithInvalidStatusIfOnlyPassword() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?password=password", "GET");
        assertEquals("invalid",getStatusFromJson(serverResponse));
    }


    @Test
    public void testRegisterServerRespondWithInvalidStatusIfOnlyEmail() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?email=email", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    //Could do more test like if only password missing and so on...

    //READ: If run more than once, test will fail because we will have an already existing user stored in the database
    @Test
    public void testRegisterServerRespondWithOkStatus() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?user=test&password=test&email=test@test.com", "GET");
        assertEquals("ok", getStatusFromJson(serverResponse));
    }


    @Test
    public void testRegisterServerRespondWithFailureReasonEmail() throws CommunicationLayerException, JSONException {
        //We create a user with the same email as the next one, in order to have the same email in the database
        JSONObject createUserWithSameEmail = establishConnectionAndReturnJsonResponse("/register?user=test1&password=password&email=failtest@email.com", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?user=test2&password=test&email=failtest@email.com", "GET");
        assertEquals("failure",getStatusFromJson(serverResponse));
        assertEquals("email", getReasonFromJson(serverResponse));
    }

    @Test
    public void testRegisterServerRespondWithFailureReasonUserName() throws CommunicationLayerException, JSONException {
        //We create a user with the same username as the next one, in order to have the same username in the database
        JSONObject createUserWithSameUserName = establishConnectionAndReturnJsonResponse("/register?user=failtest&password=test&email=notused@email.com", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?user=failtest&password=abc&email=anothertest@test.com", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("username", getReasonFromJson(serverResponse));
    }

}
