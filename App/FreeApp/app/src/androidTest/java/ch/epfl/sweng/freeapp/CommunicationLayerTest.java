package ch.epfl.sweng.freeapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import ch.epfl.sweng.freeapp.communication.CommunicationLayer;
import ch.epfl.sweng.freeapp.communication.CommunicationLayerException;
import ch.epfl.sweng.freeapp.communication.DefaultNetworkProvider;
import ch.epfl.sweng.freeapp.communication.NetworkProvider;
import ch.epfl.sweng.freeapp.communication.ResponseStatus;
import ch.epfl.sweng.freeapp.loginAndRegistration.LogInInfo;
import ch.epfl.sweng.freeapp.loginAndRegistration.RegistrationInfo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class CommunicationLayerTest {
    private static final int ASCII_SPACE = 0x20;
    private static String JSON_RESPONSE_USERNAME = buildJson("register", "user").toString();
    private static String JSON_RESPONSE_EMAIL = buildJson("register", "email").toString();
    private static String JSON_RESPONSE_PASSWORD = buildJson("register", "password").toString();
    private static String JSON_RESPONSE_GOOD = buildOkJSON("ok").toString();
    private static String JSON_LOG_IN_GOOD = buildSuccessfulLogInJSON().toString();
    private static String JSON_LOG_IN_USERNAME = buildJson("login", "user").toString();
    private static String JSON_LOG_IN_PASSWORD = buildJson("login", "password").toString();
    private static String JSON_CREATE_SUBMISSION_OK = buildOKSubmission().toString();
    private CommunicationLayer communicationLayer;
    private CommunicationLayer communicationLayerOnLine = new CommunicationLayer(new DefaultNetworkProvider());
    private NetworkProvider networkProvider;
    private HttpURLConnection connection;
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    private Calendar current = Calendar.getInstance();
    private Submission.Builder builder = new Submission.Builder();
    private int latitude = 45;
    private int longitude = -45;

    private static JSONObject buildJson(String testType, String reason) {
        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try {
            inner.put("status", "failure");
            inner.put("reason", reason);
            outerObject.put(testType, inner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outerObject;
    }

    private static JSONObject buildOkJSON(String status) {
        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try {
            inner.put("status", status);
            outerObject.put("register", inner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outerObject;
    }

    private static JSONObject buildOKSubmission() {

        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try {
            inner.put("status", "ok");
            outerObject.put("submission", inner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outerObject;
    }

    private static JSONObject buildSuccessfulLogInJSON() {
        JSONObject outerObject = new JSONObject();
        JSONObject inner = new JSONObject();
        try {
            inner.put("status", "ok");
            inner.put("cookie", "TXeh4vQVdNOccioCdM6ZweQH5QK5T1j3pIwhdZkn9dSfqHiRSXScqn0TeAq3A4CE");
            outerObject.put("login", inner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outerObject;
    }

    @Before
    public void setUp() throws Exception {

        startTime.set(Calendar.HOUR_OF_DAY, 17);
        endTime.set(Calendar.HOUR_OF_DAY, 18);

        builder.name("Croissant ");
        builder.description("Good Food");
        builder.category(SubmissionCategory.Food);
        builder.location("EPFL ecublens 1203");
        builder.image("RUBBISH IMAGE");
        builder.keywords("Food BREAD FREE");

        builder.startOfEvent(startTime);
        builder.endOfEvent(endTime);
        builder.submitted(current);
        builder.latitude(latitude);
        builder.longitude(longitude);

        this.connection = Mockito.mock(HttpURLConnection.class);
        this.networkProvider = Mockito.mock(NetworkProvider.class);
        Mockito.doReturn(connection).when(networkProvider).getConnection(Mockito.any(URL.class));
        this.communicationLayer = new CommunicationLayer(networkProvider);
    }


    private void configureCrash(int status) throws IOException {
        InputStream dataStream = Mockito.mock(InputStream.class);
        Mockito.when(dataStream.read())
                .thenReturn(ASCII_SPACE, ASCII_SPACE, ASCII_SPACE, ASCII_SPACE)
                .thenThrow(new IOException());
        Mockito.doReturn(status).when(connection).getResponseCode();
        Mockito.doReturn(dataStream).when(connection).getInputStream();
    }

    private void configureResponse(String content, int status) throws IOException {
        InputStream dataStream = new ByteArrayInputStream(content.getBytes());
        Mockito.doReturn(dataStream).when(connection).getInputStream();
        Mockito.doReturn(status).when(connection).getResponseCode();
    }

    @Test
    public void testResponseOkForCreateSubmissionOnline() throws CommunicationLayerException {


        ResponseStatus status = communicationLayerOnLine.sendAddSubmissionRequest(builder.build());
        assertEquals(ResponseStatus.OK, status);


    }

    @Test
    public void testCreateSubmission() throws CommunicationLayerException, IOException {
        configureResponse(JSON_CREATE_SUBMISSION_OK, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.OK, communicationLayer.sendAddSubmissionRequest(builder.build()));

    }

    @Test
    public void testLogInSuccessful() throws CommunicationLayerException, IOException {
        configureResponse(JSON_LOG_IN_GOOD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.OK, communicationLayer.sendLogInInfo(new LogInInfo("test", "test")));
    }

    @Test
    public void testLogInUserProblem() throws CommunicationLayerException, IOException {
        configureResponse(JSON_LOG_IN_USERNAME, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.USERNAME, communicationLayer.sendLogInInfo(new LogInInfo("test", "test")));
    }

    @Test
    public void testLogInPasswordProblem() throws CommunicationLayerException, IOException {
        configureResponse(JSON_LOG_IN_PASSWORD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.PASSWORD, communicationLayer.sendLogInInfo(new LogInInfo("test", "test")));
    }

    @Test
    public void testCorrectDataSuccessRegister() throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_GOOD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.OK, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }

    @Test
    public void testCorrectDataFailureUsername() throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_USERNAME, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.USERNAME, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }

    @Test
    public void testCorrectDataFailureEmail() throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_EMAIL, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.EMAIL, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }

    @Test
    public void testCorrectDataFailurePassword() throws IOException, CommunicationLayerException {
        configureResponse(JSON_RESPONSE_PASSWORD, HttpURLConnection.HTTP_OK);
        assertEquals(ResponseStatus.PASSWORD, communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test")));
    }

    @Test
    public void testInvalidJson() throws IOException {
        try {
            configureResponse("this is rubbish JSON", HttpURLConnection.HTTP_OK);
            communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test"));
            fail("exception wasn't thrown");
        } catch (CommunicationLayerException e) {
            //PERFECT exception thrown
        }
    }

    /**
     * Test that communication throws CommunicationLayerException  when connection is broken
     */
    @Test
    public void testResponseConnectionBroken() throws IOException {
        configureCrash(HttpURLConnection.HTTP_OK);
        try {
            communicationLayer.sendRegistrationInfo(new RegistrationInfo("test", "test", "test"));
            fail("Did not raise QuizClientException");
        } catch (CommunicationLayerException e) {
            // PERFECT
        }
    }

}
