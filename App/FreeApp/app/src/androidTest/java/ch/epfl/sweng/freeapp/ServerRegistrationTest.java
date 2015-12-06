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

import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.communication.NetworkProvider;

import static junit.framework.Assert.assertEquals;

public class ServerRegistrationTest {
    private static final String SERVER_URL = "http://sweng-wiinotfit.appspot.com";
    private final static int HTTP_SUCCESS_START = 200;
    private final static int HTTP_SUCCESS_END = 299;
    private NetworkProvider networkProvider = new DefaultNetworkProvider();

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
    public void testRegisterServerRespondsWithInvalidStatusIfNoParameters() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    @Test
    public void testRegisterServerRespondsWithInvalidStatusIfOnlyUserName() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?username=username", "GET");
        assertEquals(getStatusFromJson(serverResponse), "invalid");
    }

    @Test
    public void testRegisterServerRespondsWithInvalidStatusIfOnlyPassword() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?password=password", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }


    @Test
    public void testRegisterServerRespondsWithInvalidStatusIfOnlyEmail() throws CommunicationLayerException, JSONException {
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?email=email", "GET");
        assertEquals("invalid", getStatusFromJson(serverResponse));
    }

    @Test
    public void testRegisterServerRespondsWithOkStatus() throws CommunicationLayerException, JSONException {
        //We delete the user if it already exists, so that we can run the test more than once
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=test", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?user=test&password=test&email=test@test.com", "GET");
        assertEquals("ok", getStatusFromJson(serverResponse));
        //Delete user once again so that it is no more in db
        JSONObject deleteUserAgain = establishConnectionAndReturnJsonResponse("/delete/user?name=test", "GET");
    }


    @Test
    public void testRegisterServerRespondsWithFailureReasonEmail() throws CommunicationLayerException, JSONException {
        //We create a user with the same email as the next one, in order to have the same email in the database
        JSONObject createUserWithSameEmail = establishConnectionAndReturnJsonResponse("/register?user=test1&password=password&email=failtest@email.com", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?user=test2&password=test&email=failtest@email.com", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("email", getReasonFromJson(serverResponse));
        //Delete user so that it is no more in db
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=test1", "GET");
    }

    @Test
    public void testRegisterServerRespondsWithFailureReasonUserName() throws CommunicationLayerException, JSONException {
        //We create a user with the same username as the next one, in order to have the same username in the database
        JSONObject createUserWithSameUserName = establishConnectionAndReturnJsonResponse("/register?user=failtest&password=test&email=notused@email.com", "GET");
        JSONObject serverResponse = establishConnectionAndReturnJsonResponse("/register?user=failtest&password=abc&email=anothertest@test.com", "GET");
        assertEquals("failure", getStatusFromJson(serverResponse));
        assertEquals("username", getReasonFromJson(serverResponse));
        //Delete user so that it is no more in db
        JSONObject deleteUser = establishConnectionAndReturnJsonResponse("/delete/user?name=failtest", "GET");
    }

}
